package com.taxiservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents a full-time driver in the taxi service system
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FullTimeDriver extends Driver {
    
    // Full-time specific attributes
    private double monthlySalary;
    private int workingHoursPerWeek;
    private boolean healthInsurance;
    
    // Constructor
    public FullTimeDriver() {
        super();
        setDriverType(DriverType.FULL_TIME);
    }
    
    /**
     * Calculates fare for full-time drivers
     * Full-time drivers have a fixed rate plus distance rate
     */
    @Override
    public double calculateFare(double distance, double time) {
        // Base fare + distance fare + minimal time factor
        double baseFare = 50.0;
        double distanceFare = distance * 12.0;
        double timeFare = time * 0.5;
        
        return baseFare + distanceFare + timeFare;
    }
}
