package com.finance.moneyowl.restcontroller;

import com.finance.moneyowl.generatedmodels.*;
import com.finance.moneyowl.service.Interface.SimulatorService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import src.finance.moneyowl.SimulatorApi;

@RestController
public class SimulatorController implements SimulatorApi {

    private final SimulatorService simulatorService;

    public SimulatorController(SimulatorService simulatorService) {
        this.simulatorService = simulatorService;
    }

    @Override
    public ResponseEntity<SurvivalResponseDTO> calculateSurvival(@Valid @RequestBody SurvivalRequestDTO request) {
        SurvivalResponseDTO response =simulatorService.calculateSurvival(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<LifeGoalsListResponse> getUserGoals(Integer userId) {

        // convert Integer -> Long for service layer
        LifeGoalsListResponse response =
                simulatorService.getLifeGoals(userId.longValue());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<LifeGoalResponseDTO> createLifeGoal(
            LifeGoalRequestDTO lifeGoalRequestDTO) {

        LifeGoalResponseDTO response =
                simulatorService.createLifeGoal(lifeGoalRequestDTO);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> updateLifeGoal(
            String goalId,
            LifeGoalRequestDTO lifeGoalRequestDTO) {

        simulatorService.updateLifeGoal(goalId, lifeGoalRequestDTO);
        return ResponseEntity.ok().build();
    }




}
