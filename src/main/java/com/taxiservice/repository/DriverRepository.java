package com.taxiservice.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.taxiservice.model.Driver;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class DriverRepository extends JsonFileRepository<Driver> {
    
    public DriverRepository() {
        super("drivers.json", Driver.class, new TypeReference<List<Driver>>() {});
    }

    @Override
    protected String getEntityId(Driver entity) {
        return entity.getId();
    }

    @Override
    protected void setEntityId(Driver entity, String id) {
        entity.setId(id);
    }

    @Override
    protected int findEntityIndex(List<Driver> entities, String id) {
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find all drivers that are currently available for trips
     * @return List of available drivers
     */
    public List<Driver> findAvailableDrivers() {
        return readAll().stream()
                .filter(Driver::isAvailability)
                .collect(Collectors.toList());
    }
    
    /**
     * Find a driver by their user ID
     * @param userId User ID to search for
     * @return Optional containing the driver if found
     */
    public Optional<Driver> findByUserId(String userId) {
        return readAll().stream()
                .filter(driver -> driver.getUserId().equals(userId))
                .findFirst();
    }
    
    /**
     * Find drivers with experience greater than or equal to the specified years
     * @param years Minimum years of experience
     * @return List of drivers with the specified experience
     */
    public List<Driver> findByExperienceGreaterThanEqual(int years) {
        return readAll().stream()
                .filter(driver -> driver.getYearsOfExperience() >= years)
                .collect(Collectors.toList());
    }
    
    /**
     * Find drivers with rating greater than or equal to the specified value
     * @param rating Minimum rating
     * @return List of drivers with the specified rating
     */
    public List<Driver> findByRatingGreaterThanEqual(double rating) {
        return readAll().stream()
                .filter(driver -> driver.getRating() >= rating)
                .collect(Collectors.toList());
    }
    
    /**
     * Find top performers based on rating and number of trips
     * @param limit Maximum number of drivers to return
     * @return List of top performing drivers
     */
    public List<Driver> findTopPerformers(int limit) {
        return readAll().stream()
                .filter(driver -> driver.getTotalTrips() > 0)
                .sorted((d1, d2) -> {
                    // Sort by rating first, then by total trips
                    int ratingCompare = Double.compare(d2.getRating(), d1.getRating());
                    if (ratingCompare != 0) {
                        return ratingCompare;
                    }
                    return Integer.compare(d2.getTotalTrips(), d1.getTotalTrips());
                })
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Delete a driver by ID
     * @param id ID of the driver to delete
     */
    public void deleteById(String id) {
        List<Driver> drivers = readAll();
        drivers.removeIf(driver -> driver.getId().equals(id));
        writeAll(drivers);
    }
}