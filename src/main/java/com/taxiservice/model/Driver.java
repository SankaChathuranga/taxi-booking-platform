package com.taxiservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "driverType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = FullTimeDriver.class, name = "FULL_TIME"),
    @JsonSubTypes.Type(value = PartTimeDriver.class, name = "PART_TIME")
})
public class Driver {
    private String id;
    
    private String userId;
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @NotBlank(message = "Contact number is required")
    private String contactNumber;
    
    @NotBlank(message = "License plate is required")
    private String licensePlate;
    
    @NotBlank(message = "Vehicle model is required")
    private String vehicleModel;
    
    private String currentLocation;
    
    @PositiveOrZero(message = "Rating must be a positive number")
    @Min(value = 0, message = "Rating must be at least 0")
    @Max(value = 5, message = "Rating must be at most 5")
    private double rating;
    
    private boolean availability;
    
    private LocalDate registrationDate = LocalDate.now();
    
    @PositiveOrZero(message = "Years of experience must be a positive number")
    private int yearsOfExperience;
    
    @PositiveOrZero(message = "Total trips must be a positive number")
    private int totalTrips;
    
    @NotNull(message = "Driver type is required")
    private DriverType driverType;
    
    /**
     * Calculates fare based on driver type.
     * This method will be overridden by subclasses.
     * 
     * @param distance Distance in kilometers
     * @param time Time in minutes
     * @return Calculated fare
     */
    public double calculateFare(double distance, double time) {
        // Base implementation
        return distance * 10.0;
    }
    
    /**
     * Enum to represent driver types
     */
    public enum DriverType {
        FULL_TIME,
        PART_TIME
    }
}