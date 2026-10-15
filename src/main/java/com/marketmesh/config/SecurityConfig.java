package com.marketmesh.config;

/**
 * Security configuration class that sets up the security filter chain and password encoding for the application.
 * Currently, it permits all requests to allow authentication endpoints to function while development is ongoing.
 * In the future, this will be replaced with proper JWT-based route protection.
 */
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // TEMPORARY: permits all requests so auth endpoints work while we build things.
    // This gets replaced with proper JWT-based route protection in the next step.
    @Bean 
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // register/login stay open (no auth required or token required)
                .requestMatchers("/api/auth/**").permitAll() 
                // anyone can browse
                .requestMatchers(HttpMethod.GET, "/api/categories/**", "/api/products/**").permitAll() 
                // only admin can create/update/delete
                .requestMatchers("/api/categories/**", "/api/products/**").hasRole("ADMIN")
                //every other endpoint now requires a valid Bearer token.
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated() 
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    //use to password encoded
    @Bean 
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}