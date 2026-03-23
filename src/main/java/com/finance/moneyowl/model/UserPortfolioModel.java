package com.finance.moneyowl.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_portfolio")
@Data
public class UserPortfolioModel {
    private Long userId;
    private AssetAccount equities;

    /** For Dynamic Fi Type
     * private Map<String, AssetAccount> assets;
     * */
}
