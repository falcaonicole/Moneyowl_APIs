package com.finance.moneyowl.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.moneyowl.enums.AssetClass;
import com.finance.moneyowl.exceptions.MoneyowlApplicationException;
import com.finance.moneyowl.exceptions.ResourceNotFoundException;
import com.finance.moneyowl.generatedmodels.LiabilityDTO;
import com.finance.moneyowl.generatedmodels.PortfolioDTO;
import com.finance.moneyowl.generatedmodels.PortfolioResponse;
import com.finance.moneyowl.service.Interface.GrowthGraphCalculatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.finance.moneyowl.utils.Constants.*;
import static com.finance.moneyowl.utils.GrthRateCalHelperMethods.mapToResponse;
import static com.finance.moneyowl.utils.GrthRateCalHelperMethods.safe;


@Service
@Slf4j
public class GrowthGraphCalculatorServiceImpl implements GrowthGraphCalculatorService {

    @Autowired
    private UserServiceImpl
            userService;
    private final ChatClient chatClient;

    @Autowired
    private ObjectMapper objectMapper;

    public GrowthGraphCalculatorServiceImpl(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultTools("getUserPortfolioData", "getUserLiabilities")
                .build();
    }


    @Override
    public List<PortfolioResponse> getIndividualAssetGrowthRate(Long userId, Integer years) {
        log.info("Start GrowthGraphCalculatorServiceImpl :: getIndividualAssetGrowthRate");
        PortfolioDTO userPortfolio = userService.getUserPortfolio(userId);
        List<PortfolioResponse> portfolioResponses = new ArrayList<>();

        Map<AssetClass, BigDecimal> values = new EnumMap<>(AssetClass.class);
        values.put(AssetClass.EQUITY, safe(userPortfolio.getEquity()));
        values.put(AssetClass.DEBT, safe(userPortfolio.getDebt()));
        values.put(AssetClass.CASH_AND_LIQUID, safe(userPortfolio.getCashAndLiquid()));
        values.put(AssetClass.GOLD, safe(userPortfolio.getGold()));
        values.put(AssetClass.REAL_ESTATE, safe(userPortfolio.getRealEstate()));
        values.put(AssetClass.FIXED_INCOME, safe(userPortfolio.getFixedIncome()));
        values.put(AssetClass.OTHER_INCOME, safe(userPortfolio.getOtherIncome()));

        for (int year = 1; year <= years; year++) {
            PortfolioResponse response = new PortfolioResponse();
            for (var entry : values.entrySet()) {
                AssetClass asset = entry.getKey();
                BigDecimal startValue = entry.getValue();
                BigDecimal rate = ASSET_GROWTH_RATES.getOrDefault(asset, BigDecimal.ZERO);
                BigDecimal endValue = startValue.multiply(
                        BigDecimal.ONE.add(rate.divide(ONE_HUNDRED, MC)),
                        MC
                ).setScale(2, RoundingMode.HALF_UP);
                entry.setValue(endValue);
                mapToResponse(response, asset, endValue);
            }
            portfolioResponses.add(response);
        }
        log.info("End GrowthGraphCalculatorServiceImpl :: getIndividualAssetGrowthRate");
        return portfolioResponses;
    }


    @Override
    @Transactional
    public Map<String, BigDecimal> getNetWorthGrowthRate(Long userId, Integer duration) {
        log.info("Start GrowthGraphCalculatorServiceImpl :: getNetWorthGrowthRate");
        // 1. Fetch Data
        if (!userService.existsById(userId)) {
            log.error("User with Requested Id does not Exists");
            throw new ResourceNotFoundException("User with Requested Id does not Exists");
        }
        Map<Integer, BigDecimal> totalAssets = getProjectedYearEndAssetValue(userId, duration);
        Map<Integer, BigDecimal> totalLiabilities = getProjectedYearEndLiability(userId, duration);
        if (totalAssets == null) totalAssets = new HashMap<>();
        if (totalLiabilities == null) totalLiabilities = new HashMap<>();
        Map<String, BigDecimal> yoyNetWorth = new LinkedHashMap<>();
        for (Integer year : totalAssets.keySet()) {
            BigDecimal assetValue = totalAssets.get(year);
            if (assetValue == null) assetValue = BigDecimal.ZERO;
            BigDecimal liabilityValue = totalLiabilities.get(year);
            if (liabilityValue == null) liabilityValue = BigDecimal.ZERO;
            BigDecimal netWorth = assetValue.subtract(liabilityValue);
            yoyNetWorth.put(year.toString(), netWorth);
        }
        log.info("End GrowthGraphCalculatorServiceImpl :: getNetWorthGrowthRate");
        return yoyNetWorth;
    }

