package com.finance.moneyowl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles", schema = "moneyowl")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Roles {

    @Id
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_name")
    private String roleName;

    private String description;

    private String type;

    @Column(name = "is_active")
    private Boolean isActive;
}
