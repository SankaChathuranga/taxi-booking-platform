package com.taxibooking.controller;

import com.taxibooking.model.AbstractDriver;
import com.taxibooking.model.FullTimeDriver;
import com.taxibooking.model.PartTimeDriver;
import com.taxibooking.model.DriverType;
import com.taxibooking.service.DriverManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/drivers")
public class DriverManagementController {
    
    private final DriverManagementService driverService;
    
    @Autowired
    public DriverManagementController(DriverManagementService driverService) {
        this.driverService = driverService;
    }
    
    // Driver listing and search
    @GetMapping
    public String listDrivers(Model model) {
        try {
            List<AbstractDriver> drivers = driverService.getAllDrivers();
            model.addAttribute("drivers", drivers);
            return "driver/list";
        } catch (IOException e) {
            return "error";
        }
    }
    
    @GetMapping("/available")
    public String listAvailableDrivers(Model model) {
        try {
            List<AbstractDriver> drivers = driverService.getAvailableDrivers();
            model.addAttribute("drivers", drivers);
            model.addAttribute("title", "Available Drivers");
            return "driver/list";
        } catch (IOException e) {
            return "error";
        }
    }
    
    @GetMapping("/top-rated")
    public String listTopRatedDrivers(@RequestParam(defaultValue = "5") int limit, Model model) {
        try {
            List<AbstractDriver> drivers = driverService.getTopRatedDrivers(limit);
            model.addAttribute("drivers", drivers);
            model.addAttribute("title", "Top Rated Drivers");
            return "driver/list";
        } catch (IOException e) {
            return "error";
        }
    }
    
    @GetMapping("/type/{type}")
    public String listDriversByType(@PathVariable DriverType type, Model model) {
        try {
            List<AbstractDriver> drivers = driverService.getDriversByType(type);
            model.addAttribute("drivers", drivers);
            model.addAttribute("title", type.name() + " Drivers");
            return "driver/list";
        } catch (IOException e) {
            return "error";
        }
    }
    
    // Driver profile management
    @GetMapping("/{driverId}")
    public String viewDriverProfile(@PathVariable String driverId, Model model) {
        try {
            Optional<AbstractDriver> driverOpt = driverService.getDriverById(driverId);
            if (driverOpt.isPresent()) {
                model.addAttribute("driver", driverOpt.get());
                return "driver/profile";
            }
            return "error";
        } catch (IOException e) {
            return "error";
        }
    }
    
    @PostMapping("/{driverId}/availability")
    @ResponseBody
    public ResponseEntity<String> updateAvailability(
            @PathVariable String driverId,
            @RequestParam boolean isAvailable) {
        try {
            driverService.updateDriverAvailability(driverId, isAvailable);
            return ResponseEntity.ok("Availability updated successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to update availability");
        }
    }
    
    @PostMapping("/{driverId}/location")
    @ResponseBody
    public ResponseEntity<String> updateLocation(
            @PathVariable String driverId,
            @RequestParam String location) {
        try {
            driverService.updateDriverLocation(driverId, location);
            return ResponseEntity.ok("Location updated successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to update location");
        }
    }
    
    // Driver registration
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("driverTypes", DriverType.values());
        return "driver/register";
    }
    
    @PostMapping("/register/full-time")
    public String registerFullTimeDriver(@ModelAttribute FullTimeDriver driver) {
        try {
            driverService.saveDriver(driver);
            return "redirect:/drivers/" + driver.getUserId();
        } catch (IOException e) {
            return "error";
        }
    }
    
    @PostMapping("/register/part-time")
    public String registerPartTimeDriver(@ModelAttribute PartTimeDriver driver) {
        try {
            driverService.saveDriver(driver);
            return "redirect:/drivers/" + driver.getUserId();
        } catch (IOException e) {
            return "error";
        }
    }
    
    // Driver deletion
    @PostMapping("/{driverId}/delete")
    public String deleteDriver(@PathVariable String driverId) {
        try {
            driverService.deleteDriver(driverId);
            return "redirect:/drivers";
        } catch (IOException e) {
            return "error";
        }
    }
    
    // API endpoints for other components
    @GetMapping("/api/{driverId}")
    @ResponseBody
    public ResponseEntity<AbstractDriver> getDriverById(@PathVariable String driverId) {
        try {
            Optional<AbstractDriver> driverOpt = driverService.getDriverById(driverId);
            return driverOpt.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/api/available")
    @ResponseBody
    public ResponseEntity<List<AbstractDriver>> getAvailableDrivers() {
        try {
            return ResponseEntity.ok(driverService.getAvailableDrivers());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 