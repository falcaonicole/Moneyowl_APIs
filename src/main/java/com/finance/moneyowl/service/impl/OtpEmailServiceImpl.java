package com.finance.moneyowl.service.impl;

import com.finance.moneyowl.entity.User;
import com.finance.moneyowl.exceptions.MoneyowlApplicationException;
import com.finance.moneyowl.generatedmodels.OtpCacheModel;
import com.finance.moneyowl.generatedmodels.OtpRequest;
import com.finance.moneyowl.service.interfaces.OtpService;
import com.finance.moneyowl.service.interfaces.UserService;
import com.finance.moneyowl.utils.OtpGenerator;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
@Service
public class OtpEmailServiceImpl implements OtpService {

    private final Cache<String, OtpCacheModel> otpCache;
    @Value("${otp.max-attempts}")
    int maxAttempts;
    private JavaMailSender mailSender;
    private UserService userService;

    @Override
    public String sendOtp(String email) {
        User user = userService.findByEmail(email);

        String otp = OtpGenerator.generate();
        otpCache.put(
                user.getEmail(),
                new OtpCacheModel(otp, 0, OffsetDateTime.now())
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("moneyowl11@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject("MoneyOWL Registration Email Verification OTP");
        message.setText("Your OTP for MoneyOwl verification is: " + otp);

        mailSender.send(message);
        return String.format("OTP send to %s. Please check your MailBox", user.getEmail());
    }

    @Override
    public boolean verifyOtp(OtpRequest otpRequest) {
        User user = userService.findByEmail(otpRequest.getIdentifier());
        OtpCacheModel entry = otpCache.getIfPresent(otpRequest.getIdentifier());

        if (entry == null) {
            throw new MoneyowlApplicationException("OTP expired or not found");
        }

        if (entry.getAttempts() >= maxAttempts) {
            otpCache.invalidate(otpRequest.getIdentifier());
            throw new MoneyowlApplicationException("Maximum OTP attempts exceeded. Please Try Again Later on");
        }

        if (!entry.getOtp().equals(otpRequest.getOtp())) {
            entry.setAttempts(entry.getAttempts() + 1);
            otpCache.put(otpRequest.getIdentifier(), entry);
            return false;
        }

        // Success
        otpCache.invalidate(otpRequest.getIdentifier());
        user.setVerified(true);
        return true;

    }
}
