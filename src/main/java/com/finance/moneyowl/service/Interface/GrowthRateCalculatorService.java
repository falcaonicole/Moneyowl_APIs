package com.finance.moneyowl.service.Interface;

import com.finance.moneyowl.generatedmodels.PortfolioResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface GrowthRateCalculatorService {

    List<PortfolioResponse> getIndividualAssetGrowthRate(Long userId, Integer duration);

    Map<String, BigDecimal> getNetWorthGrowthRate(Long userId, Integer duration);

    Map<String, BigDecimal> getNetWorthGrowthRateWithInflation(Long userId, Integer duration);

    Map<String, BigDecimal> getNetWorthGrowthRateWithAdjustedInflation(Long userId, Integer duration);
}
