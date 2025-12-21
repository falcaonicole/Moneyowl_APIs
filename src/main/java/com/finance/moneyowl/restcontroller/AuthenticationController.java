package com.finance.moneyowl.restcontroller;

import com.finance.moneyowl.generatedmodels.LoginRequest;
import com.finance.moneyowl.generatedmodels.LoginResponse;
import com.finance.moneyowl.generatedmodels.OtpRequest;
import com.finance.moneyowl.generatedmodels.SignupRequest;
import com.finance.moneyowl.service.impl.AuthenticationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import src.finance.moneyowl.AuthenticationApi;

@RestController
@Slf4j
public class AuthenticationController implements AuthenticationApi {

    @Autowired
    private AuthenticationServiceImpl authService;

    @Override
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        log.info("Start AuthenticationController :: loginUser - {}", loginRequest);
        LoginResponse loginResponse = authService.login(loginRequest);
        log.info("End AuthenticationController :: loginUser");
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> verifyOtp(@RequestBody OtpRequest request) {
        log.info("Start AuthenticationController :: verifyOtp - {}", request);
        String result = authService.verifyOtp(request);
        log.info("End AuthenticationController :: verifyOtp");
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<String> registerUser(@RequestBody SignupRequest signupRequest) {
        log.info("Start AuthenticationController :: registerUser - {}", signupRequest);
        String result = authService.register(signupRequest);
        log.info("End AuthenticationController :: registerUser");
        return ResponseEntity.ok(result);
    }

}
