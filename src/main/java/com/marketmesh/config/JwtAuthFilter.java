package com.marketmesh.config;

/*
*use this filter to check for a JWT in the Authorization header of incoming requests.
*use the JwtUtil class to validate the token and extract the email from it.
*use the email to create a UsernamePasswordAuthenticationToken and set it in the SecurityContextHolder.
*use the OncePerRequestFilter to ensure that this filter is only executed once per request.
*use the @Component annotation to register this filter as a Spring bean.
*/
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {
                                     
        /*
        * Get the Authorization header from the request.
        * If the header is null or doesn't start with "Bearer ", let the request pass
        * the filter chain without authentication. The SecurityConfig will decide if the endpoint requires authentication.
        * If the header is valid, extract the token and validate it using JwtUtil.
         */                      
        String authHeader = request.getHeader("Authorization");

        // No token, or doesn't start with "Bearer " - let it pass through unauthenticated.
        // SecurityConfig decides whether the target endpoint actually requires auth.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // strip "Bearer " prefix

        if (jwtUtil.isTokenValid(token)) {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);

            // Tell Spring Security: "this request is authenticated as this email"
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}