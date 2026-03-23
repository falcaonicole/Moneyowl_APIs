package com.finance.moneyowl.configuration;

import com.finance.moneyowl.generatedmodels.OtpCacheModel;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OtpCacheConfig {

    @Value("${otp.expiry-minutes}")
    Long expInMin;

    @Bean
    public Cache<String, OtpCacheModel> otpCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(expInMin))
                .maximumSize(100_000)
                .build();
    }
}
