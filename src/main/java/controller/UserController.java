package com.example.taxi.controller;

import com.example.taxi.service.UserService;
import com.example.taxi.model.Customer; // Ensure this import exists
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Controller
public class UserController {

    private final UserService us;

    // Constructor-based dependency injection
    public UserController(UserService us) {
        this.us = us;
    }

    @GetMapping("/register")
    public String regPage() {
        return "static-html/register.html";
    }

    @PostMapping("/api/register")
    public RedirectView register(@RequestParam Map<String, String> p) {
        // Validate input parameters
        if (p.get("id") == null || p.get("name") == null || p.get("email") == null || p.get("password") == null) {
            return new RedirectView("/register?error=missing_fields");
        }
        us.register(new Customer(p.get("id"), p.get("name"), p.get("email"), p.get("password")));
        return new RedirectView("/login");
    }

    @GetMapping("/login")
    public String loginPage() {
        return "static-html/login.html";
    }

    @PostMapping("/api/login")
    public RedirectView login(@RequestParam String email, @RequestParam String password) {
        // Validate input parameters
        if (email == null || password == null) {
            return new RedirectView("/login?error=missing_credentials");
        }
        if (us.authenticate(email, password)) {
            return new RedirectView("/dashboard");
        } else {
            return new RedirectView("/login?error=invalid_credentials");
        }
    }
}