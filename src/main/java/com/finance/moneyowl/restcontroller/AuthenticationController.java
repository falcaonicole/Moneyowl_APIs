package com.finance.moneyowl.restcontroller;

import com.finance.moneyowl.generatedmodels.EmailLoginRequest;
import com.finance.moneyowl.generatedmodels.LoginResponse;
import com.finance.moneyowl.generatedmodels.OtpRequest;
import com.finance.moneyowl.generatedmodels.SignupRequest;
import com.finance.moneyowl.service.impl.AuthenticationServiceImpl;
import com.finance.moneyowl.service.impl.OtpEmailServiceImpl;
import com.finance.moneyowl.service.impl.OtpMobNoServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import src.finance.moneyowl.AuthenticationApi;

@RestController
@AllArgsConstructor
@Slf4j
public class AuthenticationController implements AuthenticationApi {

    private AuthenticationServiceImpl authService;
    private OtpEmailServiceImpl emailOtpService;
    private OtpMobNoServiceImpl mobNoOtpService;

    @Override
    public ResponseEntity<LoginResponse> loginUser(@RequestBody EmailLoginRequest EmailLoginRequest) {
        log.info("Start AuthenticationController :: loginUser - {}", EmailLoginRequest);
        LoginResponse loginResponse = authService.login(EmailLoginRequest);
        log.info("End AuthenticationController :: loginUser");
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> verifyEmailOtp(@RequestBody OtpRequest request) {
        log.info("Start AuthenticationController :: verifyEmailOtp - {}", request);
        String result = authService.verifyEmailOtp(request);
        log.info("End AuthenticationController :: verifyEmailOtp");
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<LoginResponse> verifyMobOtp(@RequestBody OtpRequest request) {
        log.info("Start AuthenticationController :: verifyMobOtp - {}", request);
        LoginResponse result = authService.verifyMobNoOtp(request);
        log.info("End AuthenticationController :: verifyMobOtp");
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<String> registerUser(@RequestBody SignupRequest signupRequest) {
        log.info("Start AuthenticationController :: registerUser - {}", signupRequest);
        String result = authService.register(signupRequest);
        log.info("End AuthenticationController :: registerUser");
        return ResponseEntity.ok(result);
    }

    /**
     * Todo : To be decided later on , for now MobNo based Login enabled
     **/
    @Override
    public ResponseEntity<String> sendOtp(@PathVariable Long otpType) {
        log.info("Start AuthenticationController :: sendOtp - {}", otpType);

        log.info("End AuthenticationController :: sendOtp");
        return ResponseEntity.ok("OTP send successfully.");
    }

}
