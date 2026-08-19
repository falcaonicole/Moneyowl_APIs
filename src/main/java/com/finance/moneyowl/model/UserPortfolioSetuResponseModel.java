package com.finance.moneyowl.model;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "user_portfolio_setu_response")
@Data
public class UserPortfolioSetuResponseModel {
    @Id
    private String id;
    private Long userId;
    private String consentId;
    private String sessionId;
    private LocalDate fetchedAt;
    private LocalDate fiDataDate;
    private Boolean firstFetch = true;
    private String status;
    private List<String> failedFiTypes;
    private Integer schemaVersion;
    private Map<String, AssetAccount> assets = new HashMap<>();
    /** Hardoded Fi Types
     * private AssetAccount equities;
     *     private AssetAccount mutualFunds;
     *     private AssetAccount deposits;
     *     private AssetAccount nps;
     * */
    //Index: compound on (userId, fetchedAt)
}
