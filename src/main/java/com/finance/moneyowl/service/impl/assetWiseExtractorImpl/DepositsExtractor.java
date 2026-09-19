package com.finance.moneyowl.service.impl.assetWiseExtractorImpl;

import com.finance.moneyowl.generatedmodels.AccountData;
import com.finance.moneyowl.generatedmodels.DepositAccount;
import com.finance.moneyowl.generatedmodels.DepositProfile;
import com.finance.moneyowl.generatedmodels.DepositSummary;
import com.finance.moneyowl.mapper.assetMapper.DepositMapper;
import com.finance.moneyowl.model.AssetAccount;
import com.finance.moneyowl.model.UserHoldings;
import com.finance.moneyowl.model.UserPortfolio;
import com.finance.moneyowl.repository.MongoHoldingRepository;
import com.finance.moneyowl.service.interfaces.assetWiseExtractor.FiTypeExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.finance.moneyowl.utils.Constants.DEPOSIT;


@Component
@RequiredArgsConstructor
public class DepositsExtractor implements FiTypeExtractor {
    private final DepositMapper depositMapper;
    private final MongoHoldingRepository mongoHoldingRepository;

    @Override
    public String getFiType() {
        return DEPOSIT;
    }

    @Override
    public UserPortfolio extract(AccountData accountData, Long userId, AssetAccount assetAccount, UserPortfolio portfolio) {
        if (!isValidAccount(accountData)) {
            return portfolio;
        }
        DepositAccount account = (DepositAccount) accountData.getData().getAccount();
        if (account == null || account.getSummary() == null) {
            return portfolio;
        }
        DepositSummary summary = account.getSummary();
        DepositProfile profile = account.getProfile();
        UserHoldings userHolding = depositMapper.toDepositHolding(account, summary, profile, userId, getFiType());
        mongoHoldingRepository.save(userHolding);
        return holdingToPortfolio(userHolding, portfolio);
    }

    @Override
    public UserPortfolio extractPortfolio(AssetAccount account) {
        return depositMapper.toPortfolio(account);
    }

    private UserPortfolio holdingToPortfolio(UserHoldings userHolding, UserPortfolio portfolio) {
        BigDecimal existingCurrentValue = portfolio.getCurrentAssetValue() != null ? portfolio.getCurrentAssetValue() : BigDecimal.ZERO;
        BigDecimal holdingCurrentValue = userHolding.getCurrentValue() != null ? userHolding.getCurrentValue() : BigDecimal.ZERO;
        portfolio.setCurrentAssetValue(existingCurrentValue.add(holdingCurrentValue));
        return portfolio;
    }

    private boolean isValidAccount(AccountData accountData) {
        if (accountData == null || accountData.getData() == null || accountData.getData().getAccount() == null) {
            return false;
        }
        String fiStatus = accountData.getFistatus();
        if (fiStatus == null) {
            return false;
        }
        return !fiStatus.equalsIgnoreCase("DENIED") && !fiStatus.equalsIgnoreCase("FAILED") && !fiStatus.equalsIgnoreCase("TIMEOUT");
    }
}