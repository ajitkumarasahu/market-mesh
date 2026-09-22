package com.marketmesh.test;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    // This should work WITHOUT a token, since only /api/auth/** is public...
    // wait, actually this path is NOT under /api/auth/**, so it SHOULD require a token.
    // We're using this to prove that.
  @GetMapping("/public-check")
    public String publicCheck() {
        return "If you see this WITHOUT a token, SecurityConfig is NOT protecting routes correctly.";
    }

    // This should work WITH a valid token, and show you which email the filter extracted
    @GetMapping("/secure-check")
    public String secureCheck() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (auth != null) ? auth.getName() : "NO AUTH FOUND";
        return "You are authenticated as: " + email;
    }
}