    @Override
    @Transactional
    public Map<String, BigDecimal> getNetWorthGrowthRateWithInflation(Long userId, Integer duration) {
        log.info("Start GrowthGraphCalculatorServiceImpl :: getNetWorthGrowthRateWithInflation");

        if (!userService.existsById(userId)) {
            log.error("User with Requested Id does not Exists");
            throw new ResourceNotFoundException("User with Requested Id does not Exists");
        }
        Map<String, BigDecimal> nominalNetWorthMap = getNetWorthGrowthRate(userId, duration);

        Map<String, BigDecimal> realNetWorthMap = new LinkedHashMap<>();

        // Fixed Inflation Rate of 4%
        BigDecimal inflationRate = new BigDecimal("0.04");
        int baseYear = LocalDate.now().getYear();

        for (Map.Entry<String, BigDecimal> entry : nominalNetWorthMap.entrySet()) {
            int projectedYear;
            try {
                projectedYear = Integer.parseInt(entry.getKey());
            } catch (NumberFormatException e) {
                continue;
            }
            BigDecimal nominalValue = entry.getValue();
            int n = projectedYear - baseYear;

            // If n is 0 or negative (current year or past), the value is already in "current terms"
            // or we simply don't discount backwards.
            if (n <= 0) {
                realNetWorthMap.put(entry.getKey(), nominalValue);
                continue;
            }

            // 2. Apply Inflation Discounting Logic
            // Discount Factor = (1 + 0.04) ^ n
            BigDecimal onePlusRate = BigDecimal.ONE.add(inflationRate);
            BigDecimal discountFactor = onePlusRate.pow(n, MC);

            // Real Value = Nominal Value / Discount Factor
            BigDecimal realValue = nominalValue.divide(discountFactor, MC)
                    .setScale(2, RoundingMode.HALF_UP);

            realNetWorthMap.put(entry.getKey(), realValue);
        }
        log.info("End GrowthGraphCalculatorServiceImpl :: getNetWorthGrowthRateWithInflation");
        return realNetWorthMap;
    }

    @Override
    @Transactional
    public Map<String, BigDecimal> getNetWorthGrowthRateWithAdjustedInflation(Long userId, Integer duration) {
        log.info("Start GrowthGraphCalculatorServiceImpl :: getNetWorthGrowthRateWithAdjustedInflation");
        if (!userService.existsById(userId)) {
            log.error("User with Requested Id does not Exists");
            throw new ResourceNotFoundException("User with Requested Id does not Exists");
        }
        return getNetWorthGrowthRateWithAdjustedInflation(userId, duration, new BigDecimal("0.04"));
    }

