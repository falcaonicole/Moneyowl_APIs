package com.finance.moneyowl.service.impl.assetWiseExtractorImpl;

import com.finance.moneyowl.generatedmodels.*;
import com.finance.moneyowl.mapper.assetMapper.EquityMapper;
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

@Component
public class EquityExtractor implements FiTypeExtractor {

    @Autowired
    private EquityMapper equityMapper;

    @Autowired
    private UserPortfolioMongoRepo userPortfolioMongoRepo;

    @Autowired
    private MongoHoldingRepository mongoHoldingRepository;

    @Override
    public String getFiType() {
        return "equities";
    }

    @Override
    public UserPortfolio extract(AccountData accountData, Long userId, AssetAccount assetAccount, UserPortfolio equityPortfolio) {
        if (accountData == null
                || accountData.getData() == null
                || accountData.getData().getAccount() == null || accountData.getFistatus().equalsIgnoreCase("DENIED")
                || accountData.getFistatus().equalsIgnoreCase("FAILED")) {
            return equityPortfolio;
        }
        EquityAccount account = (EquityAccount) accountData.getData().getAccount();
        EquityProfile profile = account.getProfile();
        //Structure is as account.getSummary().getInvestment().getHoldings().getHolding().get(0)
        List<EquityHoldings> rawHoldings = Optional.ofNullable(account.getSummary())
                .map(EquitySummary::getInvestment)
                .map(EquityInvestment::getHoldings)
                .map(EquityHoldingsList::getHolding)
                .orElse(List.of());

        List<UserHoldings> userHoldings = rawHoldings.stream()
                .map(h -> equityMapper.toEquityHoldings(h, userId, getFiType(), profile))
                .toList();
        if (!userHoldings.isEmpty()) {
            mongoHoldingRepository.saveAll(userHoldings);
        }
        return holdingsToPortfolio(userHoldings, equityPortfolio);
    }

    @Override
    public UserPortfolio extractPortfolio(AssetAccount account) {
        return equityMapper.toPortfolio(account);
    }

    private UserPortfolio holdingsToPortfolio(List<UserHoldings> userHoldings, UserPortfolio oldEquityPortfolio) {
        BigDecimal currentValue = oldEquityPortfolio.getCurrentAssetValue()
                .add(userHoldings.stream()
                        .map(UserHoldings::getCurrentValue)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        BigDecimal investedValue = oldEquityPortfolio.getTotalInvestedValue()
                .add(userHoldings.stream()
                        .map(UserHoldings::getTotalInvestedValue)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        oldEquityPortfolio.setCurrentAssetValue(currentValue);
        oldEquityPortfolio.setTotalInvestedValue(investedValue);
        return oldEquityPortfolio;
    }
}
