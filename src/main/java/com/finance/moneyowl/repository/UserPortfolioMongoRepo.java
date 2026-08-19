package com.finance.moneyowl.repository;

import com.finance.moneyowl.model.UserPortfolio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPortfolioMongoRepo extends MongoRepository<UserPortfolio, Long> {
    List<UserPortfolio> findByUserId(Long userId);

    Optional<UserPortfolio> findByUserIdAndAssetType(Long userId, String assetType);
}
