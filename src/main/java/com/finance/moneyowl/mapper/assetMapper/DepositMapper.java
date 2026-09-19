package com.finance.moneyowl.mapper.assetMapper;

import com.finance.moneyowl.generatedmodels.DepositAccount;
import com.finance.moneyowl.generatedmodels.DepositProfile;
import com.finance.moneyowl.generatedmodels.DepositSummary;
import com.finance.moneyowl.model.AssetAccount;
import com.finance.moneyowl.model.UserHoldings;
import com.finance.moneyowl.model.UserPortfolio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DepositMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "assetType", source = "fiType")
    @Mapping(target = "category", source = "summary.type")
    @Mapping(target = "externalHoldingId", source = "account.linkedAccRef")
    @Mapping(target = "isin", ignore = true)
    @Mapping(target = "units", expression = "java(java.math.BigDecimal.ONE)")
    @Mapping(target = "averageBuyPrice", ignore = true)
    @Mapping(target = "currentValue", source = "summary.currentBalance", qualifiedByName = "parseSafe")
    @Mapping(target = "totalInvestedValue", ignore = true)
    @Mapping(target = "profile", source = "profile")
    @Mapping(target = "holding", source = "summary")
    @Mapping(target = "performance", ignore = true)
    @Mapping(target = "priceSource", constant = "FIU")
    @Mapping(target = "priceAsOfDate", source = "summary.balanceDateTime", qualifiedByName = "toLocalDate")
    @Mapping(target = "isStale", constant = "false")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "lastUpdatedOn", expression = "java(java.time.LocalDateTime.now())")
    UserHoldings toDepositHolding(DepositAccount account, DepositSummary summary, DepositProfile profile, Long userId, String fiType);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "assetType", constant = "deposit")
    @Mapping(target = "currentAssetValue", ignore = true)
    @Mapping(target = "totalInvestedValue", ignore = true)
    @Mapping(target = "performance", ignore = true)
    @Mapping(target = "consentExpiry", source = "account.consentExpiry")
    @Mapping(target = "dataRange", source = "account.dataRange")
    @Mapping(target = "lastUpdatedOn", expression = "java(java.time.LocalDateTime.now())")
    UserPortfolio toPortfolio(AssetAccount account);


    @Named("parseSafe")
    default BigDecimal parseSafe(String value) {
        if (value == null || value.trim().isEmpty() || "NA".equalsIgnoreCase(value.trim())) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    @Named("toLocalDate")
    default LocalDate toLocalDate(OffsetDateTime value) {
        if (value == null) {
            return null;
        }
        return value.toLocalDate();
    }
}