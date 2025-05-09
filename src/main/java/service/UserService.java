package com.example.taxi.service;

import com.example.taxi.model.Customer; // and Admin
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.*;

@Service
public class UserService {
    private static final String FILE = "users.txt";

    public void register(User user) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE, true))) {
            pw.println(format(user));
        }
    }

    public boolean authenticate(String email, String pwd) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] f = line.split(",");
                if (f[2].equals(email) && f[3].equals(pwd)) return true;
            }
        }
        return false;
    }

    // Additional CRUD methods (readAll, update, delete)
}