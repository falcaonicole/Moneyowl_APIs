package com.finance.moneyowl.model;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Document(collection = "daily_price")
@CompoundIndex(name = "uq_isin_date", def = "{'isin':1,'date':1}", unique = true)
@Data
public class DailyPrice {
    @Id
    private String id;
    private String isin;
    private LocalDate date;
    private BigDecimal price;
    private String source;   // AMFI / NSE_BHAVCOPY / NPS_TRUST
    private LocalDate fetchedAt;
}
