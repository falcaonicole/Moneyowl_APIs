package com.finance.moneyowl.utils;

import com.finance.moneyowl.entity.User;
import com.finance.moneyowl.exceptions.ResourceNotFoundException;
import com.finance.moneyowl.generatedmodels.*;
import com.finance.moneyowl.model.AssetAccount;
import com.finance.moneyowl.model.UserPortfolio;
import com.finance.moneyowl.model.UserPortfolioSetuResponseModel;
import com.finance.moneyowl.repository.MongoHoldingRepository;
import com.finance.moneyowl.repository.UserPortfolioMongoRepo;
import com.finance.moneyowl.service.interfaces.MongoService;
import com.finance.moneyowl.service.interfaces.UserService;
import com.finance.moneyowl.service.interfaces.assetWiseExtractor.FiTypeExtractor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Slf4j
@AllArgsConstructor
public class SetuUtils {

    private final MongoService mongoService;
    private final FiTypeExtractorRegistry extractorRegistry;
    private final MongoHoldingRepository mongoHoldingRepository;
    private final UserService userService;
    private final UserPortfolioMongoRepo userPortfolioMongoRepo;

    public void saveFIData(SetuFIDataResponse fiData, Long userId, String fiType) {
        if (fiData == null || fiData.getFips() == null || fiType == null) {
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
            return;
        }
        UserPortfolioSetuResponseModel portfolioModel = mongoService.getUserPortfolio(userId);
        if (portfolioModel == null) {
            throw new UsernameNotFoundException("User Portfolio Not Found : " + userId);
        }
        Map<String, AssetAccount> assets = portfolioModel.getAssets();
        if (assets == null) {
            log.info("Creating a new HashMap for Portfolio Assets :: {} :: {}", fiType, userId);
            assets = new HashMap<>();
            portfolioModel.setAssets(assets);
        }
        AssetAccount account = assets.computeIfAbsent(fiType.toLowerCase(), k -> new AssetAccount());
        account.setAsset(accountsData);
        mongoService.saveUserPortfolio(portfolioModel);
        extractHoldingsAndAssets(fiData, userId, fiType, account);
    }

    public void extractHoldingsAndAssets(SetuFIDataResponse fiData, Long userId, String fiType, AssetAccount account) {
        if (fiData == null || fiData.getFips() == null || fiType == null || account == null) {
            return;
        }

        FiTypeExtractor extractor = extractorRegistry.get(fiType)
                .orElseThrow(() -> new ResourceNotFoundException("No extractor registered for fiType = " + fiType));

        List<AccountData> allAccounts = fiData.getFips().stream()
                .filter(Objects::nonNull)
                .map(Fip::getAccounts)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .toList();

        if (allAccounts.isEmpty()) {
            return;
        }

        UserPortfolio currentPortfolio = extractor.extractPortfolio(account);
        currentPortfolio.setUserId(userId);
        currentPortfolio.setCurrentAssetValue(BigDecimal.ZERO);
        currentPortfolio.setTotalInvestedValue(BigDecimal.ZERO);
        // Sequentially fold each account's holdings into the running portfolio
        for (AccountData accountData : allAccounts) {
            currentPortfolio = extractor.extract(accountData, userId, account, currentPortfolio);
        }
        // Save a single consolidated portfolio across all holdings
        userPortfolioMongoRepo.save(currentPortfolio);
    }

    public CreateConsentRequest buildConsentRequest(Long userId, String fiType) {
        log.info("Start SetuAAServiceImpl :: buildConsentRequest :: {}", fiType);
        User user = userService.getUserById(userId);
        CreateConsentRequest createConsentRequest = new CreateConsentRequest();
        createConsentRequest.setFiTypes(List.of(fiType));
        createConsentRequest.setVua(String.format("%s@onemoney", user.getMobNo()));
        createConsentRequest.setContext(Collections.emptyList());

        ConsentDuration duration = new ConsentDuration("MONTH", "24");
        createConsentRequest.setConsentDuration(duration);

        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneOffset.UTC);

        String to = formatter.format(Instant.now());

        String from = formatter.format(
                ZonedDateTime.now(ZoneOffset.UTC)
                        .minusYears(1)
                        .toInstant()
        );

        DataRange dataRange = new DataRange(from, to);
        mongoService.saveDataRangeByFiType(userId, dataRange, fiType);
        createConsentRequest.setDataRange(dataRange);
        log.info("End SetuAAServiceImpl :: buildConsentRequest :: {}", fiType);
        return createConsentRequest;
    }
}
