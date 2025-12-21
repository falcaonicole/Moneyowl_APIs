package com.finance.moneyowl.service.Interface;

import com.finance.moneyowl.entity.Liability;
import com.finance.moneyowl.generatedmodels.LiabilityDTO;

import java.util.List;

public interface LiabilityService {

    /**
     * Add custom getter method in Liability Enums to get and call those here
     */
    List<String> getCurrentLiabilityTypes();

    List<String> getNonCurrentLiabilityTypes();

    List<String> getLiabilityTypes();

    void saveLiability(LiabilityDTO liabilityDTO, Long userId);

    Liability getLiabilityById(Long liabilityId);

    void updateLiability(LiabilityDTO liabilityDTO, Long liabilityId);

    void deleteLiability(Long liabilityId);

}
