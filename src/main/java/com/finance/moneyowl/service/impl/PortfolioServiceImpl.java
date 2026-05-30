package com.finance.moneyowl.service.impl;

import com.finance.moneyowl.generatedmodels.*;
import com.finance.moneyowl.model.UserPortfolioModel;
import com.finance.moneyowl.service.interfaces.MongoService;
import com.finance.moneyowl.service.interfaces.PortfolioService;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

@Service
@Slf4j
public class PortfolioServiceImpl implements PortfolioService {

    @Autowired
    private MongoService mongoService;

    @Override
    public HomeDashBoardResponse getHomeDashBoardResponse(Long userId) {
        UserPortfolioModel portfolio = mongoService.getUserPortfolio(userId);
        List<AssetValuations> assetValuationsList = new ArrayList<>();

        if (portfolio == null) {
            return HomeDashBoardResponse.builder().assets(assetValuationsList).build();
        }

        // 1. Equities
        addValuationIfPresent("EQUITIES",
                portfolio.getEquities() != null ? portfolio.getEquities().getAsset() : null,
                account -> {
                    EquityAccount acc = (EquityAccount) account.getData().getAccount();
                    return new AssetSummary(acc.getSummary().getCurrentValue(), acc.getSummary().getInvestmentValue());
                }
        ).ifPresent(assetValuationsList::add);

        // 2. Deposits
        addValuationIfPresent("DEPOSITS",
                portfolio.getDeposits() != null ? portfolio.getDeposits().getAsset() : null,
                account -> {
                    DepositAccount acc = (DepositAccount) account.getData().getAccount();
                    return new AssetSummary(acc.getSummary().getCurrentBalance(), "0");
                }
        ).ifPresent(assetValuationsList::add);

        // 3. Mutual Funds
        addValuationIfPresent("MUTUAL_FUNDS",
                portfolio.getMutualFunds() != null ? portfolio.getMutualFunds().getAsset() : null,
                account -> {
                    MutualFundsAccount acc = (MutualFundsAccount) account.getData().getAccount();
                    return new AssetSummary(acc.getSummary().getCurrentValue(), acc.getSummary().getCostValue());
                }
        ).ifPresent(assetValuationsList::add);

        // 4. Add your 4th FI Type here following the exact same pattern...

        return HomeDashBoardResponse.builder()
                .assets(assetValuationsList)
                .build();
    }

    @Override
    public EquitiesDashBoardResponse getEquitiesDashBoardResponse(Long userId) {
        log.info("Fetching Equities Dashboard Data for userId: {}", userId);

        UserPortfolioModel portfolio = mongoService.getUserPortfolio(userId);

        // Fail-safe return if no equities data exists
        if (portfolio == null || portfolio.getEquities() == null || portfolio.getEquities().getAsset() == null) {
            log.warn("No equities portfolio found for userId: {}. Returning empty dashboard.", userId);
            return buildEmptyResponse();
        }

        BigDecimal totalCurrentValue = BigDecimal.ZERO;
        Set<String> uniqueBrokers = new HashSet<>();
        Set<String> uniqueIsins = new HashSet<>();
        List<Holding> allHoldings = new ArrayList<>();

        for (AccountData asset : portfolio.getEquities().getAsset()) {
            if (asset.getData() == null || asset.getData().getAccount() == null) {
                continue; // Skip malformed asset entries
            }

            EquityAccount account = (EquityAccount) asset.getData().getAccount();

            // 1. Accumulate Total Portfolio Value
            totalCurrentValue = totalCurrentValue.add(extractCurrentValue(account));

            // 2. Track Unique Brokers
            extractBrokerName(account).ifPresent(uniqueBrokers::add);

            // 3. Extract, Transform, and Accumulate Holdings
            List<Holding> accountHoldings = extractAndMapHoldings(account);
            allHoldings.addAll(accountHoldings);

            // 4. Track unique stocks (using ISIN as the unique identifier)
            accountHoldings.forEach(holding -> {
                if (StringUtils.isNotBlank(holding.getIsin())) {
                    uniqueIsins.add(holding.getIsin());
                }
            });
        }

        log.info("Successfully aggregated Equities data for userId: {}. Total Brokers: {}, Total Stocks: {}",
                userId, uniqueBrokers.size(), uniqueIsins.size());

        return EquitiesDashBoardResponse.builder()
                .totalValue(totalCurrentValue)
                .noOfBrokers(uniqueBrokers.size())
                .noOfStocks(uniqueIsins.size())
                .holdings(allHoldings)
                .build();
    }

    
    /**
     * Reusable method to calculate totals and build AssetValuations dynamically
     */
    private Optional<AssetValuations> addValuationIfPresent(
            String fiType,
            List<AccountData> accounts,
            Function<AccountData, AssetSummary> summaryExtractor) {

        if (accounts == null || accounts.isEmpty()) {
            return Optional.empty();
        }

        BigDecimal totalCurrent = BigDecimal.ZERO;
        BigDecimal totalInvested = BigDecimal.ZERO;

        for (AccountData account : accounts) {
            if (account.getData() != null && account.getData().getAccount() != null) {
                AssetSummary summary = summaryExtractor.apply(account);

                totalCurrent = totalCurrent.add(safeParseBigDecimal(summary.currentValue()));
                totalInvested = totalInvested.add(safeParseBigDecimal(summary.investedValue()));
            }
        }

        return Optional.of(AssetValuations.builder()
                .fiType(fiType)
                .currentValue(totalCurrent)
                .investedValue(totalInvested)
                .build());
    }

