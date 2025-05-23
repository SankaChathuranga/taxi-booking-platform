package com.taxiservice.service;

import com.taxiservice.dto.UserDTO;
import com.taxiservice.exception.TaxiServiceException;
import com.taxiservice.model.User;
import com.taxiservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO registerUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new TaxiServiceException("Email already registered");
        }
        User user = userDTO.toEntity();
        user = userRepository.save(user);
        return UserDTO.fromEntity(user);
    }

    public UserDTO login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new TaxiServiceException("User not found"));
        if (!user.getPassword().equals(password)) {
            throw new TaxiServiceException("Invalid password");
        }
        return UserDTO.fromEntity(user);
    }

    public UserDTO getUserDetails(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new TaxiServiceException("User not found"));
        return UserDTO.fromEntity(user);
    }

    public UserDTO updateUser(String id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new TaxiServiceException("User not found"));
        
        User user = userDTO.toEntity();
        user.setId(id);
        user.setPassword(existingUser.getPassword()); // Preserve password
        
        user = userRepository.save(user);
        return UserDTO.fromEntity(user);
    }

    public void deleteUser(String id) {
        if (!userRepository.findById(id).isPresent()) {
            throw new TaxiServiceException("User not found");
        }
        userRepository.delete(id);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }
} 