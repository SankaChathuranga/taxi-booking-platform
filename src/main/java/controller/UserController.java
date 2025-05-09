package com.example.taxi.controller;

import com.example.taxi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class UserController {
    @Autowired private UserService us;

    @GetMapping("/register") public String regPage() { return "static-html/register.html"; }

    @PostMapping("/api/register")
    public RedirectView register(@RequestParam Map<String,String> p) throws IOException {
        us.register(new Customer(p.get("id"),p.get("name"),p.get("email"),p.get("password")));
        return new RedirectView("/login");
    }

    @GetMapping("/login") public String loginPage() { return "static-html/login.html"; }

    @PostMapping("/api/login")
    public RedirectView login(@RequestParam String email,@RequestParam String password) throws IOException {
        if(us.authenticate(email,password)) return new RedirectView("/dashboard");
        else return new RedirectView("/login?error=1");
    }
}