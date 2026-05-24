package com.finance.moneyowl.service.impl;

import com.finance.moneyowl.exceptions.MoneyowlApplicationException;
import com.finance.moneyowl.exceptions.ResourceNotFoundException;
import com.finance.moneyowl.generatedmodels.AccountData;
import com.finance.moneyowl.generatedmodels.DataRange;
import com.finance.moneyowl.model.AssetAccount;
import com.finance.moneyowl.model.UserPortfolioModel;
import com.finance.moneyowl.repository.MongoDBRepository;
import com.finance.moneyowl.service.interfaces.MongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.finance.moneyowl.utils.ErrorMessageConstants.LOG_TEMPLATE;
import static com.finance.moneyowl.utils.ErrorMessageConstants.PORTFOLIO_NOT_FOUND;

@Service
@Slf4j
public class MongoServiceImpl implements MongoService {

    @Autowired
    private MongoDBRepository mongoRepository;

    @Override
    public UserPortfolioModel saveUserPortfolio(UserPortfolioModel userPortfolioModel) {
        return mongoRepository.save(userPortfolioModel);
    }

    @Override
    public void saveConsentIdAndExpiryByFiType(Long userId, String consentId, String expiryDate, String fiType) {
        if (portfolioExists(userId)) {
            UserPortfolioModel userPortfolio = getUserPortfolio(userId);
            AssetAccount assetAccount = getAssetAccountByFiType(fiType, userPortfolio);
            assetAccount.setConsentId(consentId);
            if (expiryDate != null) {
                assetAccount.setConsentExpiry(expiryDate);
            }
            saveUserPortfolio(userPortfolio);
        } else {
            UserPortfolioModel model = new UserPortfolioModel();
            model.setUserId(userId);
            createAndSaveConsentIdByFiType(fiType, expiryDate, model, consentId);
        }
    }

    @Override
    public void saveIdByFiType(Long userId, String Id, String fiType) {
        if (portfolioExists(userId)) {
            UserPortfolioModel userPortfolio = getUserPortfolio(userId);
            AssetAccount assetAccount = getAssetAccountByFiType(fiType, userPortfolio);
            assetAccount.setId(Id);
            saveUserPortfolio(userPortfolio);
        } else {
            UserPortfolioModel model = new UserPortfolioModel();
            model.setUserId(userId);
            createAndSaveIdByFiType(fiType, model, Id);
        }
    }

    @Override
    public void saveDataRangeByFiType(Long userId, DataRange range, String fiType) {
        if (portfolioExists(userId)) {
            UserPortfolioModel userPortfolio = getUserPortfolio(userId);
            AssetAccount assetAccount = getAssetAccountByFiType(fiType, userPortfolio);
            assetAccount.setDataRange(range);
            saveUserPortfolio(userPortfolio);
        } else {
            UserPortfolioModel model = new UserPortfolioModel();
            model.setUserId(userId);
            createAndSaveDataRangeByFiType(fiType, model, range);
        }
    }

    @Override
    public UserPortfolioModel getUserPortfolio(Long userId) {
        UserPortfolioModel model;
        model = mongoRepository.findByUserId(userId);
        return model;
    }

    @Override
    public List<AccountData> getAccountsByFiType(Long userId, String fiType) {
        if (portfolioExists(userId)) {
            UserPortfolioModel model = getUserPortfolio(userId);
            return getAccountsByFiType(model, fiType);
        } else {
            log.error(LOG_TEMPLATE, PORTFOLIO_NOT_FOUND, userId);
            throw new ResourceNotFoundException(PORTFOLIO_NOT_FOUND, userId);
        }
    }

