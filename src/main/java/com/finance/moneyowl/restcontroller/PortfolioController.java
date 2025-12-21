package com.finance.moneyowl.restcontroller;

import com.finance.moneyowl.generatedmodels.PortfolioDTO;
import com.finance.moneyowl.generatedmodels.PortfolioResponse;
import com.finance.moneyowl.service.Interface.PortfolioService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import src.finance.moneyowl.PortfolioApi;

@RestController
@AllArgsConstructor
@Slf4j
public class PortfolioController implements PortfolioApi {

    private PortfolioService portfolioService;

    //    @Override
    public ResponseEntity<String> deletePortfolio(@PathVariable("portfolioId") Long portfolioId) {
        log.info("Start PortfolioController :: deletePortfolio - {}", portfolioId);
        return new ResponseEntity<>(portfolioService.deletePortfolio(portfolioId), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<PortfolioResponse> getPortfolioById(@PathVariable("portfolioId") Long portfolioId) {
        log.info("Start PortfolioController :: getPortfolioById - {}", portfolioId);
        return new ResponseEntity<>(portfolioService.getPortfolioById(portfolioId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PortfolioResponse> savePortfolio(@PathVariable("userId") Long userId, @RequestBody PortfolioDTO portfolioDTO) {
        log.info("Start PortfolioController :: savePortfolio - {}", portfolioDTO);
        return new ResponseEntity<>(portfolioService.savePortfolio(portfolioDTO, userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> updatePortfolio(@PathVariable("userId") Long userId, @RequestBody PortfolioDTO portfolioDTO) {
        log.info("Start PortfolioController :: updatePortfolio - {}", portfolioDTO);
        return new ResponseEntity<>(portfolioService.updatePortfolio(portfolioDTO, userId), HttpStatus.OK);
    }

}
