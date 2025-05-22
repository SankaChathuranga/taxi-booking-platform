package com.taxiservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private UserRole role;

    public enum UserRole {
        CUSTOMER,
        DRIVER,
        ADMIN
    }
} 