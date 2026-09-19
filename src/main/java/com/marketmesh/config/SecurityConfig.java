package com.marketmesh.config;

/**
 * Security configuration class that sets up the security filter chain and password encoding for the application.
 * Currently, it permits all requests to allow authentication endpoints to function while development is ongoing.
 * In the future, this will be replaced with proper JWT-based route protection.
 */
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // TEMPORARY: permits all requests so auth endpoints work while we build things.
    // This gets replaced with proper JWT-based route protection in the next step.
    @Bean 
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    //use to password encoded
    @Bean 
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}