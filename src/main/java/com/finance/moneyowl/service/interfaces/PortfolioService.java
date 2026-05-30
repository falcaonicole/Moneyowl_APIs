package com.finance.moneyowl.service.interfaces;

import com.finance.moneyowl.generatedmodels.EquitiesDashBoardResponse;
import com.finance.moneyowl.generatedmodels.HomeDashBoardResponse;

public interface PortfolioService {
    HomeDashBoardResponse getHomeDashBoardResponse(Long userId);

    EquitiesDashBoardResponse getEquitiesDashBoardResponse(Long userId);
}
