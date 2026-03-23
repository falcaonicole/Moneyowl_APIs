package com.finance.moneyowl.service.interfaces;

import com.finance.moneyowl.generatedmodels.SetuAuthTokenRequest;
import com.finance.moneyowl.generatedmodels.SetuAuthTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Component
@FeignClient(
        name = "setuAuthTokenFeignClient",
        url = "${setu-aa.auth.url}"
)
public interface SetuAuthTokenClient {

    @PostMapping
    SetuAuthTokenResponse fetchAuthToken(
            @RequestHeader("client") String client,
            @RequestBody SetuAuthTokenRequest request
    );
}
