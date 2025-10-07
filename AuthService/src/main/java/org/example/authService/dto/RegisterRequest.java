package org.example.authService.dto;


import lombok.*;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String role; // CLIENT / CHEF / LIVREUR
}

