package com.finance.moneyowl.service.impl;

import com.finance.moneyowl.entity.Portfolio;
import com.finance.moneyowl.entity.User;
import com.finance.moneyowl.exceptions.MoneyowlApplicationException;
import com.finance.moneyowl.exceptions.ResourceNotFoundException;
import com.finance.moneyowl.generatedmodels.PortfolioDTO;
import com.finance.moneyowl.generatedmodels.PortfolioResponse;
import com.finance.moneyowl.repository.PortfolioRepository;
import com.finance.moneyowl.repository.UserRepository;
import com.finance.moneyowl.service.Interface.PortfolioService;
import com.finance.moneyowl.service.Interface.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PortfolioServiceImpl implements PortfolioService {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PortfolioRepository portfolioRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public PortfolioResponse savePortfolio(PortfolioDTO portfolioDTO, Long userId) {
        log.info("Start PortfolioServiceImpl :: savePortfolio - {}", portfolioDTO);
        User user = userService.getUserById(userId);
        Portfolio userPortfolio = user.getPortfolio();
        if(userPortfolio == null){
            Portfolio portfolio = new Portfolio(portfolioDTO.getEquity(), portfolioDTO.getDebt(),
                    portfolioDTO.getCashAndLiquid(),
                    portfolioDTO.getGold(),
                    portfolioDTO.getRealEstate(),
                    portfolioDTO.getFixedIncome(),
                    portfolioDTO.getOtherIncome()
            );
            portfolio.setUser(user);
            portfolioRepository.save(portfolio);
            log.info("End PortfolioServiceImpl :: savePortfolio - {}", portfolioDTO);
            return modelMapper.map(portfolio, PortfolioResponse.class);
        } else {
            log.error("Portfolio for requested userId already Exists");
            throw new MoneyowlApplicationException("Portfolio for requested userId already Exists");
        }

    }

    @Override
    public String updatePortfolio(PortfolioDTO portfolioDTO, Long portfolioId) {
        log.info("Start PortfolioServiceImpl :: updatePortfolio - {}", portfolioDTO);
        Portfolio portfolio = getPortfolio(portfolioId);
        BeanUtils.copyProperties(portfolioDTO, portfolio, "portfolioId");
        portfolioRepository.save(portfolio);
        log.info("End PortfolioServiceImpl :: updatePortfolio");
        return "User Portfolio Updated Successfully";
    }

    @Override
    public String deletePortfolio(Long portfolioId) {
        log.info("Start PortfolioServiceImpl :: deletePortfolio - {}", portfolioId);
        Portfolio portfolio = getPortfolio(portfolioId);
        portfolioRepository.delete(portfolio);
        log.info("End PortfolioServiceImpl :: deletePortfolio ");
        return "Portfolio Deleted Successfully";
    }

    // Helper method (you'll need to add this)
    public Portfolio getPortfolio(Long portfolioId) {
        log.info("Start PortfolioServiceImpl :: getPortfolio - {}", portfolioId);
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("PORTFOLIO.NOT.FOUND"));
    }

    @Override
    public PortfolioResponse getPortfolioById(Long portfolioId) {
        log.info("Start PortfolioServiceImpl :: getPortfolioById - {}", portfolioId);
        return modelMapper.map(getPortfolio(portfolioId), PortfolioResponse.class);
    }

}
