package com.finance.moneyowl.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "USER")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    public User(String firstName, String lastName, String email, String address, String mobNo) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.mobNo = mobNo;
    }

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String mobNo;
    private String password;
    private Boolean isActive;
    private String otp;
    private String isVerified;

}
