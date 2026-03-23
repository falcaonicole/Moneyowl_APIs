package com.finance.moneyowl.utils;

import java.security.SecureRandom;

public final class OtpGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private OtpGenerator() {
    }

    public static String generate() {
        return String.valueOf(100000 + RANDOM.nextInt(999999));
    }
}