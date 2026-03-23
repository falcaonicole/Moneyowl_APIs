package com.finance.moneyowl.repository;


import com.finance.moneyowl.model.UserPortfolioModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoDBRepository extends MongoRepository<UserPortfolioModel, Long> {
}
