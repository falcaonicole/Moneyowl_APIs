package com.finance.moneyowl.restcontroller;

import com.finance.moneyowl.generatedmodels.ConsentRequestUIResponse;
import com.finance.moneyowl.generatedmodels.CreateDataFetchResponse;
import com.finance.moneyowl.service.interfaces.SetuAAService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Slf4j
@AllArgsConstructor
public class SetuAAController {

    private SetuAAService setuConsentService;


    @GetMapping("/create-consent-request/{userId}/{fiType}")
    public ResponseEntity<ConsentRequestUIResponse> createConsentRequest(
            @PathVariable Long userId,
            @PathVariable String fiType) {
        ConsentRequestUIResponse consentResponse = setuConsentService.createUserConsent(userId, fiType);
        return new ResponseEntity<>(consentResponse, HttpStatus.OK);
    }

    @GetMapping("/get-consent-status/{userId}/{fiType}")
    public ResponseEntity<String> getConsentStatus(@PathVariable Long userId, @PathVariable String fiType) {
        String status = setuConsentService.getConsentStatus(userId, fiType);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @GetMapping("/create-data-fetch/{userId}/{fiType}")
    public ResponseEntity<CreateDataFetchResponse> createDataFetch(@PathVariable Long userId, @PathVariable String fiType) {
        CreateDataFetchResponse status = setuConsentService.createDataFetch(userId, fiType);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
    
    @GetMapping("/fetch-fiData/{userId}/{fiType}")
    public ResponseEntity<String> fetchFiData(@PathVariable Long userId, @PathVariable String fiType) {
        String status = setuConsentService.fetchFIData(userId, fiType);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

}
