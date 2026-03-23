package com.finance.moneyowl.service.impl;

import com.finance.moneyowl.entity.Roles;
import com.finance.moneyowl.entity.User;
import com.finance.moneyowl.exceptions.MoneyowlApplicationException;
import com.finance.moneyowl.exceptions.UnAuthorisedException;
import com.finance.moneyowl.generatedmodels.EmailLoginRequest;
import com.finance.moneyowl.generatedmodels.LoginResponse;
import com.finance.moneyowl.generatedmodels.OtpRequest;
import com.finance.moneyowl.generatedmodels.SignupRequest;
import com.finance.moneyowl.repository.RoleRepository;
import com.finance.moneyowl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static com.finance.moneyowl.utils.ErrorMessageConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl {

    Random random;
    private UserRepository userRepository;
    private UserServiceImpl userService;
    private PasswordEncoder passwordEncoder;
    private JwtTokenServiceImpl jwtService;
    private AuthenticationManager authenticationManager;
    private OtpEmailServiceImpl otpEmailService;
    private OtpMobNoServiceImpl otpMobNoService;
    private UserDetailsService userDetailsService;
    private RoleRepository roleRepository;

    public String register(SignupRequest request) {
        log.info("Start AuthenticationServiceImpl :: register");

        if (userRepository.existsByEmail(request.getEmail())) {
            log.error(LOG_TEMPLATE, USER_ALREADY_EXISTS, request.getEmail());
            throw new MoneyowlApplicationException(USER_ALREADY_EXISTS, request.getEmail());
        }
        Roles defaultRole = roleRepository.findByRoleName("ROLE_BASIC")
                .orElseThrow(() -> {
                    log.error("Requested Role does not exists - {}", "ROLE_BASIC");
                    return new MoneyowlApplicationException("Requested Role does not exists");
                });

        User user = User.builder()
                .fullName(request.getFullName())
                .address(request.getAddress())
                .email(request.getEmail())
                .mobNo(request.getMobNo())
                .password(passwordEncoder.encode(request.getPassword()))
                .userType("ROLE_BASIC")
                .isActive(true)
                .isVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .roles(List.of(defaultRole))
                .build();

        userRepository.save(user);
        //Then let user select verification mode as Email/MobNo
        otpMobNoService.sendOtp(request.getMobNo());
        log.info("End AuthenticationServiceImpl :: register");
        return "User registered successfully. Please check email/SMS for OTP Verification.";
    }

    public String verifyEmailOtp(OtpRequest otpRequest) {
        log.info("Start AuthenticationServiceImpl :: verifyOtp");
        if (otpEmailService.verifyOtp(otpRequest)) {
            return "Account verified successfully. Please Login to access account";
        } else {
            throw new UnAuthorisedException(INCORRECT_OTP, otpRequest.getOtp());
        }

    }

    // generate JWT if OTP Login is successful
    public LoginResponse verifyMobNoOtp(OtpRequest otpRequest) {
        log.info("Start AuthenticationServiceImpl :: verifyMobNoOtp");
        if (otpMobNoService.verifyOtp(otpRequest)) {
            User userDetails = userService.findByMobNo(otpRequest.getIdentifier());
            String jwtToken = jwtService.generateToken(userDetails);

            log.info("End AuthenticationServiceImpl :: verifyMobNoOtp");
            return new LoginResponse(jwtToken, "", "Login successful");
        } else {
            throw new UnAuthorisedException(INCORRECT_OTP, otpRequest.getOtp());
        }
    }

    public LoginResponse login(EmailLoginRequest request) {
        log.info("Start AuthenticationServiceImpl :: login");

        User user = userService.findByEmail(request.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        if (!user.isVerified()) {
            log.error(LOG_TEMPLATE, "Account not verified. Please verify your email - {}", request.getEmail());
            throw new MoneyowlApplicationException("Account not verified. Please verify your email.");
        }
        String jwtToken = jwtService.generateToken(user);

        log.info("End AuthenticationServiceImpl :: login");
        return new LoginResponse(jwtToken, "", "Login successful");
    }

}
