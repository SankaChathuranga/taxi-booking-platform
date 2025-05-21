package com.taxibooking.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Driver extends User {
    private String licenseNumber;
    private String vehicleId;
    private double rating;
    private int totalRides;
    private boolean isAvailable;
    
    public Driver(String userId, String username, String password, String email,
                 String phoneNumber, String fullName, String address,
                 String licenseNumber, String vehicleId) {
        super(userId, username, password, email, phoneNumber, fullName, address, UserType.DRIVER);
        this.licenseNumber = licenseNumber;
        this.vehicleId = vehicleId;
        this.rating = 0.0;
        this.totalRides = 0;
        this.isAvailable = true;
    }
    
    // Driver-specific methods
    public void updateRating(double newRating) {
        if (newRating >= 0 && newRating <= 5) {
            this.rating = ((this.rating * this.totalRides) + newRating) / (this.totalRides + 1);
            this.totalRides++;
        }
    }
    
    public void toggleAvailability() {
        this.isAvailable = !this.isAvailable;
    }
    
    public boolean isValidLicenseNumber() {
        return licenseNumber != null && licenseNumber.matches("^[A-Z0-9]{10}$");
    }
} 