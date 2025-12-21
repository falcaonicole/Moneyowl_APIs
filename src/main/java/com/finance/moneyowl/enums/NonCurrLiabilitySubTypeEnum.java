package com.finance.moneyowl.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum NonCurrLiabilitySubTypeEnum {

    HOME_LOAN_MORTGAGE("Home Loan / Mortgage"),
    CAR_LOAN("Car/Vehicle Loan"),
    EDUCATION_LOAN("Education Loan"),
    PERSONAL_LOAN_LONG_TERM("Personal Loan (Long-term)"),
    BUSINESS_INVESTMENT_LOAN("Business / Investment Loan"),
    PENSION_RETIREMENT_OBLIGATION("Pension / Retirement Obligation"),
    LEASE_LIABILITIES("Lease Liabilities"),
    LEGAL_SETTLEMENT_OBLIGATIONS("Legal / Settlement Obligations"),
    MISCELLANEOUS_LONG_TERM_CONTRACTS("Miscellaneous Long-Term Contracts");
    private final String displayName;

    public List<String> getNonCurrLiabilityTypes() {
        return Arrays.stream(NonCurrLiabilitySubTypeEnum.values())
                .map(NonCurrLiabilitySubTypeEnum::getDisplayName)
                .collect(Collectors.toList());
    }
}
