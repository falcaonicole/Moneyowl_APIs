package com.finance.moneyowl.service.impl;

import com.finance.moneyowl.entity.User;
import com.finance.moneyowl.exceptions.MoneyowlApplicationException;
import com.finance.moneyowl.generatedmodels.OtpCacheModel;
import com.finance.moneyowl.generatedmodels.OtpRequest;
import com.finance.moneyowl.service.interfaces.OtpService;
import com.finance.moneyowl.service.interfaces.UserService;
import com.finance.moneyowl.utils.OtpGenerator;
import com.github.benmanes.caffeine.cache.Cache;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class OtpMobNoServiceImpl implements OtpService {

    private final Cache<String, OtpCacheModel> otpCache;
    @Value("${otp.max-attempts}")
    int maxAttempts;
    @Value("${twilio.from-number}")
    private String fromNumber;
    private UserService userService;


    @Override
    public String sendOtp(String mobNo) {
        String otp = OtpGenerator.generate();
        otpCache.put(
                mobNo,
                new OtpCacheModel(otp, 0, OffsetDateTime.now())
        );

        Message.creator(
                new PhoneNumber("+91" + mobNo),
                new PhoneNumber(fromNumber),
                "Your MoneyOWL login OTP is: " + otp + ". Valid for 5 minutes."
        ).create();
        return String.format("OTP send to %s. Please check your SMS", mobNo);
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
