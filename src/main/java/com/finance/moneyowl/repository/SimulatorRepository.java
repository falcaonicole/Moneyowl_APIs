package com.finance.moneyowl.repository;

import com.finance.moneyowl.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SimulatorRepository extends JpaRepository<Portfolio, Long> {

    Optional<Portfolio> findByUser_UserId(Long userId);
}