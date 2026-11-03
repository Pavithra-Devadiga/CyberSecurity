package com.security.scanner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.scanner.dto.request.LoginRequest;
import com.security.scanner.dto.request.RegisterRequest;
import com.security.scanner.dto.response.AuthResponse;
import com.security.scanner.dto.response.UserDto;
import com.security.scanner.entity.enums.Role;
import com.security.scanner.service.AuthService;
import com.security.scanner.service.JwtService;
import com.security.scanner.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("POST /api/auth/register should register and return token")
    void testRegisterEndpoint() throws Exception {
        RegisterRequest req = new RegisterRequest("newuser", "newuser@example.com", "Password@123");
        UserDto userDto = new UserDto(1L, "newuser", "newuser@example.com", Role.ROLE_USER, LocalDateTime.now());
        AuthResponse authRes = new AuthResponse("mock-jwt-token-xyz", userDto);

        Mockito.when(authService.register(any(RegisterRequest.class))).thenReturn(authRes);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("mock-jwt-token-xyz"))
                .andExpect(jsonPath("$.data.user.username").value("newuser"));
    }

    @Test
    @DisplayName("POST /api/auth/login should authenticate and return token")
    void testLoginEndpoint() throws Exception {
        LoginRequest req = new LoginRequest("newuser", "Password@123");
        UserDto userDto = new UserDto(1L, "newuser", "newuser@example.com", Role.ROLE_USER, LocalDateTime.now());
        AuthResponse authRes = new AuthResponse("mock-login-token-123", userDto);

        Mockito.when(authService.login(any(LoginRequest.class))).thenReturn(authRes);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("mock-login-token-123"));
    }
}