    public Map<String, BigDecimal> getNetWorthGrowthRateWithAdjustedInflation(Long userId, Integer duration, BigDecimal inflationRate) {
        // 1. Get the Nominal Net Worth (Total accumulated amount per year)
        Map<String, BigDecimal> nominalNetWorthMap = getNetWorthGrowthRate(userId, duration);

        // 2. Sort the data by Year (Integer) to ensure chronological order.
        // This is required to correctly calculate the difference between Year T and Year T-1.
        TreeMap<Integer, BigDecimal> sortedNominalMap = new TreeMap<>();
        for (Map.Entry<String, BigDecimal> entry : nominalNetWorthMap.entrySet()) {
            try {
                sortedNominalMap.put(Integer.parseInt(entry.getKey()), entry.getValue());
            } catch (NumberFormatException e) {
                log.error("Skipping Invalid Year Key {}", entry.getKey());
            }
        }
        Map<String, BigDecimal> realGrowthAmountMap = new LinkedHashMap<>();

        int baseYear = LocalDate.now().getYear();

        BigDecimal onePlusInflation = BigDecimal.ONE.add(inflationRate);
        BigDecimal previousRealNetWorth = BigDecimal.ZERO;
        boolean isFirstRecord = true;

        for (Map.Entry<Integer, BigDecimal> entry : sortedNominalMap.entrySet()) {
            int currentYear = entry.getKey();
            BigDecimal nominalTotal = entry.getValue();
            BigDecimal currentRealNetWorth;

            int yearsFromBase = currentYear - baseYear;

            if (yearsFromBase <= 0) {
                currentRealNetWorth = nominalTotal;
            } else {
                BigDecimal discountFactor = onePlusInflation.pow(yearsFromBase, MathContext.DECIMAL64);
                currentRealNetWorth = nominalTotal.divide(discountFactor, 2, RoundingMode.HALF_UP);
            }
            BigDecimal realGrowthAmount;

            if (isFirstRecord) {
                realGrowthAmount = currentRealNetWorth;
                isFirstRecord = false;
            } else {
                realGrowthAmount = currentRealNetWorth.subtract(previousRealNetWorth);
            }

            realGrowthAmountMap.put(String.valueOf(currentYear), realGrowthAmount.setScale(2, RoundingMode.HALF_UP));
            previousRealNetWorth = currentRealNetWorth;
        }
        log.info("End GrowthGraphCalculatorServiceImpl :: getNetWorthGrowthRateWithAdjustedInflation");
        return realGrowthAmountMap;
    }

    //Helper Methods
    public Map<Integer, BigDecimal> getProjectedYearEndAssetValue(Long userId, int years) {
        log.info("Start GrowthGraphCalculatorServiceImpl :: getProjectedYearEndAssetValue");
        PortfolioDTO userPortfolio = userService.getUserPortfolio(userId);
        Integer currentYear = LocalDate.now().getYear();
        Map<AssetClass, BigDecimal> values = new EnumMap<>(AssetClass.class);
        values.put(AssetClass.EQUITY, safe(userPortfolio.getEquity()));
        values.put(AssetClass.DEBT, safe(userPortfolio.getDebt()));
        values.put(AssetClass.CASH_AND_LIQUID, safe(userPortfolio.getCashAndLiquid()));
        values.put(AssetClass.GOLD, safe(userPortfolio.getGold()));
        values.put(AssetClass.REAL_ESTATE, safe(userPortfolio.getRealEstate()));
        values.put(AssetClass.FIXED_INCOME, safe(userPortfolio.getFixedIncome()));
        Map<Integer, BigDecimal> projectedValues = new LinkedHashMap<>();

        for (int year = 1; year <= years; year++) {
            BigDecimal yearEndAssetValue = BigDecimal.ZERO;
            for (var entry : values.entrySet()) {
                AssetClass asset = entry.getKey();
                BigDecimal startValue = entry.getValue();
                BigDecimal rate = ASSET_GROWTH_RATES.getOrDefault(asset, BigDecimal.ZERO);
                BigDecimal endValue = startValue
                        .multiply(BigDecimal.ONE.add(rate.divide(ONE_HUNDRED, MC)), MC)
                        .setScale(2, RoundingMode.HALF_UP);

                entry.setValue(endValue);
                yearEndAssetValue = yearEndAssetValue.add(endValue);
            }
            projectedValues.put(currentYear, yearEndAssetValue);
            currentYear++;
        }
        log.info("End GrowthGraphCalculatorServiceImpl :: getProjectedYearEndAssetValue");
        return projectedValues;
    }

