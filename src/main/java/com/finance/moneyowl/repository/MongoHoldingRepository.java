package com.finance.moneyowl.repository;

import com.finance.moneyowl.model.UserHoldings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoHoldingRepository extends MongoRepository<UserHoldings, Long> {
    List<UserHoldings> findByUserIdAndAssetTypeAndIsActiveTrue(
            Long userId,
            String assetType
    );
}
