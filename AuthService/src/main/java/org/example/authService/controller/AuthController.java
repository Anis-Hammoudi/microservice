package org.example.authService.controller;

import lombok.RequiredArgsConstructor;
import org.example.authService.dto.AuthRequest;
import org.example.authService.dto.AuthResponse;
import org.example.authService.dto.RegisterRequest;
import org.example.authService.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest request) {
        authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }
}