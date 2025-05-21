package com.taxibooking.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EconomyVehicle extends Vehicle {
    private static final double ECONOMY_RATE_MULTIPLIER = 1.0;
    private static final double MINIMUM_FARE = 5.0;

    public EconomyVehicle() {
        super();
        this.vehicleType = "ECONOMY";
    }

    public EconomyVehicle(String licensePlate, String make, String model, int year,
                         int capacity, String color, double baseRate, String features) {
        super(licensePlate, make, model, year, capacity, color, baseRate, features);
        this.vehicleType = "ECONOMY";
    }

    @Override
    public double calculateFare(double distance) {
        double fare = getBaseRate() * distance * ECONOMY_RATE_MULTIPLIER;
        return Math.max(fare, MINIMUM_FARE);
    }
} 