package com.finance.moneyowl.utils;

import com.finance.moneyowl.entity.User;
import com.finance.moneyowl.exceptions.ResourceNotFoundException;
import com.finance.moneyowl.generatedmodels.*;
import com.finance.moneyowl.model.AssetAccount;
import com.finance.moneyowl.model.UserPortfolio;
import com.finance.moneyowl.model.UserPortfolioSetuResponseModel;
import com.finance.moneyowl.repository.UserPortfolioMongoRepo;
import com.finance.moneyowl.service.interfaces.MongoService;
import com.finance.moneyowl.service.interfaces.UserService;
import com.finance.moneyowl.service.interfaces.assetWiseExtractor.FiTypeExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SetuUtils {

    private final MongoService mongoService;
    private final FiTypeExtractorRegistry extractorRegistry;
    private final UserService userService;
    private final UserPortfolioMongoRepo userPortfolioMongoRepo;

    @Value("${setu-aa.aa.vua-suffix:@onemoney}")
    private String vuaSuffix;

    @Value("${setu-aa.aa.consent-duration-months:24}")
    private String consentDurationMonths;

    @Value("${setu-aa.aa.data-range-years:1}")
    private int dataRangeYears;

    @Transactional
    public void saveFIData(SetuFIDataResponse fiData, Long userId, String fiType) {
        if (fiData == null || fiData.getFips() == null || !StringUtils.hasText(fiType)) {
            log.warn("Invalid FI Data received for UserId: {}", userId);
            return;
        }

        List<AccountData> accountsData = fiData.getFips()
                .stream()
                .filter(Objects::nonNull)
                .map(Fip::getAccounts)
                .filter(accounts -> accounts != null && !accounts.isEmpty())
                .flatMap(Collection::stream)
                .toList();

        if (accountsData.isEmpty()) {
            log.info("No accounts found in FI Data for UserId: {}, FiType: {}", userId, fiType);
            return;
        }

        UserPortfolioSetuResponseModel portfolioModel = mongoService.getUserPortfolio(userId);
        if (portfolioModel == null) {
            throw new ResourceNotFoundException("Raw User Portfolio State Not Found for User: " + userId);
        }

        if (portfolioModel.getAssets() == null) {
            portfolioModel.setAssets(new HashMap<>());
        }

        AssetAccount account = portfolioModel.getAssets().computeIfAbsent(fiType, k -> new AssetAccount());
        account.setAsset(accountsData);

        mongoService.saveUserPortfolio(portfolioModel);

        extractHoldingsAndAssets(userId, fiType, account, accountsData);
    }

    private void extractHoldingsAndAssets(Long userId, String fiType, AssetAccount account, List<AccountData> allAccounts) {

        FiTypeExtractor extractor = extractorRegistry.get(fiType)
                .orElseThrow(() -> new ResourceNotFoundException("No extractor registered for fiType: " + fiType));

        UserPortfolio existingPortfolio = userPortfolioMongoRepo.findByUserIdAndAssetType(userId, fiType)
                .orElseGet(() -> {
                    UserPortfolio newPortfolio = extractor.extractPortfolio(account);
                    newPortfolio.setUserId(userId);
                    newPortfolio.setAssetType(fiType);
                    return newPortfolio;
                });

        existingPortfolio.setCurrentAssetValue(BigDecimal.ZERO);
        existingPortfolio.setTotalInvestedValue(BigDecimal.ZERO);
        existingPortfolio.setLastUpdatedOn(java.time.LocalDateTime.now());

        for (AccountData accountData : allAccounts) {
            existingPortfolio = extractor.extract(accountData, userId, account, existingPortfolio);
        }

        userPortfolioMongoRepo.save(existingPortfolio);
        log.info("Successfully extracted and saved portfolio for UserId: {}, FiType: {}", userId, fiType);
    }

    public CreateConsentRequest buildConsentRequest(Long userId, String fiType) {
        log.info("Start buildConsentRequest for UserId: {}, FiType: {}", userId, fiType);

        User user = userService.getUserById(userId);
        CreateConsentRequest request = new CreateConsentRequest();

        request.setFiTypes(List.of(fiType));
        request.setVua(user.getMobNo() + vuaSuffix); // e.g., @onemoney
        request.setContext(Collections.emptyList());
        request.setConsentDuration(new ConsentDuration("MONTH", consentDurationMonths));

        // 1. Strict Formatter ensuring exactly 3 millisecond digits and 'Z' for UTC
        DateTimeFormatter strictFormatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneOffset.UTC);

        // 2. Base time in UTC
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);

        // 3. FROM: Today - 1 Year (Fetches existing wealth/portfolio)
        String from = strictFormatter.format(nowUTC.minusYears(1));

        // 4. TO: Today + 1 Year (Allows continuous future syncing on this same consent)
        String to = strictFormatter.format(nowUTC);

        DataRange dataRange = new DataRange(from, to);

        // Save state
        mongoService.saveDataRangeByFiType(userId, dataRange, fiType);
        request.setDataRange(dataRange);

        log.info("End buildConsentRequest for UserId: {}, FiType: {} | Range: {} to {}", userId, fiType, from, to);
        return request;
    }
}
