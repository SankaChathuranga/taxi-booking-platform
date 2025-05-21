package com.taxibooking.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractDriver extends User {
    protected String licenseNumber;
    protected String vehicleId;
    protected double rating;
    protected int totalRides;
    protected boolean isAvailable;
    protected DriverType driverType;
    protected String currentLocation;
    protected double baseFare;
    
    public AbstractDriver(String userId, String username, String password, String email,
                         String phoneNumber, String fullName, String address,
                         String licenseNumber, String vehicleId, DriverType driverType) {
        super(userId, username, password, email, phoneNumber, fullName, address, UserType.DRIVER);
        this.licenseNumber = licenseNumber;
        this.vehicleId = vehicleId;
        this.rating = 0.0;
        this.totalRides = 0;
        this.isAvailable = true;
        this.driverType = driverType;
        this.currentLocation = "";
        this.baseFare = 10.0; // Default base fare
    }
    
    // Abstract method for fare calculation
    public abstract double calculateFare(double distance);
    
    // Common driver methods
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
    
    public void updateLocation(String location) {
        this.currentLocation = location;
    }
    
    public void setBaseFare(double baseFare) {
        if (baseFare > 0) {
            this.baseFare = baseFare;
        }
    }
} 