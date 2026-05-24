package com.finance.moneyowl.model;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_portfolio")
@Data
public class UserPortfolioModel {
    @Id
    private String id;
    private Long userId;
    private AssetAccount equities;

    /** For Dynamic Fi Type
     * private Map<String, AssetAccount> assets;
     * */
}
