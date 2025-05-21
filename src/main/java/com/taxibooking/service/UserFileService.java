package com.taxibooking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxibooking.model.User;
import com.taxibooking.model.Passenger;
import com.taxibooking.model.Driver;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserFileService {
    private static final String USERS_FILE = "users.txt";
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public UserFileService() {
        createFileIfNotExists();
    }
    
    private void createFileIfNotExists() {
        try {
            if (!Files.exists(Paths.get(USERS_FILE))) {
                Files.createFile(Paths.get(USERS_FILE));
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create users file", e);
        }
    }
    
    public void saveUser(User user) throws IOException {
        List<User> users = getAllUsers();
        users.removeIf(u -> u.getUserId().equals(user.getUserId()));
        users.add(user);
        writeUsersToFile(users);
    }
    
    public List<User> getAllUsers() throws IOException {
        if (Files.size(Paths.get(USERS_FILE)) == 0) {
            return new ArrayList<>();
        }
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(USERS_FILE))) {
            return reader.lines()
                    .map(line -> {
                        try {
                            Map<String, Object> userMap = objectMapper.readValue(line, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                            String userType = (String) userMap.get("userType");
                            
                            if (User.UserType.DRIVER.name().equals(userType)) {
                                return objectMapper.convertValue(userMap, Driver.class);
                            } else if (User.UserType.PASSENGER.name().equals(userType)) {
                                return objectMapper.convertValue(userMap, Passenger.class);
                            } else {
                                return objectMapper.convertValue(userMap, User.class);
                            }
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }
    
    public Optional<User> getUserById(String userId) throws IOException {
        return getAllUsers().stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst();
    }
    
    public Optional<User> getUserByUsername(String username) throws IOException {
        return getAllUsers().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }
    
    public Optional<User> getUserByEmail(String email) throws IOException {
        return getAllUsers().stream()
                .filter(user -> user.getEmail() != null && user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
    
    public void deleteUser(String userId) throws IOException {
        List<User> users = getAllUsers();
        users.removeIf(user -> user.getUserId().equals(userId));
        writeUsersToFile(users);
    }
    
    private void writeUsersToFile(List<User> users) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(USERS_FILE))) {
            for (User user : users) {
                writer.write(objectMapper.writeValueAsString(user));
                writer.newLine();
            }
        }
    }
    
    public String generateUserId() throws IOException {
        List<User> users = getAllUsers();
        if (users.isEmpty()) {
            return "USER001";
        }
        
        return String.format("USER%03d", 
            users.stream()
                .map(user -> Integer.parseInt(user.getUserId().substring(4)))
                .max(Integer::compareTo)
                .orElse(0) + 1);
    }
} 