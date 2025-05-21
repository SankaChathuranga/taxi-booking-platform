package com.taxibooking.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VanVehicle extends Vehicle {
    private static final double VAN_RATE_MULTIPLIER = 1.8;
    private static final double MINIMUM_FARE = 15.0;
    private boolean hasWheelchairAccess;
    private boolean hasLuggageRack;
    private int cargoCapacity;

    public VanVehicle() {
        super();
        this.vehicleType = "VAN";
    }

    public VanVehicle(String licensePlate, String make, String model, int year,
                     int capacity, String color, double baseRate, String features,
                     boolean hasWheelchairAccess, boolean hasLuggageRack, int cargoCapacity) {
        super(licensePlate, make, model, year, capacity, color, baseRate, features);
        this.vehicleType = "VAN";
        this.hasWheelchairAccess = hasWheelchairAccess;
        this.hasLuggageRack = hasLuggageRack;
        this.cargoCapacity = cargoCapacity;
    }

    @Override
    public double calculateFare(double distance) {
        double fare = getBaseRate() * distance * VAN_RATE_MULTIPLIER;
        // Add van features surcharge
        if (hasWheelchairAccess) fare += 3.0;
        if (hasLuggageRack) fare += 2.0;
        // Add cargo capacity surcharge (per cubic meter)
        fare += (cargoCapacity * 0.5);
        return Math.max(fare, MINIMUM_FARE);
    }

    @Override
    public String getVehicleDetails() {
        return super.getVehicleDetails() + " (Van - " + cargoCapacity + "m³ cargo)";
    }
} 