package com.finance.moneyowl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MoneyowlApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyowlApplication.class, args);
    }

}
