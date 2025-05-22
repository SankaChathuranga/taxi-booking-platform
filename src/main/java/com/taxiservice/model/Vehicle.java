package com.taxiservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    private String id;
    private String model;
    private VehicleType type;
    private String licensePlate;
    private int capacity;
    private String driverId;
    private boolean available;
    private String description;
    private int yearOfManufacture;
    private double baseFarePerKm;

    public enum VehicleType {
        SEDAN,
        SUV,
        HATCHBACK
    }
} 