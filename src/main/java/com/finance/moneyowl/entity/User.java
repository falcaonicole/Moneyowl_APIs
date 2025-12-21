package com.finance.moneyowl.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", schema = "moneyowl")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    public User(String firstName, String lastName, String email, String address, String mobNo) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.mobNo = mobNo;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "mob_no")
    private String mobNo;

    @Column(name = "password")
    private String password;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "otp")
    private String otp;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "user_type")
    private String userType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles_mapping",
            schema = "moneyowl",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @ToString.Exclude
    private List<Roles> roles;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Portfolio portfolio;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Liability> liabilities = new ArrayList<>();

}