    private BigDecimal safeParseBigDecimal(String val) {
        if (val == null || val.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(val);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private EquitiesDashBoardResponse buildEmptyResponse() {
        return EquitiesDashBoardResponse.builder()
                .totalValue(BigDecimal.ZERO)
                .noOfBrokers(0)
                .noOfStocks(0)
                .holdings(Collections.emptyList())
                .build();
    }

    private BigDecimal extractCurrentValue(EquityAccount account) {
        return Optional.ofNullable(account.getSummary())
                .map(EquitySummary::getCurrentValue)
                .map(this::safeParseBigDecimal)
                .orElse(BigDecimal.ZERO);
    }

    private Optional<String> extractBrokerName(EquityAccount account) {
        try {
            if (account.getProfile() != null &&
                    account.getProfile().getHolders() != null &&
                    account.getProfile().getHolders().getHolder() != null &&
                    !account.getProfile().getHolders().getHolder().isEmpty()) {

                String brokerName = account.getProfile().getHolders().getHolder().get(0).getBrokerName();
                return Optional.ofNullable(brokerName)
                        .map(String::strip)
                        .filter(s -> !s.isBlank());
            }
        } catch (Exception e) {
            log.error("Error extracting broker name: {}", e.getMessage());
        }
        return Optional.empty();
    }

    private List<Holding> extractAndMapHoldings(EquityAccount account) {
        try {
            // Check deep nesting existence
            if (account.getSummary() == null ||
                    account.getSummary().getInvestment() == null ||
                    account.getSummary().getInvestment().getHoldings() == null ||
                    account.getSummary().getInvestment().getHoldings().getHolding() == null) {
                return Collections.emptyList();
            }

            List<EquityHoldings> dbHoldings = account.getSummary().getInvestment().getHoldings().getHolding();
            List<Holding> mappedHoldings = new ArrayList<>(dbHoldings.size());

            for (EquityHoldings dbHolding : dbHoldings) {
                // Calculate Total Amount Invested (Units * Rate)
                BigDecimal units = safeParseBigDecimal(dbHolding.getUnits());
                BigDecimal rate = safeParseBigDecimal(dbHolding.getRate());
                BigDecimal totalInvested = units.multiply(rate);

                // Map DB Object to Response Object
                Holding responseHolding = Holding.builder()
                        .issuerName(dbHolding.getIssuerName())
                        .isin(dbHolding.getIsin())
                        .units(dbHolding.getUnits())
                        .rate(dbHolding.getRate())
                        .lastTradedPrice(dbHolding.getLastTradedPrice())
                        .desc(dbHolding.getDescription()) // DB is 'description', Schema is 'desc'
                        .totalAmountInvested(totalInvested)
                        .build();

                mappedHoldings.add(responseHolding);
            }

            return mappedHoldings;

        } catch (Exception e) {
            log.error("Failed to map holdings for account: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * A simple record to hold extracted string values before conversion
     */
    private record AssetSummary(String currentValue, String investedValue) {
    }
}
