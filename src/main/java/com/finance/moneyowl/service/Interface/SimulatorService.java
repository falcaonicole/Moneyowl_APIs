package com.finance.moneyowl.service.Interface;

import com.finance.moneyowl.generatedmodels.*;

public interface SimulatorService {
    SurvivalResponseDTO calculateSurvival(SurvivalRequestDTO request);
    LifeGoalsListResponse getLifeGoals(Long userId);
    LifeGoalResponseDTO createLifeGoal(LifeGoalRequestDTO request);
    void updateLifeGoal(String goalId, LifeGoalRequestDTO request);
}

