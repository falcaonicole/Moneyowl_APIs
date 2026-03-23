package com.finance.moneyowl.service.interfaces;

import com.finance.moneyowl.generatedmodels.ConsentRequestUIResponse;
import com.finance.moneyowl.generatedmodels.CreateDataFetchResponse;
import com.finance.moneyowl.generatedmodels.SetuAuthTokenResponse;

public interface SetuAAService {

    SetuAuthTokenResponse fetchAuthToken();

    //Consent APIs
    ConsentRequestUIResponse createUserConsent(Long userId, String fiType);

    String getConsentStatus(Long userId, String fiType);

    CreateDataFetchResponse createDataFetch(Long userId, String fiType);

    String fetchFIData(Long userId, String fiType);
}
