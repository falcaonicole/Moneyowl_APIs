package com.finance.moneyowl.service.impl;

import com.finance.moneyowl.entity.Liability;
import com.finance.moneyowl.entity.Portfolio;
import com.finance.moneyowl.exceptions.MoneyowlApplicationException;
import com.finance.moneyowl.generatedmodels.SurvivalRequestDTO;
import com.finance.moneyowl.generatedmodels.SurvivalResponseDTO;
import com.finance.moneyowl.repository.LiabilityRepository;
import com.finance.moneyowl.repository.PortfolioRepository;
import com.finance.moneyowl.service.Interface.SimulatorService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class SimulatorServiceImpl implements SimulatorService {

    private final PortfolioRepository portfolioRepository;
    private final LiabilityRepository liabilityRepository;

    public SimulatorServiceImpl(
            PortfolioRepository portfolioRepository,
            LiabilityRepository liabilityRepository) {
        this.portfolioRepository = portfolioRepository;
        this.liabilityRepository = liabilityRepository;
    }

    @Override
    public SurvivalResponseDTO calculateSurvival(SurvivalRequestDTO request) {

        // 1️⃣ Validate request
        if (request.getUserId() == null) {
            throw new MoneyowlApplicationException(
                    "INVALID_REQUEST",
                    "UserId cannot be null"
            );
        }

        if (request.getMode() == null || request.getMode().isBlank()) {
            throw new MoneyowlApplicationException(
                    "INVALID_MODE",
                    "Survival mode is required"
            );
        }

        // 2️⃣ Fetch portfolio (ALWAYS)
        Portfolio portfolio = portfolioRepository
                .findByUser_UserId(request.getUserId())
                .orElseThrow(() -> new MoneyowlApplicationException(
                        "PORTFOLIO_NOT_FOUND",
                        "Portfolio not found for given user"
                ));

        // 3️⃣ Calculate liquid assets (ALWAYS)
        BigDecimal liquidAssets =
                safe(portfolio.getCashAndLiquid())
                        .add(safe(portfolio.getFixedIncome()))
                        .add(safe(portfolio.getOtherIncome()));

        if (liquidAssets.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MoneyowlApplicationException(
                    "NO_LIQUID_ASSETS",
                    "User does not have any liquid assets"
            );
        }

        SurvivalResponseDTO response = new SurvivalResponseDTO();
        response.setLiquidAssets(liquidAssets);

        // 4️⃣ Fetch monthly expenses
        List<Liability> monthlyExpenses =
                liabilityRepository.findByUser_UserIdAndStatusAndLiabilitySubType(
                        request.getUserId(),
                        "ACTIVE",
                        "MONTHLY_EXPENSE"
                );

        // 5️⃣ Set flag ONLY as indicator
        boolean hasMonthlyExpense =
                monthlyExpenses != null && !monthlyExpenses.isEmpty();
        response.setIsMonthlyExpensePresent(hasMonthlyExpense);

        // 6️⃣ Default values (ALWAYS set)
        response.setMonthlyBurn(BigDecimal.ZERO);
        response.setSurvivalMonths(BigDecimal.ZERO);
        response.setSurvivalYears(BigDecimal.ZERO);

        // 7️⃣ Calculate only if expense exists
        if (hasMonthlyExpense) {

            response.setMonthlyExpenseId(
                    monthlyExpenses.get(0).getLiabilityId()
            );

            BigDecimal baseMonthlyBurn = monthlyExpenses.stream()
                    .map(Liability::getAmount)
                    .filter(a -> a != null && a.compareTo(BigDecimal.ZERO) > 0)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (baseMonthlyBurn.compareTo(BigDecimal.ZERO) > 0) {

                BigDecimal monthlyBurn =
                        applyModeMultiplier(baseMonthlyBurn, request.getMode());

                BigDecimal survivalMonths =
                        liquidAssets.divide(monthlyBurn, 2, RoundingMode.HALF_UP);

                BigDecimal survivalYears =
                        survivalMonths.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

                response.setMonthlyBurn(monthlyBurn);
                response.setSurvivalMonths(survivalMonths);
                response.setSurvivalYears(survivalYears);
            }
        }

        return response;
    }

    private BigDecimal applyModeMultiplier(BigDecimal base, String mode) {
        return switch (mode.toUpperCase()) {
            case "ZOMBIE" -> base.multiply(BigDecimal.valueOf(0.5));
            case "LEAN" -> base;
            case "COMFORT" -> base.multiply(BigDecimal.valueOf(1.3));
            default -> throw new MoneyowlApplicationException(
                    "INVALID_MODE",
                    "Unsupported survival mode"
            );
        };
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
