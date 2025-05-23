package com.taxiservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents a part-time driver in the taxi service system
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PartTimeDriver extends Driver {
    
    // Part-time specific attributes
    private double hourlyRate;
    private int maxHoursPerWeek;
    private boolean flexibleHours;
    
    public PartTimeDriver() {
        super();
        setDriverType(DriverType.PART_TIME);
    }
    
    /**
     * Calculates fare for part-time drivers
     * Part-time drivers have a lower base rate but higher per-minute charge
     */
    @Override
    public double calculateFare(double distance, double time) {
        // Lower base fare + distance fare + higher time factor
        double baseFare = 30.0;
        double distanceFare = distance * 10.0;
        double timeFare = time * 1.0; // Part-time drivers charge more per minute
        
        return baseFare + distanceFare + timeFare;
    }
}
