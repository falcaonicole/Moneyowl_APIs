package com.finance.moneyowl.utils;

import com.finance.moneyowl.enums.AssetClass;
import com.finance.moneyowl.generatedmodels.PortfolioResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@AllArgsConstructor
@Component
public class GrthRateCalHelperMethods {
    //Helper Methods
    public static BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    public static void mapToResponse(PortfolioResponse response, AssetClass asset, BigDecimal value) {
        switch (asset) {
            case EQUITY -> response.setEquity(value);
            case DEBT -> response.setDebt(value);
            case CASH_AND_LIQUID -> response.setCashAndLiquid(value);
            case GOLD -> response.setGold(value);
            case REAL_ESTATE -> response.setRealEstate(value);
            case FIXED_INCOME -> response.setFixedIncome(value);
            case OTHER_INCOME -> response.setOtherIncome(value);
        }
    }

}
