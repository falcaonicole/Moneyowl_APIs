package com.finance.moneyowl.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum LiabilityTypeEnum {
    CURRENT_LIABILITIES("Current Liabilities"),
    NON_CURRENT_LIABILITIES("Non-current Liabilities"),
    CONTINGENT_LIABILITIES("Contingent Liabilities");
    private final String displayName;

    public List<String> getLiabilityTypes() {
        return Arrays.stream(LiabilityTypeEnum.values())
                .map(LiabilityTypeEnum::getDisplayName)
                .collect(Collectors.toList());
    }
}
