package com.finance.moneyowl.service.interfaces;

import com.finance.moneyowl.generatedmodels.DepositsDashBoardResponse;
import com.finance.moneyowl.generatedmodels.EquitiesDashBoardResponse;
import com.finance.moneyowl.generatedmodels.HomeDashBoardResponse;
import com.finance.moneyowl.generatedmodels.MutualFundsDashBoardResponse;

public interface DashboardService {
    HomeDashBoardResponse getHomeDashboard(Long userId);

    EquitiesDashBoardResponse getEquityDashboard(Long userId);

    MutualFundsDashBoardResponse getMutualFundsDashboard(Long userId);

    DepositsDashBoardResponse getDepositsDashboard(Long userId);
}
