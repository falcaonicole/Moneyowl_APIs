package com.finance.moneyowl.repository;

import com.finance.moneyowl.model.UserPortfolio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPortfolioMongoRepository extends MongoRepository<UserPortfolio, Long> {
    UserPortfolio findByUserId(Long userId);
}
