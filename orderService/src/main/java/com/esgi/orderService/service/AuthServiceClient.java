package com.esgi.orderService.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${AUTH_SERVICE_URL:http://localhost:8084/auth}")
    private String authServiceUrl;

    @Data
    public static class UserInfo {
        private Long userId;
        private String email;
        private String role;
    }

    public UserInfo getUserInfo(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<UserInfo> response = restTemplate.exchange(
                authServiceUrl + "/user-info",
                HttpMethod.GET,
                entity,
                UserInfo.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user info from auth service", e);
        }
    }
} 