package com.project.back_end.services;

import com.project.back_end.dto.Login;
import com.project.back_end.exceptions.UnauthorizedException;
import com.project.back_end.models.Admin;
import com.project.back_end.repo.AdminRepository;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AdminRepository adminRepository;
    private final TokenService tokenService;

    public AuthService(AdminRepository adminRepository, TokenService tokenService) {
        this.adminRepository = adminRepository;
        this.tokenService = tokenService;
    }

    public Map<String, Object> validateAdminLogin(Login login) {
        Admin admin = adminRepository.findByUsername(login.getIdentifier())
                .orElseThrow(() -> new UnauthorizedException("Invalid admin username or password"));

        if (!admin.getPassword().equals(login.getPassword())) {
            throw new UnauthorizedException("Invalid admin username or password");
        }

        String token = tokenService.generateToken(admin.getUsername(), "ADMIN", admin.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", "ADMIN");
        response.put("userId", admin.getId());
        response.put("username", admin.getUsername());
        return response;
    }

    public boolean validateToken(String token, String expectedRole) {
        if (token == null || token.isBlank()) {
            return false;
        }
        return tokenService.isTokenValidForRole(token, expectedRole);
    }

    public void requireRole(String token, String expectedRole) {
        if (!validateToken(token, expectedRole)) {
            throw new UnauthorizedException("Invalid or expired token for role: " + expectedRole);
        }
    }

    public void requireAnyRole(String token, String... allowedRoles) {
        if (token == null || token.isBlank() || !tokenService.isTokenValid(token)) {
            throw new UnauthorizedException("Invalid or expired authentication token");
        }
        String role = tokenService.extractRole(token);
        for (String allowed : allowedRoles) {
            if (allowed.equalsIgnoreCase(role)) {
                return;
            }
        }
        throw new UnauthorizedException("Insufficient permissions for role: " + role);
    }
}
