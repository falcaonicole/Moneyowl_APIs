package com.finance.moneyowl.model;

import com.finance.moneyowl.generatedmodels.DataRange;
import com.finance.moneyowl.generatedmodels.Performance;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "user_portfolio")
@Data
public class UserPortfolio {
    //Over All Asset's Analytics
    @Id
    private String Id;
    private Long userId;
    private String assetType;
    private String consentExpiry;
    private DataRange dataRange;
    private Performance performance;
    private LocalDateTime lastUpdatedOn;
    private BigDecimal currentAssetValue;
    private BigDecimal totalInvestedValue;
}
