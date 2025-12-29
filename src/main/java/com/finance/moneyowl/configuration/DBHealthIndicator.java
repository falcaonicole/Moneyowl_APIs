package com.finance.moneyowl.configuration;

import com.finance.moneyowl.exceptions.MoneyowlApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;

@Component
@Slf4j
public class DBHealthIndicator implements HealthIndicator {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Override
    public Health health() {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            return Health.up().withDetail("Database", "Connected Successfully").build();
        } catch (Exception e) {
            log.error("DB Connection Failed {}", e.getMessage());
            throw new MoneyowlApplicationException("DB Connection Failed");
        }
    }
}
