package com.finance.moneyowl.service.interfaces;

import com.finance.moneyowl.generatedmodels.EquitiesDashBoardResponse;
import com.finance.moneyowl.generatedmodels.HomeDashBoardResponse;

public interface DashboardService {
    HomeDashBoardResponse getHomeDashboard(Long userId);

    EquitiesDashBoardResponse getEquityDashboard(Long userId);
}
