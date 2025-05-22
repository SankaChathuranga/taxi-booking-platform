package com.taxiservice.dto;

import com.taxiservice.model.User;
import lombok.Data;

@Data
public class UserDTO {
    private String id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private User.UserRole role;

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setRole(user.getRole());
        return dto;
    }

    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setPhone(this.phone);
        user.setAddress(this.address);
        user.setRole(this.role);
        return user;
    }
} 