    //LiabilityCalculators
    public Map<Integer, BigDecimal> getProjectedYearEndLiability(Long userId, int numberOfYears) {
        log.info("Start GrowthGraphCalculatorServiceImpl :: getProjectedYearEndLiability");
        // 1. Fetch Active Liabilities
        List<LiabilityDTO> liabilities = userService.getAllUserLiabilities(userId);

        Map<Integer, BigDecimal> projectedLiabilities = new LinkedHashMap<>();
        int currentYear = LocalDate.now().getYear(); // 2025

        // SAFETY CHECK 1: If the List itself is null or empty, return empty map immediately
        // This saves processing time for users with no debt.
        if (liabilities == null || liabilities.isEmpty()) {
            // Pre-fill with Zeros if you want the graph to show flat line,
            // or return empty if you want no graph.
            // Here returning empty as per your logic.
            return projectedLiabilities;
        }

        // 2. Loop through future years
        for (int i = 1; i <= numberOfYears; i++) {
            int targetYear = currentYear + i;
            LocalDate targetDate = LocalDate.of(targetYear, 12, 31); // Snapshot on Dec 31

            BigDecimal totalLiabilityForYear = BigDecimal.ZERO;

            for (LiabilityDTO liability : liabilities) {

                // SAFETY CHECK 2: Handle Null Liability Object inside List
                if (liability == null) continue;

                // SAFETY CHECK 3: Handle Null Critical Fields
                // If Start Date is missing, we can't calculate logic. Skip it.
                if (liability.getStartDate() == null) continue;

                // Logic: Skip if liability starts in the future relative to target date
                if (liability.getStartDate().isAfter(targetDate)) continue;

                // Calculate Balance
                BigDecimal outstandingBalance = calculateOutstandingBalance(liability, targetDate);

                // SAFETY CHECK 4: Handle Null Return from Calculation
                // If calculateOutstandingBalance returns null (error case), treat as 0
                if (outstandingBalance != null) {
                    totalLiabilityForYear = totalLiabilityForYear.add(outstandingBalance);
                }
            }

            // Store formatted value
            projectedLiabilities.put(targetYear, totalLiabilityForYear.setScale(2, RoundingMode.HALF_UP));
        }
        log.info("End GrowthGraphCalculatorServiceImpl :: getProjectedYearEndLiability");
        return projectedLiabilities;
    }

    /**
     * Routing Logic: Decides which financial formula to use
     */
    private BigDecimal calculateOutstandingBalance(LiabilityDTO liability, LocalDate targetDate) {
        log.info("Start GrowthGraphCalculatorServiceImpl :: calculateOutstandingBalance");
        // If target date is past the loan end date, liability is 0 (Assuming paid off)
        if (targetDate.isAfter(liability.getEndDate())) {
            return BigDecimal.ZERO;
        }

        // Logic Switch based on Sub-Type
        switch (liability.getLiabilitySubType()) {
            case "HOME_LOAN":
            case "VEHICLE_LOAN":
            case "PERSONAL_LOAN_LONG_TERM":
            case "BUSINESS_INVESTMENT_LOAN":
            case "EDUCATION_LOAN":
            case "LEASE_LIABILITIES":
            case "BNPL":
                return getAmortizedBalance(liability, targetDate);

            case "SHORT_TERM_LOANS":
            case "CREDIT_CARD_DEBT":
            case "MONTHLY_FIXED_EXPENSES":
                return getBulletRepaymentBalance(liability, targetDate);

            default:
                return liability.getAmount();
        }
    }

    // =========================================================================
    // FINANCIAL FORMULAS (The Core Logic)
    // =========================================================================

