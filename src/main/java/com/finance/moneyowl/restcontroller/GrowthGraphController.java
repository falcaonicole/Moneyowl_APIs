package com.finance.moneyowl.restcontroller;

import com.finance.moneyowl.generatedmodels.PortfolioResponse;
import com.finance.moneyowl.service.impl.GrowthGraphCalculatorServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import src.finance.moneyowl.AssetGrowthRateApi;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
public class GrowthGraphController implements AssetGrowthRateApi {

    private final GrowthGraphCalculatorServiceImpl growthRateCalculatorService;

    @Override
    public ResponseEntity<List<PortfolioResponse>> getIndividualAssetGrowthRate(@PathVariable("userId") Long userId, @PathVariable("duration") Integer duration) {
        log.info("Start GrowthGraphController :: getIndividualAssetGrowthRate - {},{}", userId, duration);
        List<PortfolioResponse> portfolioResponses = growthRateCalculatorService.getIndividualAssetGrowthRate(userId, duration);
        log.info("End GrowthGraphController :: getIndividualAssetGrowthRate");
        return new ResponseEntity<>(portfolioResponses, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, BigDecimal>> getNetWorthGrowthRate(@PathVariable("userId") Long userId, @PathVariable("duration") Integer duration) {
        log.info("Start GrowthGraphController :: getNetWorthGrowthRate - {},{}", userId, duration);
        Map<String, BigDecimal> responseMap = growthRateCalculatorService.getNetWorthGrowthRate(userId, duration);
        log.info("End GrowthGraphController :: getNetWorthGrowthRate - {},{}", userId, duration);
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, BigDecimal>> getNetWorthGrowthRateWithInflation(@PathVariable("userId") Long userId, @PathVariable("duration") Integer duration) {
        log.info("Start GrowthGraphController :: getNetWorthGrowthRateWithInflation - {},{}", userId, duration);
        Map<String, BigDecimal> result = growthRateCalculatorService.getNetWorthGrowthRateWithInflation(userId, duration);
        log.info("End GrowthGraphController :: getNetWorthGrowthRateWithInflation");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, BigDecimal>> getNetWorthGrowthRateWithAdjustedInflation(@PathVariable("userId") Long userId, @PathVariable("duration") Integer duration) {
        log.info("Start GrowthGraphController :: getNetWorthGrowthRateWithAdjustedInflation - {},{}", userId, duration);
        Map<String, BigDecimal> result = growthRateCalculatorService.getNetWorthGrowthRateWithAdjustedInflation(userId, duration);
        log.info("End GrowthGraphController :: getNetWorthGrowthRateWithAdjustedInflation");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //AI Based APIs
    @GetMapping("/ai/get/netWorthGrowth/{userId}/{duration}")
    public ResponseEntity<String> getAIIndividualAssetGrowthRate(@PathVariable("userId") Long userId, @PathVariable("duration") Integer duration) {
        log.info("Start GrowthGraphController :: getAIIndividualAssetGrowthRate - {},{}", userId, duration);
        String portfolioResponses = growthRateCalculatorService.getAINetGrowthAmount(userId, duration);
        log.info("End GrowthGraphController :: getAIIndividualAssetGrowthRate");
        return new ResponseEntity<>(portfolioResponses, HttpStatus.OK);
    }

    @GetMapping("/ai/get/netWorthGrowthWithInflation/{userId}/{duration}")
    public ResponseEntity<String> getAIIndividualAssetGrowthWithInflation(@PathVariable("userId") Long userId, @PathVariable("duration") Integer duration) {
        log.info("Start GrowthGraphController :: getAIIndividualAssetGrowthWithInflation - {},{}", userId, duration);
        String portfolioResponses = growthRateCalculatorService.getAINetGrowthAmountWithInflation(userId, duration);
        log.info("End GrowthGraphController :: getAIIndividualAssetGrowthWithInflation");
        return new ResponseEntity<>(portfolioResponses, HttpStatus.OK);
    }

    @GetMapping("/ai/get/netWorthGrowthWithAdjustedInflation/{userId}/{duration}")
    public ResponseEntity<String> getAIIndividualAssetGrowthWithAdjustedInflation(@PathVariable("userId") Long userId, @PathVariable("duration") Integer duration) {
        log.info("Start GrowthGraphController :: getAIIndividualAssetGrowthWithAdjustedInflation - {},{}", userId, duration);
        String portfolioResponses = growthRateCalculatorService.getAINetGrowthAmountWithAdjustedInflation(userId, duration);
        log.info("End GrowthGraphController :: getAIIndividualAssetGrowthWithAdjustedInflation");
        return new ResponseEntity<>(portfolioResponses, HttpStatus.OK);
    }

}
