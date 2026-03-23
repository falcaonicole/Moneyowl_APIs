package com.finance.moneyowl.service.impl;

import com.finance.moneyowl.entity.User;
import com.finance.moneyowl.generatedmodels.*;
import com.finance.moneyowl.service.interfaces.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class SetuAAServiceImpl implements SetuAAService {

    @Value("${setu-aa.clientID}")
    private String clientID;

    @Value("${setu-aa.grant_type}")
    private String grant_type;

    @Value("${setu-aa.secret}")
    private String secret;

    @Value("${setu-aa.client}")
    private String client;

    @Value("${setu-aa.x-product-instance-id}")
    private String productInstanceId;

    @Autowired
    private SetuAuthTokenClient setuAuthTokenClient;

    @Autowired
    private SetuFeignClient setuFeignClient;

    @Autowired
    private UserService userService;

    @Autowired
    private MongoService mongoService;

    @Override
    public SetuAuthTokenResponse fetchAuthToken() {
        //Add logic to check if token has expired or not and if not then reuse same token
        log.info("Start SetuAAServiceImpl :: fetchAuthToken");
        SetuAuthTokenRequest setuAuthTokenRequest = new SetuAuthTokenRequest(clientID, grant_type, secret);
        SetuAuthTokenResponse authToken = setuAuthTokenClient.fetchAuthToken(client, setuAuthTokenRequest);
        log.info("End SetuAAServiceImpl :: fetchAuthToken :: {} ", authToken.getAccessToken());
        return authToken;
    }

    @Override
    public ConsentRequestUIResponse createUserConsent(Long userId, String fiType) {
        log.info("Start SetuAAServiceImpl :: createUserConsent :: {} - {}", userId, fiType);
        String token = String.format("Bearer %s", this.fetchAuthToken().getAccessToken());
        CreateConsentRequest createConsentRequest = buildConsentRequest(userId, fiType);

        CreateConsentResponse consentResponse = setuFeignClient.createConsentRequest(token, productInstanceId, createConsentRequest);

        ConsentRequestUIResponse consent = new ConsentRequestUIResponse();
        consent.setUrl(consentResponse.getUrl());
        consent.setFiTypes(consentResponse.getDetail().getFiTypes());

        mongoService.saveConsentIdAndExpiryByFiType(userId, consentResponse.getId().toString(), consentResponse.getDetail().getConsentExpiry(), fiType);
        log.info("End SetuAAServiceImpl :: createUserConsent");
        return consent;
    }

    @Override
    public String getConsentStatus(Long userId, String fiType) {
        String consentId = mongoService.getConsentIdByFiType(userId, fiType);
        String token = String.format("Bearer %s", this.fetchAuthToken().getAccessToken());
        GetConsentResponse response = setuFeignClient.getConsentResponse(token, productInstanceId, consentId);
        return response.getStatus();
    }

    @Override
    public CreateDataFetchResponse createDataFetch(Long userId, String fiType) {
        String consentId = mongoService.getConsentIdByFiType(userId, fiType);
        DataRange dataRange = mongoService.getDataRangeByFiType(userId, fiType);
        CreateDataFetchRequest request = new CreateDataFetchRequest(consentId, dataRange, "json");

        String token = String.format("Bearer %s", this.fetchAuthToken().getAccessToken());
        CreateDataFetchResponse response = setuFeignClient.getDataFetchResponse(token, productInstanceId, request);
        mongoService.saveIdByFiType(userId, response.getId(), fiType);
        mongoService.saveConsentIdAndExpiryByFiType(userId, response.getConsentId(), null, fiType);
        return response;
    }

    @Override
    public String fetchFIData(Long userId, String fiType) {
        try {
            String token = String.format("Bearer %s", this.fetchAuthToken().getAccessToken());
            String Id = mongoService.getIdByFiType(userId, fiType);
            SetuFIDataResponse fiData = setuFeignClient.getFIData(token, productInstanceId, Id);

            //Fetch fiData/getAccount to UserPortfolioModel's AssetAccount and then save in MongoDB
            return "FI Data Fetched Successfully";
        } catch (Exception e) {
            return "Error Occurred While Fetching FI Data";
        }
    }

    private CreateConsentRequest buildConsentRequest(Long userId, String fiType) {
        log.info("Start SetuAAServiceImpl :: buildConsentRequest :: {}", fiType);
        User user = userService.getUserById(userId);
        CreateConsentRequest createConsentRequest = new CreateConsentRequest();
        createConsentRequest.setFiTypes(List.of(fiType));
        createConsentRequest.setVua("9833756340@onemoney");
        /** createConsentRequest.setVua(String.format("%s@onemoney", user.getMobNo())); */
        createConsentRequest.setContext(Collections.emptyList());

        ConsentDuration duration = new ConsentDuration("MONTH", "24");
        createConsentRequest.setConsentDuration(duration);

        String to = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString();

        String from = ZonedDateTime
                .now(ZoneOffset.UTC)
                .minusYears(1)
                .toInstant()
                .truncatedTo(ChronoUnit.SECONDS).toString();

        DataRange dataRange = new DataRange(from, to);
        mongoService.saveDataRangeByFiType(userId, dataRange, fiType);
        createConsentRequest.setDataRange(dataRange);
        log.info("End SetuAAServiceImpl :: buildConsentRequest :: {}", fiType);
        return createConsentRequest;
    }
}
