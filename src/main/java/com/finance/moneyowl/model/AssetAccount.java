package com.finance.moneyowl.model;

import com.finance.moneyowl.generatedmodels.Account;
import com.finance.moneyowl.generatedmodels.DataRange;
import lombok.Data;

import java.util.List;

@Data
public class AssetAccount {

    private String id;
    private String consentId;
    private String consentExpiry;
    private DataRange dataRange;
    private List<Account> asset;
}
