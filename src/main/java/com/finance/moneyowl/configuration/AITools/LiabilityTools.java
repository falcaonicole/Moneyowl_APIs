package com.finance.moneyowl.configuration.AITools;

import com.finance.moneyowl.generatedmodels.LiabilityDTO;
import com.finance.moneyowl.service.Interface.LiabilityService;
import com.finance.moneyowl.service.Interface.UserService;
import com.finance.moneyowl.service.impl.AuthenticationServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.List;
import java.util.function.Function;

@Configuration
@Slf4j
@AllArgsConstructor
public class LiabilityTools {

    private final LiabilityService liabilityService;
    private final UserService userService;
    private final AuthenticationServiceImpl authenticationService;

    // Input DTO for the AI to fill
    public record LiabilityRequest(Long userId) {
    }

    // Output DTO (or just return the Entity list)
    public record LiabilityResponse(List<LiabilityDTO> userLiabilities) {
    }

    @Bean
    @Description("Fetch the Liabilities of the User. Use this when the user asks any query related to their Portfolio and Liabilities of the User.")
    public Function<LiabilityRequest, LiabilityResponse> getUserLiabilities() {
        return liabilityRequest -> {
            log.info("Start LiabilityTools :: getUserLiabilities - {}", authenticationService.getLoggedInUserId());
            List<LiabilityDTO> data = userService.getAllUserLiabilities(authenticationService.getLoggedInUserId());
            log.info("End LiabilityTools:: getUserLiabilities");
            return new LiabilityResponse(data);
        };
    }
}
