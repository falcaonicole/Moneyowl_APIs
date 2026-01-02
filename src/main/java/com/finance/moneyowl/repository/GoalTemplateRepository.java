package com.finance.moneyowl.repository;

import com.finance.moneyowl.entity.GoalTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalTemplateRepository extends JpaRepository<GoalTemplate, String> {
}

