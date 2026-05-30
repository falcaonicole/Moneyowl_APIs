//package com.finance.moneyowl.service.impl;
//
//import com.finance.moneyowl.exceptions.MoneyowlApplicationException;
//import com.finance.moneyowl.exceptions.ResourceNotFoundException;
//import com.finance.moneyowl.generatedmodels.AccountData;
//import com.finance.moneyowl.generatedmodels.DataRange;
//import com.finance.moneyowl.model.AssetAccount;
//import com.finance.moneyowl.model.UserPortfolioModel;
//import com.finance.moneyowl.repository.MongoDBRepository;
//import com.finance.moneyowl.service.interfaces.MongoService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//import static com.finance.moneyowl.utils.ErrorMessageConstants.LOG_TEMPLATE;
//import static com.finance.moneyowl.utils.ErrorMessageConstants.PORTFOLIO_NOT_FOUND;
//
//@Service
//@Slf4j
//public class MongoServiceImpl implements MongoService {
//
//    @Autowired
//    private MongoDBRepository mongoRepository;
//
//    @Override
//    public UserPortfolioModel saveUserPortfolio(UserPortfolioModel userPortfolioModel) {
//        return mongoRepository.save(userPortfolioModel);
//    }
//
//    @Override
//    public void saveConsentIdAndExpiryByFiType(Long userId, String consentId, String expiryDate, String fiType) {
//        if (portfolioExists(userId)) {
//            UserPortfolioModel userPortfolio = getUserPortfolio(userId);
//            AssetAccount assetAccount = getAssetAccountByFiType(fiType, userPortfolio);
//            assetAccount.setConsentId(consentId);
//            if (expiryDate != null) {
//                assetAccount.setConsentExpiry(expiryDate);
//            }
//            saveUserPortfolio(userPortfolio);
//        } else {
//            UserPortfolioModel model = new UserPortfolioModel();
//            model.setUserId(userId);
//            createAndSaveConsentIdByFiType(fiType, expiryDate, model, consentId);
//        }
//    }
//
//    @Override
//    public void saveIdByFiType(Long userId, String Id, String fiType) {
//        if (portfolioExists(userId)) {
//            UserPortfolioModel userPortfolio = getUserPortfolio(userId);
//            AssetAccount assetAccount = getAssetAccountByFiType(fiType, userPortfolio);
//            assetAccount.setId(Id);
//            saveUserPortfolio(userPortfolio);
//        } else {
//            UserPortfolioModel model = new UserPortfolioModel();
//            model.setUserId(userId);
//            createAndSaveIdByFiType(fiType, model, Id);
//        }
//    }
//
//    @Override
//    public void saveDataRangeByFiType(Long userId, DataRange range, String fiType) {
//        if (portfolioExists(userId)) {
//            UserPortfolioModel userPortfolio = getUserPortfolio(userId);
//            AssetAccount assetAccount = getAssetAccountByFiType(fiType, userPortfolio);
//            assetAccount.setDataRange(range);
//            saveUserPortfolio(userPortfolio);
//        } else {
//            UserPortfolioModel model = new UserPortfolioModel();
//            model.setUserId(userId);
//            createAndSaveDataRangeByFiType(fiType, model, range);
//        }
//    }
//
//    @Override
//    public UserPortfolioModel getUserPortfolio(Long userId) {
//        UserPortfolioModel model;
//        model = mongoRepository.findByUserId(userId);
//        return model;
//    }
//
//    @Override
//    public List<AccountData> getAccountsByFiType(Long userId, String fiType) {
//        if (portfolioExists(userId)) {
//            UserPortfolioModel model = getUserPortfolio(userId);
//            return getAccountsByFiType(model, fiType);
//        } else {
//            log.error(LOG_TEMPLATE, PORTFOLIO_NOT_FOUND, userId);
//            throw new ResourceNotFoundException(PORTFOLIO_NOT_FOUND, userId);
//        }
//    }
//
//    @Override
//    public String getConsentIdByFiType(Long userId, String fiType) {
//        if (portfolioExists(userId)) {
//            UserPortfolioModel model = getUserPortfolio(userId);
//            switch (fiType) {
//                case "EQUITIES":
//                    return model.getEquities().getConsentId();
//                case "DEPOSIT":
//                    return model.getDeposits().getConsentId();
//                case "MUTUAL_FUNDS":
//                    return model.getMutualFunds().getConsentId();
//                case "NPS":
//                    return model.getNps().getConsentId();
//                default:
//                    throw new MoneyowlApplicationException("Invalid fiType");
//            }
//        } else {
//            log.error(LOG_TEMPLATE, PORTFOLIO_NOT_FOUND, userId);
//            throw new ResourceNotFoundException(PORTFOLIO_NOT_FOUND, userId);
//        }
//    }
//
//    @Override
//    public String getIdByFiType(Long userId, String fiType) {
//        if (portfolioExists(userId)) {
//            UserPortfolioModel model = getUserPortfolio(userId);
//            switch (fiType) {
//                case "EQUITIES" -> {
//                    return model.getEquities().getId();
//                }
//                case "DEPOSIT" -> {
//                    return model.getDeposits().getId();
//                }
//                case "MUTUAL_FUNDS" -> {
//                    return model.getMutualFunds().getId();
//                }
//                case "NPS" -> {
//                    return model.getNps().getId();
//                }
//                default -> throw new MoneyowlApplicationException("Invalid fiType");
//            }
//        } else {
//            log.error(LOG_TEMPLATE, PORTFOLIO_NOT_FOUND, userId);
//            throw new ResourceNotFoundException(PORTFOLIO_NOT_FOUND, userId);
//        }
//    }
//
//    @Override
//    public AssetAccount getAssetAccountByFiType(String fiType, UserPortfolioModel userPortfolioModel) {
//        switch (fiType.toUpperCase()) {
//            case "EQUITIES":
//                return userPortfolioModel.getEquities();
//            case "DEPOSIT":
//                return userPortfolioModel.getDeposits();
//            case "MUTUAL_FUNDS":
//                return userPortfolioModel.getMutualFunds();
//            case "NPS":
//                return userPortfolioModel.getNps();
//
//            default:
//                throw new IllegalArgumentException("Invalid fiType");
//        }
//
//        /** For Dynamic FiType
//         * UserPortfolioModel portfolio =
//         *             userPortfolioRepository.findById(userId)
//         *                     .orElseThrow(() -> new RuntimeException("Not found"));
//         *
//         *     return portfolio.getAssets().get(fiType);*/
//    }
//
//    @Override
//    public DataRange getDataRangeByFiType(Long userId, String fiType) {
//        if (portfolioExists(userId)) {
//            UserPortfolioModel model = getUserPortfolio(userId);
//            switch (fiType) {
//                case "EQUITIES":
//                    return model.getEquities().getDataRange();
//                case "DEPOSIT":
//                    return model.getDeposits().getDataRange();
//                case "MUTUAL_FUNDS":
//                    return model.getMutualFunds().getDataRange();
//                case "NPS":
//                    return model.getNps().getDataRange();
//
//                default:
//                    throw new MoneyowlApplicationException("Invalid fiType");
//            }
//        } else {
//            log.error(LOG_TEMPLATE, PORTFOLIO_NOT_FOUND, userId);
//            throw new ResourceNotFoundException(PORTFOLIO_NOT_FOUND, userId);
//        }
//    }
//
//    private Boolean portfolioExists(Long userId) {
//        return mongoRepository.existsByUserId(userId);
//    }
//
//    private void createAndSaveConsentIdByFiType(String fiType, String expiryDate, UserPortfolioModel model, String consentId) {
//        AssetAccount assetAccount = new AssetAccount();
//        assetAccount.setConsentId(consentId);
//        if (expiryDate != null) {
//            assetAccount.setConsentExpiry(expiryDate);
//        }
//        switch (fiType.toUpperCase()) {
//            case "EQUITIES":
//                model.setEquities(assetAccount);
//            case "DEPOSIT":
//                model.setDeposits(assetAccount);
//            case "MUTUAL_FUNDS":
//                model.setMutualFunds(assetAccount);
//            case "NPS":
//                model.setNps(assetAccount);
//
//        }
//        saveUserPortfolio(model);
//    }
//
//    private void createAndSaveIdByFiType(String fiType, UserPortfolioModel model, String Id) {
//
//        AssetAccount assetAccount = new AssetAccount();
//        assetAccount.setId(Id);
//
//        switch (fiType.toUpperCase()) {
//            case "EQUITIES":
//                model.setEquities(assetAccount);
//            case "DEPOSIT":
//                model.setDeposits(assetAccount);
//            case "MUTUAL_FUNDS":
//                model.setMutualFunds(assetAccount);
//            case "NPS":
//                model.setNps(assetAccount);
//        }
//        saveUserPortfolio(model);
//    }
//
//    private void createAndSaveDataRangeByFiType(String fiType, UserPortfolioModel model, DataRange range) {
//
//        AssetAccount assetAccount = new AssetAccount();
//        assetAccount.setDataRange(range);
//
//        switch (fiType.toUpperCase()) {
//            case "EQUITIES":
//                model.setEquities(assetAccount);
//            case "DEPOSIT":
//                model.setDeposits(assetAccount);
//            case "MUTUAL_FUNDS":
//                model.setMutualFunds(assetAccount);
//            case "NPS":
//                model.setNps(assetAccount);
//        }
//        saveUserPortfolio(model);
//    }
//
//    public List<AccountData> getAccountsByFiType(UserPortfolioModel model, String fiType) {
//        switch (fiType) {
//            case "EQUITIES":
//                return model.getEquities().getAsset();
//            case "DEPOSIT":
//                return model.getDeposits().getAsset();
//            case "MUTUAL_FUNDS":
//                return model.getMutualFunds().getAsset();
//            case "NPS":
//                return model.getNps().getAsset();
//            default:
//                throw new MoneyowlApplicationException("Invalid fiType");
//        }
//    }
//}
package com.finance.moneyowl.service.impl;

