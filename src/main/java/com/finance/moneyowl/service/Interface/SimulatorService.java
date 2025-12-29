package com.finance.moneyowl.service.Interface;

import com.finance.moneyowl.generatedmodels.SurvivalRequestDTO;
import com.finance.moneyowl.generatedmodels.SurvivalResponseDTO;

public interface SimulatorService {
    SurvivalResponseDTO calculateSurvival(SurvivalRequestDTO request);
}