    /**
     * SCENARIO A: EMI Based Loans (Principal decreases monthly)
     * Formula: B = P * [ (1+r)^n - (1+r)^p ] / [ (1+r)^n - 1 ]
     * P: Principal, r: Monthly Rate, n: Total Months, p: Months Paid
     */
    private BigDecimal getAmortizedBalance(LiabilityDTO l, LocalDate targetDate) {
        log.info("Start GrowthGraphCalculatorServiceImpl :: getAmortizedBalance");
        BigDecimal P = l.getAmount();

        // Calculate 'r' (Monthly ROI)
        BigDecimal annualRate = l.getRoi().divide(ONE_HUNDRED, MC);
        BigDecimal r = annualRate.divide(new BigDecimal(MONTHS_IN_YEAR), MC);

        // Calculate 'n' (Total Tenure in Months)
        long totalMonths = ChronoUnit.MONTHS.between(l.getStartDate(), l.getEndDate());
        if (totalMonths == 0) return P; // Safety check

        // Calculate 'p' (Months passed from Start Date to Target Date)
        long monthsPassed = ChronoUnit.MONTHS.between(l.getStartDate(), targetDate);

        // Edge Case: If we are calculating for a date before the loan starts (unlikely but safe)
        if (monthsPassed < 0) return P;

        // Math Power Calculations
        BigDecimal onePlusR = BigDecimal.ONE.add(r);
        BigDecimal onePlusR_Pow_N = onePlusR.pow((int) totalMonths, MC);
        BigDecimal onePlusR_Pow_P = onePlusR.pow((int) monthsPassed, MC);

        // Numerator: (1+r)^n - (1+r)^p
        BigDecimal numerator = onePlusR_Pow_N.subtract(onePlusR_Pow_P);

        // Denominator: (1+r)^n - 1
        BigDecimal denominator = onePlusR_Pow_N.subtract(BigDecimal.ONE);

        // Result: P * (Num / Denom)
        BigDecimal fraction = numerator.divide(denominator, MC);
        BigDecimal balance = P.multiply(fraction, MC);
        log.info("End GrowthGraphCalculatorServiceImpl :: getAmortizedBalance");
        return balance.max(BigDecimal.ZERO); // Ensure never negative
    }

    /**
     * SCENARIO B: Bullet / Balloon Loans (Interest Compounds, No Monthly Payment)
     * Formula: A = P * (1 + r)^t
     * The liability GROWS until the end date.
     */
    private BigDecimal getBulletRepaymentBalance(LiabilityDTO l, LocalDate targetDate) {
        log.info("Start GrowthGraphCalculatorServiceImpl :: getBulletRepaymentBalance");
        BigDecimal P = l.getAmount();
        BigDecimal annualRate = l.getRoi().divide(ONE_HUNDRED, MC);

        // Time in Years (fractional)
        long daysPassed = ChronoUnit.DAYS.between(l.getStartDate(), targetDate);
        double years = daysPassed / 365.25;

        // Math: (1 + r) ^ years
        // Note: BigDecimal doesn't support double exponents easily, using Math.pow for the factor
        double rateFactorDouble = Math.pow(1 + annualRate.doubleValue(), years);
        BigDecimal rateFactor = new BigDecimal(rateFactorDouble, MC);
        log.info("End GrowthGraphCalculatorServiceImpl :: getBulletRepaymentBalance");
        return P.multiply(rateFactor, MC);
    }

    //AI Based Methods
    public String getAINetGrowthAmount(Long userId, Integer duration) {
        log.info("Start GrowthGraphCalculatorServiceImpl :: getAINetGrowthAmount");

        try {
            String userPortfolio = objectMapper.writeValueAsString(userService.getUserPortfolio(userId));
            String userLiabilities = objectMapper.writeValueAsString(userService.getAllUserLiabilities(userId));

            String promptTemplate = """
                    SYSTEM INSTRUCTION: COMPUTE INTERNALLY. DO NOT USE TOOLS.
                    You are a closed-system Financial Calculation Engine.
                    - You are STRICTLY FORBIDDEN from using search, code interpreters, or external tools.
                    - Use the provided variables to perform the math.
                
                    You are an expert Financial Analyst in India.
                    DATA CONTEXT:
                    User's Portfolio (Current Assets): {userPortfolio}
                    User's Liabilities (Current Loans/Payables): {userLiabilities}
                    Simulation Duration: {duration} years
                    CALCULATION PARAMETERS (Annual Growth Rates in %):
                    Equity: 12
                    Debt: 7
                    Cash & Liquid: 5
                    Gold: 5
                    Real Estate: 6
                    Liabilities: Compound strictly using the individual roi, start_date, and end_date provided for each liability.
                    TASK:
                    Calculate the Net Asset Growth Value for each year from the current year up to year {duration} .
                    LOGIC STEPS:
                    Compute the future value of each asset class annually using the specified growth rates.
                    Compute the future value of each liability based on its own ROI and active duration.
                    For each year, calculate:
                    Net Asset Value = Total Future Assets − Total Future Liabilities
                    OUTPUT RULES:
                            Strictly Do NOT include markdown, explanations, calculations or additional text.
                            Output MUST be strict valid JSON with Key-Value Pairs.
                            Output MUST be a single JSON object.
                            Keys MUST be the calendar year as a string.
                            Values MUST be the Net Asset Value as BigDecimal (2 decimal places).
                            EXAMPLE OUTPUT FORMAT in JSON:
                            "2026": 14500000.00,
                            "2027": 15820000.75
                """;

            log.info("End GrowthGraphCalculatorServiceImpl :: getAINetGrowthAmount");

            Map<String, BigDecimal> resultMap = chatClient.prompt()
                    .user(u -> u.text(promptTemplate)
                            .param("userPortfolio", userPortfolio)
                            .param("userLiabilities", userLiabilities)
                            .param("duration", duration))
                    .call()
                    .entity(new ParameterizedTypeReference<Map<String, BigDecimal>>() {
                    });
            return objectMapper.writeValueAsString(resultMap);
        } catch (Exception e) {
            log.error("Error occurred while returning AI Response {}", e.getMessage());
            throw new MoneyowlApplicationException("The AI agent is currently overloaded or the Daily Request Quota is Exhausted. Please try again later.");
        }
    }

