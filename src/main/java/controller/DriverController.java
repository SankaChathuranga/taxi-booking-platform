package com.example.taxi.controller;

import com.example.taxi.model.Driver;
import com.example.taxi.service.DriverService;
import com.example.taxi.service.SortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.List;

@Controller
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private SortService sortService;

    // Show list page
    @GetMapping("/drivers")
    public String showDriversPage(Model model) throws IOException {
        List<Driver> drivers = driverService.findAll();
        // sort by rating descending
        sortService.sortByRatingDesc(drivers);
        model.addAttribute("drivers", drivers);
        return "static-html/driver_list.html";
    }

    // API: get all drivers (JSON)
    @GetMapping("/api/drivers")
    @ResponseBody
    public List<Driver> getDrivers() throws IOException {
        List<Driver> drivers = driverService.findAll();
        sortService.sortByRatingDesc(drivers);
        return drivers;
    }

    // API: add new driver
    @PostMapping("/api/drivers")
    public String addDriver(@RequestParam String id,
                            @RequestParam String name,
                            @RequestParam String vehicle,
                            @RequestParam double rating) throws IOException {
        Driver d = new Driver(id, name, vehicle, rating);
        driverService.save(d);
        return "redirect:/drivers";
    }

    // API: update driver
    @PutMapping("/api/drivers/{id}")
    @ResponseBody
    public Driver updateDriver(@PathVariable String id,
                               @RequestParam String name,
                               @RequestParam String vehicle,
                               @RequestParam double rating) throws IOException {
        Driver d = new Driver(id, name, vehicle, rating);
        driverService.update(d);
        return d;
    }

    // API: delete driver
    @DeleteMapping("/api/drivers/{id}")
    @ResponseBody
    public String deleteDriver(@PathVariable String id) throws IOException {
        driverService.delete(id);
        return "Deleted";
    }