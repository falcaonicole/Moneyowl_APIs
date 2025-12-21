package com.finance.moneyowl.service.impl;

import com.finance.moneyowl.entity.Roles;
import com.finance.moneyowl.entity.User;
import com.finance.moneyowl.exceptions.MoneyowlApplicationException;
import com.finance.moneyowl.generatedmodels.LoginRequest;
import com.finance.moneyowl.generatedmodels.LoginResponse;
import com.finance.moneyowl.generatedmodels.OtpRequest;
import com.finance.moneyowl.generatedmodels.SignupRequest;
import com.finance.moneyowl.repository.RoleRepository;
import com.finance.moneyowl.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class AuthenticationServiceImpl {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenServiceImpl jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private SentOTPEmailServiceImpl emailService;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RoleRepository roleRepository;

    public String register(SignupRequest request) {
        log.info("Start AuthenticationServiceImpl :: register");

        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("User with provided email already exists - {}", request.getEmail());
            throw new MoneyowlApplicationException("User with provided email already exists");
        }
        Roles defaultRole = roleRepository.findByRoleName("ROLE_BASIC")
                .orElseThrow(() -> {
                    log.error("User with provided email already exists - {}", "ROLE_BASIC");
                    return new MoneyowlApplicationException("Error: Role not found.");
                });

        // Generate 6 digit OTP
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .mobNo(request.getMobNo())
                .isActive(true)
                .isVerified(false)
                .otp(otp)
                .roles(List.of(defaultRole))
                .userType("ROLE_BASIC")
                .build();

        userRepository.save(user);

        // Send OTP
        emailService.sendOtpEmail(user.getEmail(), otp);
        log.info("End AuthenticationServiceImpl :: register");
        return "User registered successfully. Please check email for OTP Verification.";
    }

    public String verifyOtp(OtpRequest request) {
        log.info("Start AuthenticationServiceImpl :: verifyOtp");
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new MoneyowlApplicationException("User not found"));

        if (user.getOtp() != null && user.getOtp().equals(request.getOtp())) {
            user.setIsVerified(true);
            user.setOtp(null);
            userRepository.save(user);
            log.info("End AuthenticationServiceImpl :: verifyOtp");
            return "Account verified successfully. Please Login to access account";
        } else {
            log.error("Invalid OTP - {}", request.getOtp());
            throw new MoneyowlApplicationException("Invalid OTP");
        }
    }

    public LoginResponse login(LoginRequest request) {
        log.info("Start AuthenticationServiceImpl :: login");
        // 1. Authenticate via Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Check Verification status
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("User with provided email already exists - {}", request.getEmail());
                    return new MoneyowlApplicationException("User with provided email already exists");
                });

        if (!user.getIsVerified()) {
            log.error("Account not verified. Please verify your email - {}", request.getEmail());
            throw new MoneyowlApplicationException("Account not verified. Please verify your email.");
        }

        // 3. Generate Token
        User userDetails = userService.findByEmail(request.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);

        log.info("End AuthenticationServiceImpl :: login");
        return new LoginResponse(jwtToken, "Login successful");
    }
}
