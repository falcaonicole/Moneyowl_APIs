package com.finance.moneyowl.model;

import com.finance.moneyowl.generatedmodels.AccountData;
import com.finance.moneyowl.generatedmodels.DataRange;
import lombok.Data;

import java.util.List;

@Data
/**
 * "errorMsg": "1 validation error(s): [1] fiTypes -> 0 -> Must be one of: DEPOSIT, TERM_DEPOSIT, RECURRING_DEPOSIT,
 * SIP, CP, GOVT_SECURITIES, EQUITIES, BONDS, DEBENTURES, MUTUAL_FUNDS, ETF, IDR, CIS, AIF, INSURANCE_POLICIES,
 * NPS, INVIT, REIT, OTHER, GSTR1_3B, LIFE_INSURANCE, GENERAL_INSURANCE."
 * **/
public class AssetAccount {

    private String id;
    private String consentId;
    private String consentExpiry;
    private DataRange dataRange;
    private List<AccountData> asset;
}
