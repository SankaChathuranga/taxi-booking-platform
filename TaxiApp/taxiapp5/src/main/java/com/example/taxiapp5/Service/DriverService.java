package com.example.taxiapp5.Service;

import com.example.taxiapp5.model.Driver;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class DriverService {

    private static final String FILE_PATH = System.getProperty("user.dir") + "/driver.txt";
    private static long nextId = 1;

    private synchronized void initializeFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                file.createNewFile();
                writeDriversToFile(new ArrayList<>());
            } catch (IOException e) {
                throw new RuntimeException("Failed to create driver.txt", e);
            }
        }
        List<Driver> drivers = readDriversFromFile();
        if (!drivers.isEmpty()) {
            nextId = drivers.stream().mapToLong(Driver::getId).max().orElse(0) + 1;
        }
    }

    private List<Driver> readDriversFromFile() {
        List<Driver> drivers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    try {
                        Driver driver = new Driver(
                                Long.parseLong(parts[0]),
                                parts[1],
                                Double.parseDouble(parts[2])
                        );
                        drivers.add(driver);
                    } catch (NumberFormatException e) {
                        System.out.println("Warning: Invalid rating format in line: " + line + " - Skipping this driver");
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read driver.txt", e);
        }
        return drivers;
    }

    private void writeDriversToFile(List<Driver> drivers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Driver driver : drivers) {
                writer.write(String.format("%d:%s:%.1f%n",
                        driver.getId(), driver.getName(), driver.getRating()));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to driver.txt", e);
        }
    }

    public Driver createDriver(Driver driver) {
        initializeFile();
        driver.setId(nextId++);
        List<Driver> drivers = readDriversFromFile();
        drivers.add(driver);
        writeDriversToFile(drivers);
        return driver;
    }

    public List<Driver> getAllDrivers() {
        return readDriversFromFile();
    }

    public Driver getDriverById(Long id) {
        return readDriversFromFile().stream()
                .filter(driver -> driver.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
    }

    public Driver updateDriver(Long id, Driver driverDetails) {
        List<Driver> drivers = readDriversFromFile();
        boolean found = false;
        for (int i = 0; i < drivers.size(); i++) {
            if (drivers.get(i).getId().equals(id)) {
                Driver driver = drivers.get(i);
                driver.setName(driverDetails.getName());
                driver.setRating(driverDetails.getRating());
                found = true;
                break;
            }
        }
        if (!found) {
            throw new RuntimeException("Driver not found with id: " + id);
        }
        writeDriversToFile(drivers);
        return getDriverById(id);
    }

    public void deleteDriver(Long id) {
        List<Driver> drivers = readDriversFromFile();
        boolean removed = drivers.removeIf(driver -> driver.getId().equals(id));
        if (!removed) {
            throw new RuntimeException("Driver not found with id: " + id);
        }
        writeDriversToFile(drivers);
    }

    public Driver assignDriver() {
        List<Driver> drivers = readDriversFromFile();
        List<Driver> sortedDrivers = drivers.stream()
                .sorted(Comparator.comparingDouble(Driver::getRating).reversed()
                        .thenComparing(Driver::getName))
                .toList();

        if (sortedDrivers.isEmpty()) {
            System.out.println("Warning: No drivers found");
            return null;
        }

        System.out.println("Drivers sorted by rating (descending):");
        sortedDrivers.forEach(driver ->
                System.out.println(driver.getName() + " - Rating: " + driver.getRating())
        );

        return sortedDrivers.get(0);
    }
}