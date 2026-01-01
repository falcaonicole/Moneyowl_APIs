package com.finance.moneyowl.repository;

import com.finance.moneyowl.entity.LifeGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LifeGoalRepository extends JpaRepository<LifeGoal, UUID> {
    List<LifeGoal> findByUser_UserId(Long userId);
}
