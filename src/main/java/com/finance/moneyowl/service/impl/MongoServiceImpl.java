package com.finance.moneyowl.service.impl;

import com.finance.moneyowl.exceptions.ResourceNotFoundException;
import com.finance.moneyowl.generatedmodels.AccountData;
import com.finance.moneyowl.generatedmodels.DataRange;
import com.finance.moneyowl.model.AssetAccount;
import com.finance.moneyowl.model.UserPortfolioSetuResponseModel;
import com.finance.moneyowl.repository.MongoDBRepository;
import com.finance.moneyowl.service.interfaces.MongoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.finance.moneyowl.utils.ErrorMessageConstants.LOG_TEMPLATE;
import static com.finance.moneyowl.utils.ErrorMessageConstants.PORTFOLIO_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class MongoServiceImpl implements MongoService {

    private final MongoDBRepository mongoRepository;

    @Override
    public UserPortfolioSetuResponseModel saveUserPortfolio(UserPortfolioSetuResponseModel userPortfolioSetuResponseModel) {
        return mongoRepository.save(userPortfolioSetuResponseModel);
    }

    @Override
    public void saveConsentIdAndExpiryByFiType(Long userId, String consentId, String expiryDate, String fiType) {
        UserPortfolioSetuResponseModel model = getOrCreatePortfolio(userId);
        if (model.getAssets() == null) {
            model.setAssets(new HashMap<>());
        }
        AssetAccount account = model.getAssets().computeIfAbsent(fiType.toLowerCase(), k -> new AssetAccount());

        account.setConsentId(consentId);
        if (StringUtils.hasText(expiryDate)) {
            account.setConsentExpiry(expiryDate);
        }
        saveUserPortfolio(model);
    }

    @Override
    public void saveIdByFiType(Long userId, String id, String fiType) {
        UserPortfolioSetuResponseModel model = getOrCreatePortfolio(userId);
        if (model.getAssets() == null) {
            model.setAssets(new HashMap<>());
        }
        model.getAssets()
                .computeIfAbsent(fiType.toLowerCase(), k -> new AssetAccount())
                .setId(id);

        saveUserPortfolio(model);
    }

    @Override
    public void saveDataRangeByFiType(Long userId, DataRange range, String fiType) {
        UserPortfolioSetuResponseModel model = getOrCreatePortfolio(userId);

        if (model.getAssets() == null) {
            model.setAssets(new HashMap<>());
        }
        model.getAssets()
                .computeIfAbsent(fiType.toLowerCase(), k -> new AssetAccount())
                .setDataRange(range);
        saveUserPortfolio(model);
    }

    @Override
    public UserPortfolioSetuResponseModel getUserPortfolio(Long userId) {
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
    public AssetAccount getAssetAccountByFiType(String fiType, UserPortfolioSetuResponseModel model) {
        return extractAssetAccount(model, fiType);
    }

    private UserPortfolioSetuResponseModel getOrCreatePortfolio(Long userId) {
        UserPortfolioSetuResponseModel model = mongoRepository.findByUserId(userId);
        if (model == null) {
            model = new UserPortfolioSetuResponseModel();
            model.setUserId(userId);
        }
        return model;
    }

    private AssetAccount getExistingAssetAccountSafe(Long userId, String fiType) {
        if (!StringUtils.hasText(fiType)) {
            return new AssetAccount();
        }
        UserPortfolioSetuResponseModel model = mongoRepository.findByUserId(userId);
        if (model == null) {
            log.error(LOG_TEMPLATE, PORTFOLIO_NOT_FOUND, userId);
            throw new ResourceNotFoundException(PORTFOLIO_NOT_FOUND, userId);
        }
        Map<String, AssetAccount> assets = model.getAssets();
        if (model.getAssets() == null) {
            return new AssetAccount();
        }
        return assets.getOrDefault(fiType.toLowerCase(), new AssetAccount());
    }

    private AssetAccount extractAssetAccount(UserPortfolioSetuResponseModel model, String fiType) {
        if (!StringUtils.hasText(fiType)) return null;
        return model.getAssets().get(fiType.toLowerCase());
    }

    private void setAssetAccount(UserPortfolioSetuResponseModel portfolioModel, String fiType, AssetAccount account) {
        if (!StringUtils.hasText(fiType) || portfolioModel == null) return;

        if (portfolioModel.getAssets() == null) {
            portfolioModel.setAssets(new HashMap<>());
        }
        portfolioModel.getAssets().put(fiType.toLowerCase(), account);
    }
}
