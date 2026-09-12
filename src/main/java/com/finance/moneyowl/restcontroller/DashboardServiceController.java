package com.finance.moneyowl.restcontroller;

import com.finance.moneyowl.generatedmodels.EquitiesDashBoardResponse;
import com.finance.moneyowl.generatedmodels.HomeDashBoardResponse;
import com.finance.moneyowl.service.interfaces.DashboardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import src.finance.moneyowl.DashboardApi;

@RestController
@AllArgsConstructor
@Slf4j
public class DashboardServiceController implements DashboardApi {

    private DashboardService dashboardService;

    @Override
    public ResponseEntity<HomeDashBoardResponse> getHomeDashboard(@PathVariable("userId") Long userId) {
        log.info("Start DashboardServiceController :: getHomeDashboard - {}", userId);
        HomeDashBoardResponse response = dashboardService.getHomeDashboard(userId);
        log.info("End DashboardServiceController :: getHomeDashboard Response - {}", response);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<EquitiesDashBoardResponse> getEquityDashboard(@PathVariable("userId") Long userId) {
        log.info("Start DashboardServiceController :: getEquityDashboard - {}", userId);
        EquitiesDashBoardResponse response = dashboardService.getEquityDashboard(userId);
        log.info("End DashboardServiceController :: getEquityDashboard Response - {}", response);
        return ResponseEntity.ok(response);
    }
}
