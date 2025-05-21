package com.taxibooking.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FullTimeDriver extends AbstractDriver {
    private static final double FULL_TIME_RATE = 2.5; // Rate per kilometer
    private static final double MINIMUM_FARE = 15.0;
    
    public FullTimeDriver(String userId, String username, String password, String email,
                         String phoneNumber, String fullName, String address,
                         String licenseNumber, String vehicleId) {
        super(userId, username, password, email, phoneNumber, fullName, address,
              licenseNumber, vehicleId, DriverType.FULL_TIME);
    }
    
    @Override
    public double calculateFare(double distance) {
        double fare = baseFare + (distance * FULL_TIME_RATE);
        return Math.max(fare, MINIMUM_FARE);
    }
} 