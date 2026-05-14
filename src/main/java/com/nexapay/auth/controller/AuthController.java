package com.nexapay.auth.controller;

import com.nexapay.common.security.JwtService;
import com.nexapay.common.security.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication & RBAC", description = "Stateless JWT token issuance, session verification, and 1-click FinTech demo role switching")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public record LoginRequest(
            @NotBlank String username,
            String password,
            String roleHint // "ANALYST", "FRAUD_SPECIALIST", "SETTLEMENT_MANAGER", "ADMIN"
    ) {}

    public record AuthResponse(
            String token,
            String tokenType,
            String username,
            String role,
            String actorId
    ) {}

    @Operation(summary = "Authenticate & Obtain JWT Token", description = "Generates signed HMAC SHA-256 Bearer token with embedded role claims for authorization.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        UserRole role = determineRole(request.roleHint(), request.username());
        String actorId = "ACTOR-" + request.username().toUpperCase().replaceAll("[^A-Z0-9]", "");
        String token = jwtService.generateToken(request.username(), role, actorId);

        return ResponseEntity.ok(new AuthResponse(
                token,
                "Bearer",
                request.username(),
                role.name(),
                actorId
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthenticated"));
        }
        return ResponseEntity.ok(Map.of(
                "username", auth.getName(),
                "authorities", auth.getAuthorities()
        ));
    }

    private UserRole determineRole(String roleHint, String username) {
        if (roleHint != null) {
            try {
                if (!roleHint.startsWith("ROLE_")) {
                    roleHint = "ROLE_" + roleHint.toUpperCase();
                }
                return UserRole.valueOf(roleHint);
            } catch (Exception ignored) {}
        }
        if (username.toLowerCase().contains("admin")) return UserRole.ROLE_ADMIN;
        if (username.toLowerCase().contains("fraud")) return UserRole.ROLE_FRAUD_SPECIALIST;
        if (username.toLowerCase().contains("settle")) return UserRole.ROLE_SETTLEMENT_MANAGER;
        return UserRole.ROLE_OPERATIONS_ANALYST;
    }
}
