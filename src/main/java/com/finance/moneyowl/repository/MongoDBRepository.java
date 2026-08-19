package com.finance.moneyowl.repository;


import com.finance.moneyowl.model.UserPortfolioSetuResponseModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoDBRepository extends MongoRepository<UserPortfolioSetuResponseModel, Long> {
    UserPortfolioSetuResponseModel findByUserId(Long userId);
}
