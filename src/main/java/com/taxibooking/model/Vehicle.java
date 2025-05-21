package com.taxibooking.model;

import lombok.Data;
import java.util.UUID;

@Data
public abstract class Vehicle {
    private String vehicleId;
    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private int capacity;
    private String color;
    private VehicleStatus status;
    private String driverId;
    private double baseRate;
    private String features;
    protected String vehicleType;

    public enum VehicleStatus {
        AVAILABLE,
        IN_USE,
        MAINTENANCE,
        OUT_OF_SERVICE
    }

    public Vehicle() {
        this.vehicleId = UUID.randomUUID().toString();
        this.status = VehicleStatus.AVAILABLE;
    }

    public Vehicle(String licensePlate, String make, String model, int year, 
                  int capacity, String color, double baseRate, String features) {
        this();
        this.licensePlate = licensePlate;
        this.make = make;
        this.model = model;
        this.year = year;
        this.capacity = capacity;
        this.color = color;
        this.baseRate = baseRate;
        this.features = features;
    }

    // Abstract method to calculate fare based on distance
    public abstract double calculateFare(double distance);

    // Method to check if vehicle is available
    public boolean isAvailable() {
        return status == VehicleStatus.AVAILABLE;
    }

    // Method to update vehicle status
    public void updateStatus(VehicleStatus newStatus) {
        this.status = newStatus;
    }

    // Method to assign/unassign driver
    public void assignDriver(String driverId) {
        this.driverId = driverId;
    }

    public void unassignDriver() {
        this.driverId = null;
    }

    // Method to get vehicle details as a string
    public String getVehicleDetails() {
        return String.format("%s %s %s (%s)", year, make, model, licensePlate);
    }
} 