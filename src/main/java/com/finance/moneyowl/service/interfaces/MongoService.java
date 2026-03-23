package com.finance.moneyowl.service.interfaces;

import com.finance.moneyowl.generatedmodels.Account;
import com.finance.moneyowl.generatedmodels.DataRange;
import com.finance.moneyowl.model.AssetAccount;
import com.finance.moneyowl.model.UserPortfolioModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MongoService {

    UserPortfolioModel saveUserPortfolio(UserPortfolioModel userPortfolioModel);

    void saveConsentIdAndExpiryByFiType(Long userId, String consentId, String expiryDate, String fiType);

    void saveIdByFiType(Long userId, String consentId, String fiType);

    void saveDataRangeByFiType(Long userId, DataRange range, String fiType);

    UserPortfolioModel getUserPortfolio(Long userId);

    List<Account> getAccountsByFiType(Long userId, String fiType);

    String getConsentIdByFiType(Long userId, String fiType);

    String getIdByFiType(Long userId, String fiType);

    AssetAccount getAssetAccountByFiType(String fiType, UserPortfolioModel userPortfolioModel);

    DataRange getDataRangeByFiType(Long userId, String fiType);
}
