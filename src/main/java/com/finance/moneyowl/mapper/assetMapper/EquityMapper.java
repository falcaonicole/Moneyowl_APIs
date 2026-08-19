package com.finance.moneyowl.mapper.assetMapper;

import com.finance.moneyowl.generatedmodels.EquityHoldings;
import com.finance.moneyowl.generatedmodels.EquityProfile;
import com.finance.moneyowl.generatedmodels.EquitySummary;
import com.finance.moneyowl.model.AssetAccount;
import com.finance.moneyowl.model.UserHoldings;
import com.finance.moneyowl.model.UserPortfolio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = LocalDateTime.class)
public interface EquityMapper {

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "assetType", source = "fiType")
    @Mapping(target = "externalHoldingId", source = "profile", qualifiedByName = "extractDematId")
    @Mapping(target = "profile", source = "profile")
    @Mapping(target = "averageBuyPrice", source = "equityHoldings.rate")
    @Mapping(target = "units", source = "equityHoldings.units")
    @Mapping(target = "isin", source = "equityHoldings.isin")
    @Mapping(target = "holding", source = "equityHoldings")
    @Mapping(target = "currentValue", source = "equityHoldings", qualifiedByName = "calculateCurrentValue")
    @Mapping(target = "totalInvestedValue", source = "equityHoldings", qualifiedByName = "calculateTotalInvestment")
    @Mapping(target = "lastUpdatedOn", expression = "java(LocalDateTime.now())")
    @Mapping(target = "isActive", constant = "true")
    UserHoldings toEquityHoldings(EquityHoldings equityHoldings, Long userId, String fiType, EquityProfile profile);


    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "assetType", source = "fiType")
    @Mapping(target = "currentAssetValue", source = "summary.currentValue")
    @Mapping(target = "totalInvestedValue", source = "summary.investmentValue")
    @Mapping(target = "consentExpiry", source = "assetAccount.consentExpiry")
    @Mapping(target = "dataRange", source = "assetAccount.dataRange")
    @Mapping(target = "lastUpdatedOn", expression = "java(LocalDateTime.now())")
    UserPortfolio toEquityPortfolio(EquitySummary summary, Long userId, String fiType, AssetAccount assetAccount);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "assetType", constant = "equities")
    @Mapping(target = "consentExpiry", source = "account.consentExpiry")
    @Mapping(target = "dataRange", source = "account.dataRange")
    @Mapping(target = "performance", ignore = true)
    @Mapping(target = "lastUpdatedOn", expression = "java(java.time.LocalDateTime.now())")
    UserPortfolio toPortfolio(AssetAccount account);

    @Named("calculateCurrentValue")
    default BigDecimal calculateCurrentValue(EquityHoldings raw) {
        if (raw == null) return BigDecimal.ZERO;
        BigDecimal units = parseSafe(raw.getUnits());
        BigDecimal ltp = parseSafe(raw.getLastTradedPrice());
        return units.multiply(ltp);
    }

    @Named("calculateTotalInvestment")
    default BigDecimal calculateTotalInvestment(EquityHoldings raw) {
        if (raw == null) return BigDecimal.ZERO;
        BigDecimal units = parseSafe(raw.getUnits());
        BigDecimal rate = parseSafe(raw.getRate());
        return units.multiply(rate);
    }

    @Named("parseSafe")
    default BigDecimal parseSafe(String value) {
        if (value == null || value.trim().isEmpty() || "NA".equalsIgnoreCase(value.trim())) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    @Named("extractDematId")
    default String extractDematId(EquityProfile profile) {
        if (profile == null
                || profile.getHolders() == null
                || profile.getHolders().getHolder() == null
                || profile.getHolders().getHolder().isEmpty()) {
            return null;
        }

        return profile.getHolders()
                .getHolder()
                .getFirst()
                .getDematId();
    }
}
