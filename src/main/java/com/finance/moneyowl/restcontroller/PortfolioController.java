package com.finance.moneyowl.restcontroller;

import com.finance.moneyowl.generatedmodels.EquitiesDashBoardResponse;
import com.finance.moneyowl.generatedmodels.HomeDashBoardResponse;
import com.finance.moneyowl.service.interfaces.PortfolioService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Slf4j
@AllArgsConstructor
public class PortfolioController {

    private PortfolioService portfolioService;

    @GetMapping("/get-home-dashboard/{userId}")
    public ResponseEntity<HomeDashBoardResponse> getHomeDashBoard(
            @PathVariable Long userId) {
        HomeDashBoardResponse homeDashBoardResponse = portfolioService.getHomeDashBoardResponse(userId);
        return new ResponseEntity<>(homeDashBoardResponse, HttpStatus.OK);
    }

    @GetMapping("/get-equities-dashboard/{userId}")
    public ResponseEntity<EquitiesDashBoardResponse> getEquitiesDashBoard(
            @PathVariable Long userId) {
        EquitiesDashBoardResponse equitiesDashBoardResponse = portfolioService.getEquitiesDashBoardResponse(userId);
        return new ResponseEntity<>(equitiesDashBoardResponse, HttpStatus.OK);
    }
}