    @Override
    public String getConsentIdByFiType(Long userId, String fiType) {
        if (portfolioExists(userId)) {
            UserPortfolioModel model = getUserPortfolio(userId);
            switch (fiType) {
                case "EQUITIES" -> {
                    return model.getEquities().getConsentId();
                }
                default -> throw new MoneyowlApplicationException("Invalid fiType");
            }
        } else {
            log.error(LOG_TEMPLATE, PORTFOLIO_NOT_FOUND, userId);
            throw new ResourceNotFoundException(PORTFOLIO_NOT_FOUND, userId);
        }
    }

    @Override
    public String getIdByFiType(Long userId, String fiType) {
        if (portfolioExists(userId)) {
            UserPortfolioModel model = getUserPortfolio(userId);
            switch (fiType) {
                case "EQUITIES" -> {
                    return model.getEquities().getId();
                }
                default -> throw new MoneyowlApplicationException("Invalid fiType");
            }
        } else {
            log.error(LOG_TEMPLATE, PORTFOLIO_NOT_FOUND, userId);
            throw new ResourceNotFoundException(PORTFOLIO_NOT_FOUND, userId);
        }
    }

    @Override
    public AssetAccount getAssetAccountByFiType(String fiType, UserPortfolioModel userPortfolioModel) {
        switch (fiType.toUpperCase()) {
            case "EQUITIES":
                return userPortfolioModel.getEquities();

            default:
                throw new IllegalArgumentException("Invalid fiType");
        }

        /** For Dynamic FiType
         * UserPortfolioModel portfolio =
         *             userPortfolioRepository.findById(userId)
         *                     .orElseThrow(() -> new RuntimeException("Not found"));
         *
         *     return portfolio.getAssets().get(fiType);*/
    }

    @Override
    public DataRange getDataRangeByFiType(Long userId, String fiType) {
        if (portfolioExists(userId)) {
            UserPortfolioModel model = getUserPortfolio(userId);
            switch (fiType) {
                case "EQUITIES" -> {
                    return model.getEquities().getDataRange();
                }
                default -> throw new MoneyowlApplicationException("Invalid fiType");
            }
        } else {
            log.error(LOG_TEMPLATE, PORTFOLIO_NOT_FOUND, userId);
            throw new ResourceNotFoundException(PORTFOLIO_NOT_FOUND, userId);
        }
    }

    private Boolean portfolioExists(Long userId) {
        return mongoRepository.existsByUserId(userId);
    }

    private void createAndSaveConsentIdByFiType(String fiType, String expiryDate, UserPortfolioModel model, String consentId) {
        switch (fiType.toUpperCase()) {
            case "EQUITIES" -> {
                AssetAccount assetAccount = new AssetAccount();
                assetAccount.setConsentId(consentId);
                if (expiryDate != null) {
                    assetAccount.setConsentExpiry(expiryDate);
                }
                model.setEquities(assetAccount);
            }

            default -> throw new MoneyowlApplicationException("Invalid fiType");
        }
        saveUserPortfolio(model);
    }

    private void createAndSaveIdByFiType(String fiType, UserPortfolioModel model, String Id) {
        switch (fiType.toUpperCase()) {
            case "EQUITIES" -> {
                AssetAccount assetAccount = new AssetAccount();
                assetAccount.setId(Id);
                model.setEquities(assetAccount);
            }

            default -> throw new MoneyowlApplicationException("Invalid fiType");
        }
        saveUserPortfolio(model);
    }

    private void createAndSaveDataRangeByFiType(String fiType, UserPortfolioModel model, DataRange range) {
        switch (fiType.toUpperCase()) {
            case "EQUITIES" -> {
                AssetAccount assetAccount = new AssetAccount();
                assetAccount.setDataRange(range);
                model.setEquities(assetAccount);
            }

            default -> throw new MoneyowlApplicationException("Invalid fiType");
        }
        saveUserPortfolio(model);
    }

    public List<AccountData> getAccountsByFiType(UserPortfolioModel model, String fiType) {
        switch (fiType) {
            case "EQUITIES" -> {
                return model.getEquities().getAsset();
            }
            default -> throw new MoneyowlApplicationException("Invalid fiType");
        }
    }
}
