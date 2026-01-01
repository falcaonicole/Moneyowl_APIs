package com.finance.moneyowl.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "life_goal",
        schema = "moneyowl"
)
public class LifeGoal {

    @Id
    @GeneratedValue
    @Column(name = "life_goal_id")
    private UUID lifeGoalId;

    @Column(name = "template_id", nullable = false, length = 50)
    private String templateId;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "icon", length = 10)
    private String icon;

    @Column(name = "cost", nullable = false, precision = 18, scale = 2)
    private BigDecimal cost;

    @Column(name = "target_year", nullable = false)
    private Integer targetYear;

    @Column(name = "is_loan")
    private Boolean isLoan;

    @Column(name = "roi")
    private Double roi;

    @Column(name = "loan_duration")
    private Integer loanDuration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /* ---------------- getters & setters ---------------- */

    public UUID getLifeGoalId() {
        return lifeGoalId;
    }

    public void setLifeGoalId(UUID lifeGoalId) {
        this.lifeGoalId = lifeGoalId;
    }


    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Integer getTargetYear() {
        return targetYear;
    }

    public void setTargetYear(Integer targetYear) {
        this.targetYear = targetYear;
    }

    public Boolean getIsLoan() {
        return isLoan;
    }

    public void setIsLoan(Boolean isLoan) {
        this.isLoan = isLoan;
    }

    public Double getRoi() {
        return roi;
    }

    public void setRoi(Double roi) {
        this.roi = roi;
    }

    public Integer getLoanDuration() {
        return loanDuration;
    }

    public void setLoanDuration(Integer loanDuration) {
        this.loanDuration = loanDuration;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
