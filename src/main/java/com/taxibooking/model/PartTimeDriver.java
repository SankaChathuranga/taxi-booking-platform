package com.taxibooking.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PartTimeDriver extends AbstractDriver {
    private static final double PART_TIME_RATE = 2.0; // Rate per kilometer
    private static final double MINIMUM_FARE = 12.0;
    private static final double PEAK_HOUR_RATE_MULTIPLIER = 1.2;
    
    public PartTimeDriver(String userId, String username, String password, String email,
                         String phoneNumber, String fullName, String address,
                         String licenseNumber, String vehicleId) {
        super(userId, username, password, email, phoneNumber, fullName, address,
              licenseNumber, vehicleId, DriverType.PART_TIME);
    }
    
    @Override
    public double calculateFare(double distance) {
        double fare = baseFare + (distance * PART_TIME_RATE);
        
        // Apply peak hour multiplier if applicable
        int currentHour = java.time.LocalTime.now().getHour();
        if (isPeakHour(currentHour)) {
            fare *= PEAK_HOUR_RATE_MULTIPLIER;
        }
        
        return Math.max(fare, MINIMUM_FARE);
    }
    
    private boolean isPeakHour(int hour) {
        // Peak hours: 7-9 AM and 5-7 PM
        return (hour >= 7 && hour < 9) || (hour >= 17 && hour < 19);
    }
} 