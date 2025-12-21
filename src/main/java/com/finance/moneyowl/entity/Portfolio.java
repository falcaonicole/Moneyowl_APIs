package com.finance.moneyowl.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "portfolio", schema = "moneyowl")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Portfolio {
    public Portfolio(BigDecimal equity, BigDecimal debt, BigDecimal cashAndLiquid, BigDecimal gold, BigDecimal realEstate, BigDecimal fixedIncome, BigDecimal otherIncome) {
        this.equity = equity;
        this.debt = debt;
        this.cashAndLiquid = cashAndLiquid;
        this.gold = gold;
        this.realEstate = realEstate;
        this.fixedIncome = fixedIncome;
        this.otherIncome = otherIncome;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_id")
    private Long portfolioId;

    @Column(name = "equity")
    private BigDecimal equity;

    @Column(name = "debt")
    private BigDecimal debt;

    @Column(name = "cash_and_liquid")
    private BigDecimal cashAndLiquid;

    @Column(name = "gold")
    private BigDecimal gold;

    @Column(name = "real_estate")
    private BigDecimal realEstate;

    @Column(name = "fixed_income")
    private BigDecimal fixedIncome;

    @Column(name = "other_income")
    private BigDecimal otherIncome;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

}