    public String getAINetGrowthAmountWithInflation(Long userId, Integer duration) {
        log.info("Start GrowthGraphCalculatorServiceImpl :: getAINetGrowthAmountWithInflation");

        try {
            String userPortfolio = objectMapper.writeValueAsString(userService.getUserPortfolio(userId));
            String userLiabilities = objectMapper.writeValueAsString(userService.getAllUserLiabilities(userId));

            String promptTemplate = """
                    SYSTEM INSTRUCTION: COMPUTE INTERNALLY. DO NOT USE TOOLS.
                    You are a closed-system Financial Calculation Engine.
                    - You are STRICTLY FORBIDDEN from using search, code interpreters, or external tools.
                    - Use the provided variables to perform the math.
                
                    You are an expert Financial Analyst in India.
                    Do not use any Tools to fetch any Context Data , as I have already provided you with the required Context Data.
                    DATA CONTEXT:
                    User's Portfolio (Current Assets): {userPortfolio}
                    User's Liabilities (Current Loans/Payables): {userLiabilities}
                    Simulation Duration: {duration} years
                    Fixed Annual Inflation Rate: 4.0%
                    CALCULATION PARAMETERS (Annual Growth Rates in %):
                    Equity: 12
                    Debt: 7
                    Cash & Liquid: 5
                    Gold: 5
                    Real Estate: 6
                    Liabilities: Compound strictly using the individual roi, start_date, and end_date provided for each liability.
                    TASK:
                    Calculate the Inflation-Adjusted Net Asset Growth Value (Real Value) for each year from now up to year {duration}, assuming a constant annual inflation rate of 4%.
                    LOGIC STEPS:
                    Compute the future value of each asset class annually using nominal growth rates.
                    Compute the future value of liabilities using their defined ROI and tenure.
                    Calculate Net Worth for each year:
                    Net Worth = Total Assets − Total Liabilities
                    Convert Net Worth into Real Value using fixed inflation:
                    RealValue = NominalNetWorth / ((1 + 0.04) ^ year_index)
                    OUTPUT RULES:
                            Strictly Do NOT include markdown, explanations, calculations or additional text.
                            Output MUST be strict valid JSON with Key-Value Pairs.
                            Output MUST be a single JSON object.
                            Keys MUST be the calendar year as a string.
                            Values MUST be the Net Asset Value as BigDecimal (2 decimal places).
                            EXAMPLE OUTPUT FORMAT in JSON:
                            "2026": 14500000.00,
                            "2027": 15820000.75
                """;

            log.info("End GrowthGraphCalculatorServiceImpl :: getAINetGrowthAmountWithInflation");

            Map<String, BigDecimal> resultMap = chatClient.prompt()
                    .user(u -> u.text(promptTemplate)
                            .param("userPortfolio", userPortfolio)
                            .param("userLiabilities", userLiabilities)
                            .param("duration", duration))
                    .call()
                    .entity(new ParameterizedTypeReference<Map<String, BigDecimal>>() {
                    });
            return objectMapper.writeValueAsString(resultMap);
        } catch (Exception e) {
            log.error("Error occurred while returning AI Response {}", e.getMessage());
            throw new MoneyowlApplicationException("The AI agent is currently overloaded or the Daily Request Quota is Exhausted. Please try again later.");
        }
    }

