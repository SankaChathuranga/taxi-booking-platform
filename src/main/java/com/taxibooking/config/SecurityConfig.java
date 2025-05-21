package com.taxibooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**") // Disable CSRF for API endpoints
                .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/users/login", "POST")) // Require CSRF for login
            )
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/users/register", "/users/login", "/css/**", "/js/**").permitAll() // Allow access to registration and login pages
                    .requestMatchers("/passenger/**").hasRole("PASSENGER")
                    .requestMatchers("/driver/**").hasRole("DRIVER")
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated() // Secure all other requests
            )
            .formLogin(formLogin ->
                formLogin
                    .loginPage("/users/login") // Specify the custom login page
                    .loginProcessingUrl("/users/login") // Specify the URL to process the login attempt
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/users/login?error=true")
                    .permitAll() // Allow everyone to see the login page
            )
            .logout(logout ->
                logout
                    .logoutRequestMatcher(new AntPathRequestMatcher("/users/logout"))
                    .logoutSuccessUrl("/users/login?logout=true")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll() // Allow everyone to logout
            )
            .sessionManagement(session ->
                session
                    .maximumSessions(1)
                    .expiredUrl("/users/login?expired=true")
            );
        return http.build();
    }
} 