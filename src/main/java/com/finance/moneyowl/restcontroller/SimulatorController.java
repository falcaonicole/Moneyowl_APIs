package com.finance.moneyowl.restcontroller;

import com.finance.moneyowl.generatedmodels.SurvivalRequestDTO;
import com.finance.moneyowl.generatedmodels.SurvivalResponseDTO;
import com.finance.moneyowl.service.Interface.SimulatorService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import src.finance.moneyowl.SimulatorApi;

@RestController
public class SimulatorController implements SimulatorApi {

    private final SimulatorService simulatorService;

    public SimulatorController(SimulatorService simulatorService) {
        this.simulatorService = simulatorService;
    }

    @Override
    public ResponseEntity<SurvivalResponseDTO> calculateSurvival(
            @Valid @RequestBody SurvivalRequestDTO request) {

        SurvivalResponseDTO response =
                simulatorService.calculateSurvival(request);

        return ResponseEntity.ok(response);
    }
}