import com.finance.moneyowl.exceptions.MoneyowlApplicationException;
import com.finance.moneyowl.exceptions.ResourceNotFoundException;
import com.finance.moneyowl.generatedmodels.AccountData;
import com.finance.moneyowl.generatedmodels.DataRange;
import com.finance.moneyowl.model.AssetAccount;
import com.finance.moneyowl.model.UserPortfolioModel;
import com.finance.moneyowl.repository.MongoDBRepository;
import com.finance.moneyowl.service.interfaces.MongoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

import static com.finance.moneyowl.utils.ErrorMessageConstants.LOG_TEMPLATE;
import static com.finance.moneyowl.utils.ErrorMessageConstants.PORTFOLIO_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class MongoServiceImpl implements MongoService {

    private final MongoDBRepository mongoRepository;

    @Override
    public UserPortfolioModel saveUserPortfolio(UserPortfolioModel userPortfolioModel) {
        return mongoRepository.save(userPortfolioModel);
    }

    @Override
    public void saveConsentIdAndExpiryByFiType(Long userId, String consentId, String expiryDate, String fiType) {
        UserPortfolioModel model = getOrCreatePortfolio(userId);
        AssetAccount assetAccount = getOrCreateAssetAccount(model, fiType);

        assetAccount.setConsentId(consentId);
        if (StringUtils.hasText(expiryDate)) {
            assetAccount.setConsentExpiry(expiryDate);
        }

        saveUserPortfolio(model);
    }

    @Override
    public void saveIdByFiType(Long userId, String id, String fiType) {
        UserPortfolioModel model = getOrCreatePortfolio(userId);
        AssetAccount assetAccount = getOrCreateAssetAccount(model, fiType);

        assetAccount.setId(id);
        saveUserPortfolio(model);
    }

    @Override
    public void saveDataRangeByFiType(Long userId, DataRange range, String fiType) {
        UserPortfolioModel model = getOrCreatePortfolio(userId);
        AssetAccount assetAccount = getOrCreateAssetAccount(model, fiType);

        assetAccount.setDataRange(range);
        saveUserPortfolio(model);
    }

    @Override
    public UserPortfolioModel getUserPortfolio(Long userId) {
        return mongoRepository.findByUserId(userId);
    }

    @Override
    public List<AccountData> getAccountsByFiType(Long userId, String fiType) {
        AssetAccount account = getExistingAssetAccountSafe(userId, fiType);
        return account.getAsset() != null ? account.getAsset() : Collections.emptyList();
    }

    @Override
    public String getConsentIdByFiType(Long userId, String fiType) {
        return getExistingAssetAccountSafe(userId, fiType).getConsentId();
    }

    @Override
    public String getIdByFiType(Long userId, String fiType) {
        return getExistingAssetAccountSafe(userId, fiType).getId();
    }

    @Override
    public DataRange getDataRangeByFiType(Long userId, String fiType) {
        return getExistingAssetAccountSafe(userId, fiType).getDataRange();
    }

    @Override
    public AssetAccount getAssetAccountByFiType(String fiType, UserPortfolioModel model) {
        return extractAssetAccount(model, fiType);
    }

    // ===================================================================================
    // PRIVATE HELPER METHODS (DRY Principle & Safety)
    // ===================================================================================

    private UserPortfolioModel getOrCreatePortfolio(Long userId) {
        UserPortfolioModel model = mongoRepository.findByUserId(userId);
        if (model == null) {
            model = new UserPortfolioModel();
            model.setUserId(userId);
        }
        return model;
    }

    private AssetAccount getOrCreateAssetAccount(UserPortfolioModel model, String fiType) {
        AssetAccount account = extractAssetAccount(model, fiType);
        if (account == null) {
            account = new AssetAccount();
            setAssetAccount(model, fiType, account);
        }
        return account;
    }

    private AssetAccount getExistingAssetAccountSafe(Long userId, String fiType) {
        UserPortfolioModel model = mongoRepository.findByUserId(userId);
        if (model == null) {
            log.error(LOG_TEMPLATE, PORTFOLIO_NOT_FOUND, userId);
            throw new ResourceNotFoundException(PORTFOLIO_NOT_FOUND, userId);
        }
        AssetAccount account = extractAssetAccount(model, fiType);
        // Return an empty AssetAccount instead of null to prevent downstream NPEs
        return account != null ? account : new AssetAccount();
    }

    private AssetAccount extractAssetAccount(UserPortfolioModel model, String fiType) {
        if (model == null || !StringUtils.hasText(fiType)) return null;

        return switch (fiType.toUpperCase()) {
            case "EQUITIES" -> model.getEquities();
            case "DEPOSIT" -> model.getDeposits();
            case "MUTUAL_FUNDS" -> model.getMutualFunds();
            case "NPS" -> model.getNps();
            default -> throw new MoneyowlApplicationException("Invalid fiType: " + fiType);
        };
    }

    private void setAssetAccount(UserPortfolioModel model, String fiType, AssetAccount account) {
        switch (fiType.toUpperCase()) {
            case "EQUITIES" -> model.setEquities(account);
            case "DEPOSIT" -> model.setDeposits(account);
            case "MUTUAL_FUNDS" -> model.setMutualFunds(account);
            case "NPS" -> model.setNps(account);
            default -> throw new MoneyowlApplicationException("Invalid fiType: " + fiType);
        }
    }
}
