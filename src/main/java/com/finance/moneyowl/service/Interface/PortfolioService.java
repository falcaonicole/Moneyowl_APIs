package com.finance.moneyowl.service.Interface;

import com.finance.moneyowl.generatedmodels.PortfolioDTO;
import com.finance.moneyowl.generatedmodels.PortfolioResponse;

public interface PortfolioService {

    PortfolioResponse savePortfolio(PortfolioDTO portfolioDTO, Long userId);

    PortfolioResponse getPortfolioById(Long portfolioId);

    String updatePortfolio(PortfolioDTO portfolioDTO, Long userId);

    String deletePortfolio(Long portfolioId);
}
