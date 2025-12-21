package com.finance.moneyowl.configuration.AITools;

import com.finance.moneyowl.generatedmodels.PortfolioDTO;
import com.finance.moneyowl.service.Interface.PortfolioService;
import com.finance.moneyowl.service.Interface.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
@Slf4j
@AllArgsConstructor
public class PortfolioTools {

    private final PortfolioService portfolioService;
    private final UserService userService;

    // Input DTO for the AI to fill
    public record PortfolioRequest(Long userId) {
    }

    // Output DTO (or just return the Entity list)
    public record PortfolioResponse(PortfolioDTO userPortfolio) {
    }

    @Bean
    @Description("Fetch the Financial Portfolio of the User. Use this when the user asks any query related to their Portfolio.")
    public Function<PortfolioRequest, PortfolioResponse> getUserPortfolioData() {
        return portfolioRequest -> {
            log.info("Start PortfolioTools :: getUserPortfolioData - {}", portfolioRequest.userId);
            PortfolioDTO data = userService.getUserPortfolio(portfolioRequest.userId);
            log.info("End PortfolioTools :: getUserPortfolioData");
            return new PortfolioResponse(data);
        };
    }
}