    public String getAINetGrowthAmountWithAdjustedInflation(Long userId, Integer duration) {
        log.info("Start GrowthGraphCalculatorServiceImpl :: getAINetGrowthAmountWithAdjustedInflation");
        try {
            String userPortfolio = objectMapper.writeValueAsString(userService.getUserPortfolio(userId));
            String userLiabilities = objectMapper.writeValueAsString(userService.getAllUserLiabilities(userId));

            String promptTemplate = """
                        SYSTEM INSTRUCTION: COMPUTE INTERNALLY. DO NOT USE TOOLS.
                        You are a closed-system Financial Calculation Engine.
                         - You are STRICTLY FORBIDDEN from using search, code interpreters, or external tools.
                         - Use the provided variables to perform the math.
                    
                        You are an expert Financial Analyst in India.
                        Do not use any Tools to fetch any Context Data , as I have already provided you with the required Context Data.
                        DATA CONTEXT:
                        User's Portfolio (Current Assets): {userPortfolio}
                        User's Liabilities (Current Loans/Payables): {userLiabilities}
                        Simulation Duration: {duration} years
                        Fixed Annual Inflation Rate: 4.0%
                        CALCULATION PARAMETERS:
                        Nominal Annual Growth Rates (%):
                        Equity: 12
                        Debt: 7
                        Cash & Liquid: 5
                        Gold: 5
                        Real Estate: 6
                        Liabilities:
                        Compound using the individual roi, start_date, and end_date provided.
                        Liability growth is NOT inflation-adjusted separately; its real impact is reflected through net worth.
                        TASK:
                        Calculate the Net Asset Growth Value using Inflation-Adjusted (Real) Growth Rates for each year up to {duration}.
                        LOGIC STEPS:
                        Convert nominal asset growth rates into real growth rates using:
                        RealGrowthRate = ((1 + NominalRate) / (1 + 0.04)) − 1
                        Apply these real growth rates directly while projecting asset values year-by-year.
                        Compute future liability values using their defined nominal ROI.
                        For each year, calculate:
                        Real Net Asset Value = Total Real Asset Value − Total Liability Value
                        Do NOT apply any further inflation discounting after this step.
                    
                        OUTPUT RULES:
                            Strictly Do NOT include markdown, explanations, calculations or additional text.
                            Output MUST be strict valid JSON with Key-Value Pairs.
                        Output MUST be a single JSON object.
                        Keys MUST be the calendar year as a string.
                        Values MUST be the Net Asset Value as BigDecimal (2 decimal places).
                    
                            EXAMPLE OUTPUT FORMAT in JSON:
                        "2026": 14500000.00,
                        "2027": 15820000.75
                """;

            log.info("End GrowthGraphCalculatorServiceImpl :: getAINetGrowthAmountWithAdjustedInflation");

            Map<String, BigDecimal> resultMap = chatClient.prompt()
                    .user(u -> u.text(promptTemplate)
                            .param("userPortfolio", userPortfolio)
                            .param("userLiabilities", userLiabilities)
                            .param("duration", duration))
                    .call()
                    .entity(new ParameterizedTypeReference<Map<String, BigDecimal>>() {
                    });
            return objectMapper.writeValueAsString(resultMap);
        } catch (Exception e) {
            log.error("Error occurred while returning AI Response {}", e.getMessage());
            throw new MoneyowlApplicationException("The AI agent is currently overloaded or the Daily Request Quota is Exhausted. Please try again later.");
        }
    }
}
