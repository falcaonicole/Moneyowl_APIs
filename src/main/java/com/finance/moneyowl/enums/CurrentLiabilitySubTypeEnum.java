package com.finance.moneyowl.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum CurrentLiabilitySubTypeEnum {

    /*Loans with Duration = 1 year/12 months*/

    SHORT_TERM_LOANS("Short Term Loans"),
    CREDIT_CARD_DEBT("Credit Card Debt"),
    YEARLY_RENT_PAYABLE("Yearly Rent Payable"),
    VEHICLE_LOAN("Vehicle Loan"),
    HOUSEHOLD_EXPENSE("Household Expense"),
    MEDICAL_INSURANCE("Medical / Insurance"),
    INCOME_TAX("Income Tax"),
    BNPL("BNPL"),
    NET_LONG_TERM_LOANS_EMI("Net Long Term Loans EMI"), // For EMIs to be paid annually for Long Term Loans
    VACATION_EXPENSE("Vacation Expense"),
    CHILDREN_MARRIAGE("Children's Marriage Expense"),
    CHILDCARE_AND_EDUCATION("Childcare and Education");
    private final String displayName;

    public List<String> getCurrentLiabilityTypes() {
        return Arrays.stream(CurrentLiabilitySubTypeEnum.values())
                .map(CurrentLiabilitySubTypeEnum::getDisplayName)
                .collect(Collectors.toList());
    }

}

