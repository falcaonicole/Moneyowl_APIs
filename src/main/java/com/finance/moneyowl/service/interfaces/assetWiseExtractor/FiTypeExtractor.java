package com.finance.moneyowl.service.interfaces.assetWiseExtractor;

import com.finance.moneyowl.generatedmodels.AccountData;
import com.finance.moneyowl.model.AssetAccount;
import com.finance.moneyowl.model.UserPortfolio;

public interface FiTypeExtractor {
    String getFiType();

    UserPortfolio extract(AccountData accountData, Long userId, AssetAccount account, UserPortfolio equityPortfolio);

    UserPortfolio extractPortfolio(AssetAccount account);
}
