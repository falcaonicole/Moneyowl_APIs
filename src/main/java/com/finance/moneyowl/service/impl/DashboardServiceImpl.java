package com.finance.moneyowl.service.impl;

import com.finance.moneyowl.generatedmodels.*;
import com.finance.moneyowl.model.UserHoldings;
import com.finance.moneyowl.model.UserPortfolio;
import com.finance.moneyowl.repository.MongoHoldingRepository;
import com.finance.moneyowl.repository.UserPortfolioMongoRepo;
import com.finance.moneyowl.service.interfaces.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.finance.moneyowl.utils.Constants.EQUITIES;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final UserPortfolioMongoRepo userPortfolioRepository;
    private final MongoHoldingRepository userHoldingsRepository;

    @Override
    public HomeDashBoardResponse getHomeDashboard(Long userId) {

        List<UserPortfolio> portfolios =
                userPortfolioRepository.findByUserId(userId);

        BigDecimal currentPortfolioValue = portfolios.stream()
                .map(UserPortfolio::getCurrentAssetValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalInvestedValue = portfolios.stream()
                .map(UserPortfolio::getTotalInvestedValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Asset> assets = portfolios.stream()
                .map(this::toAsset)
                .toList();

        return HomeDashBoardResponse.builder()
                .currentPortfolioValue(currentPortfolioValue)
                .totalInvestedValue(totalInvestedValue)
                .assets(assets)
                .build();
    }

    @Override
    public EquitiesDashBoardResponse getEquityDashboard(Long userId) {

        UserPortfolio portfolio = userPortfolioRepository
                .findByUserIdAndAssetType(userId, EQUITIES)
                .orElse(null);

        List<UserHoldings> holdings =
                userHoldingsRepository
                        .findByUserIdAndAssetTypeAndIsActiveTrue(
                                userId,
                                EQUITIES
                        );

        return EquitiesDashBoardResponse.builder()
                .portfolio(toUserPortfolioResponse(portfolio))
                .userHoldingList(
                        holdings.stream()
                                .map(this::toUserHoldingResponse)
                                .toList()
                )
                .build();
    }

    private Asset toAsset(UserPortfolio portfolio) {
        return Asset.builder()
                .fiType(portfolio.getAssetType())
                .currentValue(defaultValue(portfolio.getCurrentAssetValue()))
                .investedValue(defaultValue(portfolio.getTotalInvestedValue()))
                .lastUpdatedOn(LocalDate.from(portfolio.getLastUpdatedOn()))
                .performance(portfolio.getPerformance())
                .build();
    }

    private Portfolio toUserPortfolioResponse(
            UserPortfolio portfolio
    ) {

        if (portfolio == null) {
            return null;
        }

        return Portfolio.builder()
                .userId(Math.toIntExact(portfolio.getUserId()))
                .assetType(portfolio.getAssetType())
                .consentExpiry(portfolio.getConsentExpiry())
                .dataRange(portfolio.getDataRange())
                .performance(portfolio.getPerformance())
                .lastUpdatedOn(LocalDate.from(portfolio.getLastUpdatedOn()))
                .currentAssetValue(
                        defaultValue(portfolio.getCurrentAssetValue())
                )
                .totalInvestedValue(
                        defaultValue(portfolio.getTotalInvestedValue())
                )
                .build();
    }

    private UserHolding toUserHoldingResponse(
            UserHoldings holding
    ) {

        return UserHolding.builder()
                .id(holding.getId())
                .userId(Math.toIntExact(holding.getUserId()))
                .assetType(holding.getAssetType())
                .category(holding.getCategory())
                .isin(holding.getIsin())
                .externalHoldingId(holding.getExternalHoldingId())
                .averageBuyPrice(holding.getAverageBuyPrice())
                .units(holding.getUnits())
                .totalInvestedValue(holding.getTotalInvestedValue())
                .currentValue(holding.getCurrentValue())
                .performance(holding.getPerformance())
                .priceSource(holding.getPriceSource())
                .priceAsOfDate(holding.getPriceAsOfDate())
                .isStale(Boolean.TRUE.equals(holding.getIsStale()))
                .isActive(Boolean.TRUE.equals(holding.getIsActive()))
                .lastUpdatedOn(LocalDate.from(holding.getLastUpdatedOn()))
                .profile(holding.getProfile())
                .holding(holding.getHolding())
                .build();
    }

    private BigDecimal defaultValue(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
