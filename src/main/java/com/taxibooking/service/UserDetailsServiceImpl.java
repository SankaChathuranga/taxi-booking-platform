package com.taxibooking.service;

import com.taxibooking.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserFileService userFileService;

    @Autowired
    public UserDetailsServiceImpl(UserFileService userFileService) {
        this.userFileService = userFileService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userFileService.getUserByEmail(username);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        User user = userOptional.get();
        List<GrantedAuthority> authorities = new ArrayList<>();

        switch (user.getUserType()) {
            case PASSENGER:
                authorities.add(new SimpleGrantedAuthority("ROLE_PASSENGER"));
                break;
            case DRIVER:
                authorities.add(new SimpleGrantedAuthority("ROLE_DRIVER"));
                break;
            case ADMIN:
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                break;
            default:
                // Handle unknown user type or throw an exception
                throw new IllegalStateException("Unknown user type: " + user.getUserType());
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
