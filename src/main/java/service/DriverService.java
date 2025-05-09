package com.example.taxi.service;

import com.example.taxi.model.Driver;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class DriverService {
    private static final String FILE = "drivers.txt";

    public void save(Driver driver) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE, true))) {
            pw.println(driver.getId() + "," + driver.getName() + "," + driver.getVehicle() + "," + driver.getRating());
        }
    }

    public List<Driver> findAll() throws IOException {
        List<Driver> drivers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] f = line.split(",");
                drivers.add(new Driver(f[0], f[1], f[2], Double.parseDouble(f[3])));
            }
        }
        return drivers;
    }

    public void update(Driver updatedDriver) throws IOException {
        List<Driver> drivers = findAll();
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (Driver d : drivers) {
                if (d.getId().equals(updatedDriver.getId())) {
                    pw.println(updatedDriver.getId() + "," + updatedDriver.getName() + "," + updatedDriver.getVehicle() + "," + updatedDriver.getRating());
                } else {
                    pw.println(d.getId() + "," + d.getName() + "," + d.getVehicle() + "," + d.getRating());
                }
            }
        }
    }

    public void delete(String id) throws IOException {
        List<Driver> drivers = findAll();
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (Driver d : drivers) {
                if (!d.getId().equals(id)) {
                    pw.println(d.getId() + "," + d.getName() + "," + d.getVehicle() + "," + d.getRating());
                }
            }
        }
    }
}