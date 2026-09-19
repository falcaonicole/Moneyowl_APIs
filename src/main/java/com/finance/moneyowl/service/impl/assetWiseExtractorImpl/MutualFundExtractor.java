package com.finance.moneyowl.service.impl.assetWiseExtractorImpl;

import com.finance.moneyowl.generatedmodels.*;
import com.finance.moneyowl.mapper.assetMapper.MFMapper;
import com.finance.moneyowl.model.AssetAccount;
import com.finance.moneyowl.model.UserHoldings;
import com.finance.moneyowl.model.UserPortfolio;
import com.finance.moneyowl.repository.MongoHoldingRepository;
import com.finance.moneyowl.repository.UserPortfolioMongoRepo;
import com.finance.moneyowl.service.interfaces.assetWiseExtractor.FiTypeExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.finance.moneyowl.utils.Constants.MUTUAL_FUNDS;

@Component
public class MutualFundExtractor implements FiTypeExtractor {

    @Autowired
    private MFMapper mutualFundsMapper;

    @Autowired
    private UserPortfolioMongoRepo userPortfolioMongoRepo;

    @Autowired
    private MongoHoldingRepository mongoHoldingRepository;

    @Override
    public String getFiType() {
        return MUTUAL_FUNDS;
    }

    @Override
    public UserPortfolio extract(AccountData accountData, Long userId, AssetAccount assetAccount, UserPortfolio portfolio) {
        if (accountData == null
                || accountData.getData() == null
                || accountData.getData().getAccount() == null || accountData.getFistatus().equalsIgnoreCase("DENIED")
                || accountData.getFistatus().equalsIgnoreCase("FAILED")) {
            return portfolio;
        }
        MutualFundsAccount account = (MutualFundsAccount) accountData.getData().getAccount();

        MutualFundsProfile profile = account.getProfile();
        //Structure is as account.getSummary().getInvestment().getHoldings().getHolding().get(0)
        List<MutualFundsHoldings> rawHoldings = Optional.ofNullable(account.getSummary())
                .map(MutualFundsSummary::getInvestment)
                .map(MutualFundsInvestment::getHoldings)
                .map(MutualFundsHoldingsList::getHolding)
                .orElse(List.of());

        List<UserHoldings> userHoldings = rawHoldings.stream()
                .map(h -> mutualFundsMapper.toMutualFundsHoldings(h, userId, getFiType(), profile))
                .toList();
        if (!userHoldings.isEmpty()) {
            mongoHoldingRepository.saveAll(userHoldings);
        }
        BigDecimal investedValue = parseSafe(account.getSummary().getCostValue());
        return holdingsToPortfolio(userHoldings, portfolio, investedValue);
    }

    @Override
    public UserPortfolio extractPortfolio(AssetAccount account) {
        return mutualFundsMapper.toPortfolio(account);
    }

    private UserPortfolio holdingsToPortfolio(List<UserHoldings> userHoldings, UserPortfolio oldPortfolio, BigDecimal investedValue) {
        BigDecimal currentValue = oldPortfolio.getCurrentAssetValue()
                .add(userHoldings.stream()
                        .map(UserHoldings::getCurrentValue)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        oldPortfolio.setCurrentAssetValue(currentValue);

        BigDecimal totalInvestedValue = oldPortfolio.getTotalInvestedValue()
                .add(investedValue);

        oldPortfolio.setTotalInvestedValue(totalInvestedValue);
        return oldPortfolio;
    }

    private BigDecimal parseSafe(String value) {
        if (value == null || value.trim().isEmpty() || "NA".equalsIgnoreCase(value.trim())) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
