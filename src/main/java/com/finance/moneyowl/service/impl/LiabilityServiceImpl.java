package com.finance.moneyowl.service.impl;

import com.finance.moneyowl.entity.Liability;
import com.finance.moneyowl.entity.User;
import com.finance.moneyowl.enums.CurrentLiabilitySubTypeEnum;
import com.finance.moneyowl.enums.LiabilityTypeEnum;
import com.finance.moneyowl.enums.NonCurrLiabilitySubTypeEnum;
import com.finance.moneyowl.exceptions.ResourceNotFoundException;
import com.finance.moneyowl.generatedmodels.LiabilityDTO;
import com.finance.moneyowl.repository.LiabilityRepository;
import com.finance.moneyowl.service.Interface.LiabilityService;
import com.finance.moneyowl.service.Interface.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class LiabilityServiceImpl implements LiabilityService {

    CurrentLiabilitySubTypeEnum currentLiabilitySubTypeEnum;

    @Autowired
    UserService userService;
    LiabilityTypeEnum liabilityTypeEnum;
    NonCurrLiabilitySubTypeEnum nonCurrLiabilitySubTypeEnum;

    @Autowired
    LiabilityRepository liabilityRepository;

    @Override
    public List<String> getCurrentLiabilityTypes() {
        return currentLiabilitySubTypeEnum.getCurrentLiabilityTypes();
    }

    @Override
    public List<String> getNonCurrentLiabilityTypes() {
        return nonCurrLiabilitySubTypeEnum.getNonCurrLiabilityTypes();
    }

    @Override
    public List<String> getLiabilityTypes() {
        return liabilityTypeEnum.getLiabilityTypes();
    }

    @Override
    public void saveLiability(LiabilityDTO liaDTO, Long userId) {
        log.info("Start LiabilityServiceImpl :: saveLiability - {}", liaDTO);
        User user = userService.getUserById(userId);
        if ("MONTHLY_FIXED_EXPENSES".equals(liaDTO.getLiabilitySubType())) {
            if (liaDTO.getAmount() != null) {
                liaDTO.setAmount(liaDTO.getAmount().multiply(new BigDecimal("12.00")));
            }
        }
        Liability liability = new Liability(liaDTO.getLiabilityType(), liaDTO.getLiabilitySubType(), liaDTO.getStartDate(), liaDTO.getEndDate(), liaDTO.getRoi(), liaDTO.getDuration(), liaDTO.getAmount(), user);
        liabilityRepository.save(liability);
        log.info("End LiabilityServiceImpl :: saveLiability");
    }

    @Override
    public Liability getLiabilityById(Long liabilityId) {
        log.info("Start LiabilityServiceImpl :: getLiabilityById - {}", liabilityId);
        return liabilityRepository.findById(liabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("LIABILITY.NOT.FOUND"));
    }

    @Override
    public void updateLiability(LiabilityDTO liabilityDTO, Long liabilityId) {
        log.info("Start LiabilityServiceImpl :: updateLiability - {}", liabilityId);
        Liability liability = getLiabilityById(liabilityId);
        BeanUtils.copyProperties(liabilityDTO, liability, "liabilityId");
        liabilityRepository.save(liability);
        log.info("End LiabilityServiceImpl :: updateLiability - {}", liabilityId);
    }

    @Override
    public void deleteLiability(Long liabilityId) {
        log.info("Start LiabilityServiceImpl :: deleteLiability - {}", liabilityId);
        Liability liability = getLiabilityById(liabilityId);
        liabilityRepository.delete(liability);
        log.info("End LiabilityServiceImpl :: deleteLiability - {}", liabilityId);
    }
}

