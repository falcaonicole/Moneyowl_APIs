package com.finance.moneyowl.mapper.assetMapper;

import com.finance.moneyowl.generatedmodels.MutualFundsHoldings;
import com.finance.moneyowl.generatedmodels.MutualFundsProfile;
import com.finance.moneyowl.generatedmodels.MutualFundsSummary;
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
public interface MFMapper {

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "assetType", source = "fiType")
    @Mapping(target = "externalHoldingId", source = "holdings.folioNo")
    @Mapping(target = "profile", source = "profile")
    @Mapping(target = "category", source = "holdings.schemeCategory")
    @Mapping(target = "averageBuyPrice", ignore = true)
    @Mapping(target = "units", source = "holdings.closingUnits")
    @Mapping(target = "isin", source = "holdings.isin")
    @Mapping(target = "holding", source = "holdings")
    @Mapping(target = "currentValue", source = "holdings", qualifiedByName = "calculateCurrentValue")
    @Mapping(target = "totalInvestedValue", source = "holdings", qualifiedByName = "calculateTotalInvestment")
    @Mapping(target = "lastUpdatedOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "isActive", constant = "true")
    UserHoldings toMutualFundsHoldings(MutualFundsHoldings holdings, Long userId, String fiType, MutualFundsProfile profile);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "assetType", source = "fiType")
    @Mapping(target = "currentAssetValue", source = "summary.currentValue")
    @Mapping(target = "totalInvestedValue", source = "summary.costValue")
    @Mapping(target = "consentExpiry", source = "assetAccount.consentExpiry")
    @Mapping(target = "dataRange", source = "assetAccount.dataRange")
    @Mapping(target = "lastUpdatedOn", expression = "java(java.time.LocalDateTime.now())")
    UserPortfolio toMutualFundPortfolio(MutualFundsSummary summary, Long userId, String fiType, AssetAccount assetAccount);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "assetType", constant = "mutual_funds")
    @Mapping(target = "consentExpiry", source = "account.consentExpiry")
    @Mapping(target = "dataRange", source = "account.dataRange")
    @Mapping(target = "performance", ignore = true)
    @Mapping(target = "lastUpdatedOn", expression = "java(java.time.LocalDateTime.now())")
    UserPortfolio toPortfolio(AssetAccount account);

    @Named("calculateCurrentValue")
    default BigDecimal calculateCurrentValue(MutualFundsHoldings raw) {
        if (raw == null) return BigDecimal.ZERO;

        BigDecimal units = parseSafe(raw.getClosingUnits());
        BigDecimal nav = parseSafe(raw.getNav());

        return units.multiply(nav);
    }

    @Named("calculateTotalInvestment")
    default BigDecimal calculateTotalInvestment(MutualFundsHoldings raw) {
        return BigDecimal.ZERO;
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
}
