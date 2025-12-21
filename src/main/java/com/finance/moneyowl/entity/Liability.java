package com.finance.moneyowl.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "liability", schema = "moneyowl")
@Data
@NoArgsConstructor
public class Liability {

    public Liability(String liabilityType, String liabilitySubType, LocalDate startDate, LocalDate endDate, BigDecimal roi, BigDecimal duration, BigDecimal amount, User user) {
        this.liabilityType = liabilityType;
        this.liabilitySubType = liabilitySubType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.roi = roi;
        this.duration = duration;
        this.amount = amount;
        this.status = "ACTIVE";
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long liabilityId;

    @Column(name = "liability_type")
    private String liabilityType;

    @Column(name = "liability_sub_type")
    private String liabilitySubType;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "roi")
    private BigDecimal roi;

    @Column(name = "duration")
    private BigDecimal duration;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
