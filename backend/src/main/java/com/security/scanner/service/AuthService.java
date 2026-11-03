package com.security.scanner.service;

import com.security.scanner.dto.request.LoginRequest;
import com.security.scanner.dto.request.RegisterRequest;
import com.security.scanner.dto.response.AuthResponse;
import com.security.scanner.dto.response.UserDto;
import com.security.scanner.entity.User;
import com.security.scanner.entity.enums.Role;
import com.security.scanner.exception.BadRequestException;
import com.security.scanner.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            UserDetailsServiceImpl userDetailsService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.ROLE_USER
        );

        user = userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, mapToDto(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found"));

        return new AuthResponse(token, mapToDto(user));
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));
        return mapToDto(user);
    }

    public UserDto mapToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
