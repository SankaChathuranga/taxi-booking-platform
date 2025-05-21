package com.taxibooking.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Passenger extends User {
    private String paymentMethod;
    private double walletBalance;
    private int totalRides;
    
    public Passenger(String userId, String username, String password, String email, 
                    String phoneNumber, String fullName, String address) {
        super(userId, username, password, email, phoneNumber, fullName, address, UserType.PASSENGER);
        this.walletBalance = 0.0;
        this.totalRides = 0;
    }
    
    // Passenger-specific methods
    public void addToWallet(double amount) {
        if (amount > 0) {
            this.walletBalance += amount;
        }
    }
    
    public boolean deductFromWallet(double amount) {
        if (amount > 0 && this.walletBalance >= amount) {
            this.walletBalance -= amount;
            return true;
        }
        return false;
    }
    
    public void incrementRides() {
        this.totalRides++;
    }
} 