package com.taxibooking.controller;

import com.taxibooking.model.User;
import com.taxibooking.model.Passenger;
import com.taxibooking.model.Driver;
import com.taxibooking.service.UserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {
    
    private final UserFileService userFileService;
    
    @Autowired
    public UserController(UserFileService userFileService) {
        this.userFileService = userFileService;
    }
    
    // Registration endpoints
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "user/register";
    }
    
    @PostMapping("/register/passenger")
    public String registerPassenger(@ModelAttribute Passenger passenger) {
        try {
            passenger.setUserId(userFileService.generateUserId());
            userFileService.saveUser(passenger);
            return "redirect:/users/login";
        } catch (IOException e) {
            return "error";
        }
    }
    
    @PostMapping("/register/driver")
    public String registerDriver(@ModelAttribute Driver driver) {
        try {
            driver.setUserId(userFileService.generateUserId());
            userFileService.saveUser(driver);
            return "redirect:/users/login";
        } catch (IOException e) {
            return "error";
        }
    }
    
    @PostMapping("/register")
    public String registerUser(@RequestParam String fullName,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String confirmPassword,
                             @RequestParam User.UserType userType,
                             Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            model.addAttribute("fullName", fullName);
            model.addAttribute("email", email);
            model.addAttribute("userType", userType);
            return "user/register";
        }

        try {
            // Check if user with the same email already exists
            if (userFileService.getUserByEmail(email).isPresent()) {
                 model.addAttribute("error", "User with this email already exists");
                 model.addAttribute("fullName", fullName);
                 model.addAttribute("email", email);
                 model.addAttribute("userType", userType);
                 return "user/register";
            }

            User newUser;
            String userId = userFileService.generateUserId();
            
            // Create appropriate user type object
            switch (userType) {
                case PASSENGER:
                    newUser = new Passenger(userId, email, password, email, null, fullName, null);
                    break;
                case DRIVER:
                    newUser = new Driver(userId, email, password, email, null, fullName, null, null, null);
                    break;
                case ADMIN:
                    newUser = new User(userId, email, password, email, null, fullName, null, userType);
                    break;
                default:
                    model.addAttribute("error", "Invalid user type");
                    model.addAttribute("fullName", fullName);
                    model.addAttribute("email", email);
                    model.addAttribute("userType", userType);
                    return "user/register";
            }

            userFileService.saveUser(newUser);
            return "redirect:/users/login";
        } catch (IOException e) {
            model.addAttribute("error", "Error during registration: " + e.getMessage());
            model.addAttribute("fullName", fullName);
            model.addAttribute("email", email);
            model.addAttribute("userType", userType);
            return "user/register";
        }
    }
    
    // Login endpoints
    @GetMapping("/login")
    public String showLoginForm() {
        return "user/login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String email, 
                       @RequestParam String password,
                       HttpSession session,
                       Model model) {
        try {
            Optional<User> userOpt = userFileService.getUserByEmail(email);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (user.getPassword().equals(password)) {
                    // Store user in session
                    session.setAttribute("userId", user.getUserId());
                    session.setAttribute("userType", user.getUserType());
                    
                    // Redirect based on user type
                    switch (user.getUserType()) {
                        case PASSENGER:
                            return "redirect:/passenger/dashboard";
                        case DRIVER:
                            return "redirect:/driver/dashboard";
                        case ADMIN:
                            return "redirect:/admin/dashboard";
                        default:
                            return "redirect:/";
                    }
                }
            }

            model.addAttribute("error", "Invalid email or password");
            model.addAttribute("email", email);
            return "user/login";
        } catch (IOException e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error during login: " + e.getMessage());
            model.addAttribute("email", email);
            return "user/login";
        }
    }
    
    // Profile management endpoints
    @GetMapping("/profile")
    public String showProfile(@RequestParam String userId, Model model) {
        try {
            Optional<User> userOpt = userFileService.getUserById(userId);
            if (userOpt.isPresent()) {
                model.addAttribute("user", userOpt.get());
                return "user/profile";
            }
            return "error";
        } catch (IOException e) {
            return "error";
        }
    }
    
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User user) {
        try {
            userFileService.saveUser(user);
            return "redirect:/users/profile?userId=" + user.getUserId();
        } catch (IOException e) {
            return "error";
        }
    }
    
    // Admin endpoints
    @GetMapping("/admin/list")
    public String listUsers(Model model) {
        try {
            List<User> users = userFileService.getAllUsers();
            model.addAttribute("users", users);
            return "user/list";
        } catch (IOException e) {
            return "error";
        }
    }
    
    @PostMapping("/admin/delete/{userId}")
    public String deleteUser(@PathVariable String userId) {
        try {
            userFileService.deleteUser(userId);
            return "redirect:/users/admin/list";
        } catch (IOException e) {
            return "error";
        }
    }
    
    // API endpoints for other components
    @GetMapping("/api/{userId}")
    @ResponseBody
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        try {
            Optional<User> userOpt = userFileService.getUserById(userId);
            return userOpt.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 