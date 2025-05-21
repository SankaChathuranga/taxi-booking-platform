package com.taxibooking.service;

import com.taxibooking.model.Driver;
import com.taxibooking.model.Passenger;
import com.taxibooking.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Use this for JUnit 5 with Mockito
public class UserDetailsServiceImplTest {

    @Mock
    private UserFileService userFileService;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    // @BeforeEach
    // void setUp() {
    //     // MockitoAnnotations.openMocks(this) is called by MockitoExtension
    // }

    @Test
    void loadUserByUsername_UserFound_Passenger() {
        // Arrange
        String testEmail = "passenger@example.com";
        String encodedPassword = "encodedPassword123";
        Passenger passenger = new Passenger("p1", testEmail, encodedPassword, testEmail, null, "Test Passenger", null);
        // Passenger constructor might not set userType, let's assume it's set correctly or the getter handles it.
        // For explicit test, if User has a setUserType or constructor for it:
        // passenger.setUserType(User.UserType.PASSENGER); // Ensure UserType is set if not by constructor

        when(userFileService.getUserByEmail(testEmail)).thenReturn(Optional.of(passenger));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(testEmail);

        // Assert
        assertNotNull(userDetails);
        assertEquals(testEmail, userDetails.getUsername());
        assertEquals(encodedPassword, userDetails.getPassword());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_PASSENGER")));
    }

    @Test
    void loadUserByUsername_UserFound_Driver() {
        // Arrange
        String testEmail = "driver@example.com";
        String encodedPassword = "encodedPassword456";
        Driver driver = new Driver("d1", testEmail, encodedPassword, testEmail, null, "Test Driver", null, "D_LICENSE", "CAR_MODEL");
        // Ensure UserType is set if not by constructor
        // driver.setUserType(User.UserType.DRIVER);


        when(userFileService.getUserByEmail(testEmail)).thenReturn(Optional.of(driver));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(testEmail);

        // Assert
        assertNotNull(userDetails);
        assertEquals(testEmail, userDetails.getUsername());
        assertEquals(encodedPassword, userDetails.getPassword());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_DRIVER")));
    }

    @Test
    void loadUserByUsername_UserFound_Admin() {
        // Arrange
        String testEmail = "admin@example.com";
        String encodedPassword = "encodedPassword789";
        User admin = new User("a1", testEmail, encodedPassword, testEmail, null, "Test Admin", null, User.UserType.ADMIN);

        when(userFileService.getUserByEmail(testEmail)).thenReturn(Optional.of(admin));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(testEmail);

        // Assert
        assertNotNull(userDetails);
        assertEquals(testEmail, userDetails.getUsername());
        assertEquals(encodedPassword, userDetails.getPassword());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        // Arrange
        String testEmail = "notfound@example.com";
        when(userFileService.getUserByEmail(testEmail)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(testEmail);
        });

        assertEquals("User not found with email: " + testEmail, exception.getMessage());
    }

    @Test
    void loadUserByUsername_UserFound_UnknownRole() {
        // Arrange
        String testEmail = "unknown@example.com";
        String encodedPassword = "encodedPasswordUnk";
        // Create a base User object and manually set a UserType that's not Passenger, Driver, or Admin
        // This requires UserType to be flexible or User class to allow setting a type not handled by the switch.
        // For this test, let's assume UserType enum has a value like 'UNKNOWN' or we pass null
        // if the User class allows setting userType directly.
        // However, the enum User.UserType only has PASSENGER, DRIVER, ADMIN.
        // The default case in UserDetailsServiceImpl throws IllegalStateException.
        // So this test should check for IllegalStateException.

        User unknownUser = new User("u1", testEmail, encodedPassword, testEmail, null, "Unknown User", null, null);
        // Let's simulate a user type that is not handled by the switch case.
        // The User class might need adjustment for this, or we can test the default case
        // by ensuring user.getUserType() returns something unexpected.
        // Given current User.UserType enum, this scenario is hard to test without modifying User.UserType
        // or using a more advanced mock that can return a custom UserType.
        // Let's assume for now the User object is constructed with a null UserType,
        // and User.getUserType() would return null.

        // For the sake of this example, let's assume getUserType() could return a value not in the switch
        // This part is tricky because UserType is an enum.
        // A more realistic way to test the default case of the switch statement
        // would be if the enum had more values.
        // Given the current enum, the default case in UserDetailsServiceImpl is unreachable
        // unless user.getUserType() returns null AND the switch doesn't handle null explicitly first.

        // If user.getUserType() returns null, it will likely cause a NullPointerException in the switch
        // before hitting the default, unless there's a 'case null'.
        // The code is: switch (user.getUserType())
        // If user.getUserType() is null, this will throw a NullPointerException.

        // Let's test for IllegalStateException as per the default case in UserDetailsServiceImpl,
        // assuming somehow a User object could have a UserType that's not PASSENGER, DRIVER, or ADMIN.
        // This is a conceptual test for the default branch.
        // In practice, with the current enum, this state is impossible.
        // However, if the enum were to be expanded and the switch not updated, this would be relevant.

        // To make this testable, we'd need a way for user.getUserType() to return a new, unhandled UserType.
        // Since User.UserType is an enum, we cannot easily create a "rogue" value.
        // The default case "throw new IllegalStateException("Unknown user type: " + user.getUserType());"
        // is more of a safeguard for future enum extension.

        // Let's test the NullPointerException scenario if userType is null.
        User userWithNullType = new User("nullUser", testEmail, encodedPassword, "null@example.com", null, "Null Type", null, null);
        // We need to ensure user.getUserType() actually returns null.
        // The User constructor User(String userId, String username, String password, String email, String phoneNumber, String fullName, String address, UserType userType)
        // will set userType to null if null is passed.

        when(userFileService.getUserByEmail(testEmail)).thenReturn(Optional.of(userWithNullType));

        // Act & Assert
        // Based on `switch (user.getUserType())`, if `user.getUserType()` is null, it will throw a NullPointerException.
        // The `default` case with `IllegalStateException` would only be hit if `UserType` was an enum with more
        // values that are not covered by `case PASSENGER`, `case DRIVER`, `case ADMIN`.
        assertThrows(NullPointerException.class, () -> {
            userDetailsService.loadUserByUsername(testEmail);
        }, "A NullPointerException should be thrown if UserType is null and the switch statement tries to access it.");

        // If the intent was to test the "default" branch of the switch which throws IllegalStateException,
        // one would need to modify User.UserType enum to have another value, e.g., GUEST,
        // and then create a user with UserType.GUEST, and not have a "case GUEST:" in the switch.
    }
}
