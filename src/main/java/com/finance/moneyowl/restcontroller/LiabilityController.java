package com.finance.moneyowl.restcontroller;

import com.finance.moneyowl.generatedmodels.LiabilityDTO;
import com.finance.moneyowl.service.Interface.LiabilityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import src.finance.moneyowl.LiabilityApi;

@RestController
@AllArgsConstructor
@Slf4j
public class LiabilityController implements LiabilityApi {
    private LiabilityService liabilityService;

    public ResponseEntity<String> deleteLiability(@PathVariable("liabilityId") Long liabilityId) {
        log.info("Start LiabilityController :: deleteLiability - {}", liabilityId);
        liabilityService.deleteLiability(liabilityId);
        log.info("End LiabilityController :: deleteLiability");
        return new ResponseEntity<>("Liability deleted successfully.", HttpStatus.OK);
    }

    public ResponseEntity<String> saveLiability(
            @PathVariable("userId") Long userId,
            @RequestBody LiabilityDTO liabilityDTO) {
        log.info("Start LiabilityController :: saveLiability - {}", liabilityDTO);
        liabilityService.saveLiability(liabilityDTO, userId);
        log.info("End LiabilityController :: saveLiability");
        return new ResponseEntity<>("Liability created successfully.", HttpStatus.CREATED);
    }

    public ResponseEntity<String> updateLiability(
            @PathVariable("liabilityId") Long liabilityId,
            @RequestBody LiabilityDTO liabilityDTO) {
        log.info("Start LiabilityController :: updateLiability - {}", liabilityId);
        liabilityService.updateLiability(liabilityDTO, liabilityId);
        log.info("End LiabilityController :: updateLiability");
        return new ResponseEntity<>("Liability updated successfully.", HttpStatus.OK);
    }
}
