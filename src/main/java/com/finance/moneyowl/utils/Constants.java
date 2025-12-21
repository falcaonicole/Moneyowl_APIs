package com.finance.moneyowl.utils;

import com.finance.moneyowl.enums.AssetClass;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;

public class Constants {
    public static String USER_NOT_FOUND = "User with requested ID is not found ";

    public static final Map<AssetClass, BigDecimal> ASSET_GROWTH_RATES = Map.of(
            AssetClass.EQUITY, new BigDecimal("12.00"),
            AssetClass.DEBT, new BigDecimal("7.00"),
            AssetClass.CASH_AND_LIQUID, new BigDecimal("4.00"),
            AssetClass.GOLD, new BigDecimal("5.50"),
            AssetClass.REAL_ESTATE, new BigDecimal("9.00"),
            AssetClass.FIXED_INCOME, new BigDecimal("6.50")
    );
    public static final MathContext MC = new MathContext(14, RoundingMode.HALF_UP);
    public static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    public static final int MONTHS_IN_YEAR = 12;



}


