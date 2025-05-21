package com.taxibooking.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String userId;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String fullName;
    private String address;
    private UserType userType;
    
    public enum UserType {
        PASSENGER,
        DRIVER,
        ADMIN
    }
    
    // Additional validation methods
    public boolean isValidEmail() {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    public boolean isValidPhoneNumber() {
        return phoneNumber != null && phoneNumber.matches("^\\d{10}$");
    }
} 