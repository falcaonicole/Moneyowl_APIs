package com.finance.moneyowl.service.impl;

import com.finance.moneyowl.entity.*;
import com.finance.moneyowl.exceptions.MoneyowlApplicationException;
import com.finance.moneyowl.exceptions.ResourceNotFoundException;
import com.finance.moneyowl.generatedmodels.*;
import com.finance.moneyowl.repository.*;
import com.finance.moneyowl.service.Interface.SimulatorService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class SimulatorServiceImpl implements SimulatorService {

    private final PortfolioRepository portfolioRepository;
    private final LiabilityRepository liabilityRepository;
    private final LifeGoalRepository lifeGoalRepository;
    private final UserRepository userRepository;
    private final GoalTemplateRepository goalTemplateRepository;


    public SimulatorServiceImpl(
            LifeGoalRepository lifeGoalRepository,
            PortfolioRepository portfolioRepository,
            LiabilityRepository liabilityRepository,
            UserRepository userRepository,
            GoalTemplateRepository goalTemplateRepository ) {
        this.lifeGoalRepository = lifeGoalRepository;
        this.portfolioRepository = portfolioRepository;
        this.liabilityRepository = liabilityRepository;
        this.userRepository = userRepository;
        this.goalTemplateRepository = goalTemplateRepository;
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

    @Override
    public LifeGoalsListResponse getLifeGoals(Long userId) {

        // 1️⃣ Validate
        if (userId == null) {
            throw new MoneyowlApplicationException(
                    "INVALID_REQUEST",
                    "UserId cannot be null"
            );
        }

        // 2️⃣ Fetch goals
        List<LifeGoal> goals =
                lifeGoalRepository.findByUser_UserId(userId);

        // 3️⃣ Build response
        LifeGoalsListResponse response = new LifeGoalsListResponse();
        response.setUserId(userId.intValue());
        response.setGoals(
                goals.stream()
                        .map(this::toLifeGoalRequestDTO)
                        .toList()
        );
        return response;
    }


    private LifeGoalRequestDTO toLifeGoalRequestDTO(LifeGoal goal) {

        LifeGoalRequestDTO dto = new LifeGoalRequestDTO();

        dto.setGoalId(goal.getLifeGoalId().toString()); // ✅ IMPORTANT
        dto.setTemplateId(goal.getTemplateId());
        dto.setName(goal.getName());
        dto.setIcon(goal.getIcon());
        dto.setCost(goal.getCost());
        dto.setYear(goal.getTargetYear());
        dto.setIsLoan(goal.getIsLoan());
        dto.setRoi(goal.getRoi());
        dto.setLoanDuration(goal.getLoanDuration());
        dto.setUserId(goal.getUser().getUserId());

        return dto;
    }


    @Override
    public LifeGoalResponseDTO createLifeGoal(LifeGoalRequestDTO request) {

        // 1️⃣ Validate request
        if (request.getUserId() == null) {
            throw new MoneyowlApplicationException(
                    "INVALID_REQUEST",
                    "UserId cannot be null"
            );
        }

        if (request.getTemplateId() == null || request.getTemplateId().isBlank()) {
            throw new MoneyowlApplicationException(
                    "INVALID_TEMPLATE",
                    "TemplateId is required"
            );
        }

        if (request.getCost() == null || request.getCost().compareTo(BigDecimal.ZERO) <= 0) {
            throw new MoneyowlApplicationException(
                    "INVALID_COST",
                    "Cost must be greater than zero"
            );
        }

        if (request.getYear() == null || request.getYear() <= 0) {
            throw new MoneyowlApplicationException(
                    "INVALID_YEAR",
                    "Target year must be valid"
            );
        }

        // 2️⃣ Fetch User (MANDATORY)
        User user = userRepository
                .findById(request.getUserId())
                .orElseThrow(() -> new MoneyowlApplicationException(
                        "USER_NOT_FOUND",
                        "User not found"
                ));

        // 3️⃣ Map DTO → Entity
        LifeGoal goal = new LifeGoal();
        goal.setUser(user);
        goal.setTemplateId(request.getTemplateId());
        goal.setName(request.getName());
        goal.setIcon(request.getIcon());
        goal.setCost(request.getCost());
        goal.setTargetYear(request.getYear());
        goal.setIsLoan(request.getIsLoan());
        goal.setRoi(request.getRoi());
        goal.setLoanDuration(request.getLoanDuration());

        // 4️⃣ Persist
        goal = lifeGoalRepository.save(goal);

        // 5️⃣ Response
        LifeGoalResponseDTO response = new LifeGoalResponseDTO();
        response.setId(goal.getLifeGoalId().toString());

        return response;
    }

    @Override
    public void updateLifeGoal(String goalId, LifeGoalRequestDTO request) {

        if (goalId == null || goalId.isBlank()) {
            throw new MoneyowlApplicationException(
                    "INVALID_GOAL_ID",
                    "GoalId cannot be null or empty"
            );
        }

        LifeGoal goal = lifeGoalRepository.findById(
                UUID.fromString(goalId)
        ).orElseThrow(() -> new MoneyowlApplicationException(
                "GOAL_NOT_FOUND",
                "Life goal not found"
        ));

        // 🔁 Update allowed fields only
        if (request.getTemplateId() != null)
            goal.setTemplateId(request.getTemplateId());

        if (request.getName() != null)
            goal.setName(request.getName());

        if (request.getIcon() != null)
            goal.setIcon(request.getIcon());

        if (request.getCost() != null)
            goal.setCost(request.getCost());

        if (request.getYear() != null)
            goal.setTargetYear(request.getYear());

        if (request.getIsLoan() != null)
            goal.setIsLoan(request.getIsLoan());

        if (request.getRoi() != null)
            goal.setRoi(request.getRoi());

        if (request.getLoanDuration() != null)
            goal.setLoanDuration(request.getLoanDuration());

        lifeGoalRepository.save(goal);
    }

    @Override
    public void deleteLifeGoal(String goalId) {

        UUID uuid = UUID.fromString(goalId);

        LifeGoal goal = lifeGoalRepository.findById(uuid)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Life goal not found with id: " + goalId));

        lifeGoalRepository.delete(goal);
    }

    @Override
    public GoalTemplateListResponse getGoalTemplates() {

        List<GoalTemplate> templates = goalTemplateRepository.findAll();

        GoalTemplateListResponse response = new GoalTemplateListResponse();
        response.setTemplates(
                templates.stream()
                        .map(this::toGoalTemplateDTO)
                        .toList()
        );

        return response;
    }

    @Override
    public GoalTemplateDTO getGoalTemplateById(String templateId) {

        if (templateId == null || templateId.isBlank()) {
            throw new MoneyowlApplicationException(
                    "INVALID_TEMPLATE_ID",
                    "TemplateId cannot be null or empty"
            );
        }

        GoalTemplate template = goalTemplateRepository
                .findById(templateId)
                .orElseThrow(() -> new MoneyowlApplicationException(
                        "TEMPLATE_NOT_FOUND",
                        "Goal template not found"
                ));

        return toGoalTemplateDTO(template);
    }

    private GoalTemplateDTO toGoalTemplateDTO(GoalTemplate template) {

        GoalTemplateDTO dto = new GoalTemplateDTO();
        dto.setTemplateId(template.getTemplateId());
        dto.setName(template.getName());
        dto.setIcon(template.getIcon());
        dto.setDefaultRoi(template.getDefaultRoi());
        dto.setIsLoanAllowed(template.getIsLoanAllowed());

        return dto;
    }
}
