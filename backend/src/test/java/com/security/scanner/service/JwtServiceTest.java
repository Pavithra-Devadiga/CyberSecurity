package com.security.scanner.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 3600000L);
    }

    @Test
    @DisplayName("Should generate valid JWT token and extract username")
    void testTokenGenerationAndExtraction() {
        UserDetails userDetails = new User("sec_tester", "pass", Collections.emptyList());

        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertFalse(token.isEmpty());

        String username = jwtService.extractUsername(token);
        assertEquals("sec_tester", username);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    @DisplayName("Should invalidate token for different user")
    void testTokenInvalidForDifferentUser() {
        UserDetails userDetails1 = new User("alice", "pass", Collections.emptyList());
        UserDetails userDetails2 = new User("bob", "pass", Collections.emptyList());

        String token = jwtService.generateToken(userDetails1);
        assertFalse(jwtService.isTokenValid(token, userDetails2));
    }
}
