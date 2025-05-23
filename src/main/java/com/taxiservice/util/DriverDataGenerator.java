package com.taxiservice.util;

import com.taxiservice.model.Driver;
import com.taxiservice.model.FullTimeDriver;
import com.taxiservice.model.PartTimeDriver;
import com.taxiservice.repository.DriverRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Utility class to generate sample driver data
 */
@Component
@Order(1) // Ensure this runs early in the startup process
public class DriverDataGenerator implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DriverDataGenerator.class);
    private final DriverRepository driverRepository;
    private final Random random = new Random();

    @Autowired
    public DriverDataGenerator(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public void run(String... args) {
        logger.info("Checking if sample drivers need to be generated...");
        List<Driver> existingDrivers = driverRepository.findAll();
        logger.info("Found {} existing drivers", existingDrivers.size());
        
        // Only generate sample data if we have less than 2 drivers
        if (existingDrivers.size() < 2) {
            logger.info("Generating sample drivers...");
            generateDrivers();
        } else {
            logger.info("Skipping driver generation as {} drivers already exist", existingDrivers.size());
        }
    }

    /**
     * Generate 10 random drivers with predefined details
     */
    private void generateDrivers() {
        logger.info("Starting to generate 10 sample drivers");
        
        List<Driver> drivers = new ArrayList<>();
        drivers.add(createDriver("John Smith", "555-1234", "Toyota Camry", "DRV-001", 4.8, true, Driver.DriverType.FULL_TIME));
        drivers.add(createDriver("David Lee", "555-9101", "Ford Focus", "DRV-003", 4.9, true, Driver.DriverType.FULL_TIME));
        drivers.add(createDriver("Sarah Miller", "555-1122", "Chevrolet Malibu", "DRV-004", 4.2, true, Driver.DriverType.PART_TIME));
        drivers.add(createDriver("Kevin Brown", "555-3344", "Tesla Model 3", "DRV-005", 5.0, false, Driver.DriverType.FULL_TIME));
        drivers.add(createDriver("Maria Garcia", "555-5678", "Honda Civic", "DRV-002", 4.5, false, Driver.DriverType.PART_TIME));
        drivers.add(createDriver("James Wilson", "555-7890", "Hyundai Sonata", "DRV-006", 4.3, true, Driver.DriverType.FULL_TIME));
        drivers.add(createDriver("Lisa Johnson", "555-2468", "Nissan Altima", "DRV-007", 4.7, true, Driver.DriverType.PART_TIME));
        drivers.add(createDriver("Robert Taylor", "555-1357", "Kia Optima", "DRV-008", 4.1, false, Driver.DriverType.FULL_TIME));
        drivers.add(createDriver("Jennifer Davis", "555-3690", "Mazda 6", "DRV-009", 4.6, true, Driver.DriverType.PART_TIME));
        drivers.add(createDriver("Michael Brown", "555-8024", "Volkswagen Passat", "DRV-010", 4.4, true, Driver.DriverType.FULL_TIME));

        logger.info("Created {} driver objects in memory", drivers.size());
        
        // Save each driver individually and log the result
        int savedCount = 0;
        for (Driver driver : drivers) {
            try {
                Driver saved = driverRepository.save(driver);
                logger.info("Saved driver: {} ({})", saved.getFullName(), saved.getId());
                savedCount++;
            } catch (Exception e) {
                logger.error("Failed to save driver: {}", driver.getFullName(), e);
            }
        }
        
        logger.info("Successfully saved {} out of {} drivers", savedCount, drivers.size());
    }

    /**
     * Create a driver with the specified details
     */
    private Driver createDriver(String fullName, String contactNumber, String vehicleModel,
                             String licensePlate, double rating, boolean available, Driver.DriverType type) {
        logger.info("Creating driver: {} ({})", fullName, type);
        
        Driver driver;
        if (type == Driver.DriverType.FULL_TIME) {
            driver = new FullTimeDriver();
            double salary = 4000 + random.nextDouble() * 2000; // Random salary between 4000-6000
            ((FullTimeDriver) driver).setMonthlySalary(salary);
            ((FullTimeDriver) driver).setHealthInsurance(true);
            ((FullTimeDriver) driver).setWorkingHoursPerWeek(40);
            logger.info("Set full-time driver salary: ${}", String.format("%.2f", salary));
        } else {
            driver = new PartTimeDriver();
            double hourlyRate = 15 + random.nextDouble() * 10; // Random hourly rate between 15-25
            int maxHours = 20 + random.nextInt(21); // Random max hours between 20-40
            ((PartTimeDriver) driver).setHourlyRate(hourlyRate);
            ((PartTimeDriver) driver).setMaxHoursPerWeek(maxHours);
            logger.info("Set part-time driver hourly rate: ${} and max hours: {}", 
                    String.format("%.2f", hourlyRate), maxHours);
        }

        String driverId = UUID.randomUUID().toString();
        driver.setId(driverId);
        driver.setUserId(UUID.randomUUID().toString());
        driver.setFullName(fullName);
        driver.setContactNumber(contactNumber);
        driver.setVehicleModel(vehicleModel);
        driver.setLicensePlate(licensePlate);
        driver.setRating(rating);
        driver.setAvailability(available);
        String location = getRandomLocation();
        driver.setCurrentLocation(location);
        driver.setRegistrationDate(LocalDate.now().minusDays(random.nextInt(30)));
        driver.setYearsOfExperience(random.nextInt(10) + 1);
        driver.setTotalTrips(random.nextInt(100));
        driver.setDriverType(type);
        
        logger.info("Created driver: {} (ID: {}, Rating: {}, Location: {})", 
                fullName, driverId, rating, location);

        return driver;
    }

    private String getRandomLocation() {
        String[] locations = {"Downtown", "Uptown", "Suburbs", "Airport"};
        return locations[random.nextInt(locations.length)];
    }
}
