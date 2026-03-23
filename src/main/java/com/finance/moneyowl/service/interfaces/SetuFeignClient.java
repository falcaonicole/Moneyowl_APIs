package com.finance.moneyowl.service.interfaces;

import com.finance.moneyowl.generatedmodels.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@FeignClient(
        name = "setuFeignClient",
        url = "${setu-aa.base.url}"
)
public interface SetuFeignClient {

    @PostMapping("/consents")
    CreateConsentResponse createConsentRequest(
            @RequestHeader("Authorization") String token,
            @RequestHeader("x-product-instance-id") String productInstanceId,
            @RequestBody CreateConsentRequest request
    );

    @GetMapping("/consents/{consentId}")
    GetConsentResponse getConsentResponse(
            @RequestHeader("Authorization") String token,
            @RequestHeader("x-product-instance-id") String productInstanceId,
            @PathVariable("consentId") String consentId);

    @PostMapping("/sessions")
    CreateDataFetchResponse getDataFetchResponse(
            @RequestHeader("Authorization") String token,
            @RequestHeader("x-product-instance-id") String productInstanceId,
            @RequestBody CreateDataFetchRequest request
    );

    @GetMapping("/sessions/{Id}")
    SetuFIDataResponse getFIData(
            @RequestHeader("Authorization") String token,
            @RequestHeader("x-product-instance-id") String productInstanceId,
            @PathVariable("Id") String Id);
}
