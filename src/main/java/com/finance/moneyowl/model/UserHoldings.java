package com.finance.moneyowl.model;

import com.finance.moneyowl.generatedmodels.Performance;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "user_holdings")
@CompoundIndex(name = "uq_user_holding_day", def = "{'userId':1,'assetType':1,'isin':1,'externalHoldingId':1,'lastUpdatedOn':1}", unique = true)
@Data
public class UserHoldings {
    @Id
    private String id;
    private Long userId;
    private String assetType;
    private String category;
    private String isin;
    private String externalHoldingId;
    private BigDecimal averageBuyPrice;
    private BigDecimal units;
    private BigDecimal totalInvestedValue;
    private BigDecimal currentValue;
    private Performance performance;
    private String priceSource;
    private LocalDate priceAsOfDate;
    private Boolean isStale = false;
    private Boolean isActive = true;
    private LocalDateTime lastUpdatedOn;
    private Object profile;
    private Object holding;
}
