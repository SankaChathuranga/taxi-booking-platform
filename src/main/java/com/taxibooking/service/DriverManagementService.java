package com.taxibooking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxibooking.model.AbstractDriver;
import com.taxibooking.model.FullTimeDriver;
import com.taxibooking.model.PartTimeDriver;
import com.taxibooking.model.DriverType;
import com.taxibooking.util.DriverSorter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DriverManagementService {
    
    @Value("${app.file.storage.path}/${app.file.drivers}")
    private String driversFile;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @PostConstruct
    public void init() {
        createFileIfNotExists();
    }
    
    private void createFileIfNotExists() {
        try {
            Path filePath = Paths.get(driversFile);
            if (!Files.exists(filePath)) {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create drivers file: " + e.getMessage(), e);
        }
    }
    
    public void saveDriver(AbstractDriver driver) throws IOException {
        List<AbstractDriver> drivers = getAllDrivers();
        drivers.removeIf(d -> d.getUserId().equals(driver.getUserId()));
        drivers.add(driver);
        writeDriversToFile(drivers);
    }
    
    public List<AbstractDriver> getAllDrivers() throws IOException {
        if (Files.size(Paths.get(driversFile)) == 0) {
            return new ArrayList<>();
        }
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(driversFile))) {
            return reader.lines()
                    .map(line -> {
                        try {
                            Map<String, Object> driverMap = objectMapper.readValue(line, Map.class);
                            String driverType = (String) driverMap.get("driverType");
                            
                            if (DriverType.FULL_TIME.name().equals(driverType)) {
                                return objectMapper.convertValue(driverMap, FullTimeDriver.class);
                            } else if (DriverType.PART_TIME.name().equals(driverType)) {
                                return objectMapper.convertValue(driverMap, PartTimeDriver.class);
                            }
                            return null;
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }
    
    public List<AbstractDriver> getAvailableDrivers() throws IOException {
        return getAllDrivers().stream()
                .filter(AbstractDriver::isAvailable)
                .collect(Collectors.toList());
    }
    
    public List<AbstractDriver> getDriversByType(DriverType type) throws IOException {
        return getAllDrivers().stream()
                .filter(driver -> driver.getDriverType() == type)
                .collect(Collectors.toList());
    }
    
    public List<AbstractDriver> getTopRatedDrivers(int limit) throws IOException {
        List<AbstractDriver> drivers = getAllDrivers();
        DriverSorter.sortByRating(drivers);
        return drivers.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    public Optional<AbstractDriver> getDriverById(String driverId) throws IOException {
        return getAllDrivers().stream()
                .filter(driver -> driver.getUserId().equals(driverId))
                .findFirst();
    }
    
    public void deleteDriver(String driverId) throws IOException {
        List<AbstractDriver> drivers = getAllDrivers();
        drivers.removeIf(driver -> driver.getUserId().equals(driverId));
        writeDriversToFile(drivers);
    }
    
    public void updateDriverAvailability(String driverId, boolean isAvailable) throws IOException {
        Optional<AbstractDriver> driverOpt = getDriverById(driverId);
        if (driverOpt.isPresent()) {
            AbstractDriver driver = driverOpt.get();
            driver.setAvailable(isAvailable);
            saveDriver(driver);
        }
    }
    
    public void updateDriverLocation(String driverId, String location) throws IOException {
        Optional<AbstractDriver> driverOpt = getDriverById(driverId);
        if (driverOpt.isPresent()) {
            AbstractDriver driver = driverOpt.get();
            driver.updateLocation(location);
            saveDriver(driver);
        }
    }
    
    private void writeDriversToFile(List<AbstractDriver> drivers) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(driversFile))) {
            for (AbstractDriver driver : drivers) {
                writer.write(objectMapper.writeValueAsString(driver));
                writer.newLine();
            }
        }
    }
} 