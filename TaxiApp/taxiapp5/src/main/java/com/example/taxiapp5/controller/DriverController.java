package com.example.taxiapp5.controller;

import com.example.taxiapp5.model.Driver;
import com.example.taxiapp5.Service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @PostMapping
    public Driver createDriver(@RequestBody Driver driver) {
        return driverService.createDriver(driver);
    }

    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverService.getAllDrivers();
    }

    @GetMapping("/{id}")
    public Driver getDriverById(@PathVariable Long id) {
        return driverService.getDriverById(id);
    }

    @PutMapping("/{id}")
    public Driver updateDriver(@PathVariable Long id, @RequestBody Driver driver) {
        return driverService.updateDriver(id, driver);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/assign")
    public ResponseEntity<Driver> assignDriver() {
        Driver driver = driverService.assignDriver();
        return driver != null ? ResponseEntity.ok(driver) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}