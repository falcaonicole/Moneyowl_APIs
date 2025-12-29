package com.finance.moneyowl.repository;

import com.finance.moneyowl.entity.Liability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiabilityRepository extends JpaRepository<Liability, Long> {
    List<Liability> findByUser_UserIdAndStatusAndLiabilitySubType(
            Long userId,
            String status,
            String liabilitySubType
    );
}
