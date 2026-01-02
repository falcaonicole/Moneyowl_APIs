package com.finance.moneyowl.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "goal_template",
        schema = "moneyowl"
)
public class GoalTemplate {

    @Id
    @Column(name = "template_id")
    private String templateId;

    private String name;

    private String icon;

    @Column(name = "default_roi")
    private Double defaultRoi;

    @Column(name = "is_loan_allowed")
    private Boolean isLoanAllowed;

    // getters & setters

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

    public Double getDefaultRoi() {
        return defaultRoi;
    }

    public void setDefaultRoi(Double defaultRoi) {
        this.defaultRoi = defaultRoi;
    }

    public Boolean getIsLoanAllowed() {
        return isLoanAllowed;
    }

    public void setIsLoanAllowed(Boolean isLoanAllowed) {
        this.isLoanAllowed = isLoanAllowed;
    }
}


