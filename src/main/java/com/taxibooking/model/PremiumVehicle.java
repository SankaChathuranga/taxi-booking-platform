package com.taxibooking.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PremiumVehicle extends Vehicle {
    private static final double PREMIUM_RATE_MULTIPLIER = 1.5;
    private static final double MINIMUM_FARE = 10.0;
    private boolean hasLeatherSeats;
    private boolean hasSunroof;
    private boolean hasPremiumAudio;

    public PremiumVehicle() {
        super();
        this.vehicleType = "PREMIUM";
    }

    public PremiumVehicle(String licensePlate, String make, String model, int year,
                         int capacity, String color, double baseRate, String features,
                         boolean hasLeatherSeats, boolean hasSunroof, boolean hasPremiumAudio) {
        super(licensePlate, make, model, year, capacity, color, baseRate, features);
        this.vehicleType = "PREMIUM";
        this.hasLeatherSeats = hasLeatherSeats;
        this.hasSunroof = hasSunroof;
        this.hasPremiumAudio = hasPremiumAudio;
    }

    @Override
    public double calculateFare(double distance) {
        double fare = getBaseRate() * distance * PREMIUM_RATE_MULTIPLIER;
        // Add premium features surcharge
        if (hasLeatherSeats) fare += 2.0;
        if (hasSunroof) fare += 1.5;
        if (hasPremiumAudio) fare += 1.0;
        return Math.max(fare, MINIMUM_FARE);
    }

    @Override
    public String getVehicleDetails() {
        return super.getVehicleDetails() + " (Premium)";
    }
} 