package com.finance.moneyowl.service.interfaces;

import com.finance.moneyowl.generatedmodels.AccountData;
import com.finance.moneyowl.generatedmodels.DataRange;
import com.finance.moneyowl.model.AssetAccount;
import com.finance.moneyowl.model.UserPortfolioSetuResponseModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MongoService {

    UserPortfolioSetuResponseModel saveUserPortfolio(UserPortfolioSetuResponseModel userPortfolioSetuResponseModel);

    void saveConsentIdAndExpiryByFiType(Long userId, String consentId, String expiryDate, String fiType);

    void saveIdByFiType(Long userId, String consentId, String fiType);

    void saveDataRangeByFiType(Long userId, DataRange range, String fiType);

    UserPortfolioSetuResponseModel getUserPortfolio(Long userId);

    List<AccountData> getAccountsByFiType(Long userId, String fiType);

    String getConsentIdByFiType(Long userId, String fiType);

    String getIdByFiType(Long userId, String fiType);

    AssetAccount getAssetAccountByFiType(String fiType, UserPortfolioSetuResponseModel userPortfolioSetuResponseModel);

    DataRange getDataRangeByFiType(Long userId, String fiType);
}
