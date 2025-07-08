package org.example.authService.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.authService.Entity.User;
import org.example.authService.dto.AuthRequest;
import org.example.authService.dto.AuthResponse;
import org.example.authService.dto.RegisterRequest;
import org.example.authService.repository.UserRepository;
import org.example.authService.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.authService.Entity.Role;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public void register(RegisterRequest request) {
        Role role = Role.valueOf(request.getRole().toUpperCase());
        User user = User.builder()
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(role)
                .build();
        userRepository.save(user);
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getRole().name());
    }
}
