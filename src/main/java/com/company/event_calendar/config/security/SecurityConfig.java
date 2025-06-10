package com.company.event_calendar.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.company.event_calendar.user.service.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(withDefaults())

                .build();
    }

    

    // This bean will be used by Spring Security to handle authentication requests
    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        // DaoAuthenticationProvider is a built-in Spring Security class that
        // authenticates users from a UserDetailsService and a PasswordEncoder.
        // It verifies credentials against stored user details (usually fetched from a
        // database or another user repository).
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        /*
         * ProviderManager is Spring Security's default AuthenticationManager
         * implementation.
         * 
         * It manages a list of AuthenticationProviders — in this case, just one: the
         * DaoAuthenticationProvider.
         * 
         * When authentication is requested, the ProviderManager delegates the request
         * to its providers until one successfully authenticates or all fail. so Spring
         * Security calls the AuthenticationManager.authenticate() method then
         * The ProviderManager passes the authentication request to the
         * DaoAuthenticationProvider
         */
        return new ProviderManager(daoAuthenticationProvider);
    }
}

// Passing withDefaults() to these methods applies Spring Security's default
// settings