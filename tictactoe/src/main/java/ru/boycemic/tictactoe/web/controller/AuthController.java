package ru.boycemic.tictactoe.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.boycemic.tictactoe.domain.service.AuthService;
import ru.boycemic.tictactoe.web.model.SignUpRequest;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody SignUpRequest request) {
        boolean success = authService.register(request);
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User registered successfully"));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "User already exists"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, UUID>> login(@RequestHeader("Authorization") String authHeader) {
        UUID userId = authService.authenticate(authHeader);
        return ResponseEntity.ok(Map.of("userId", userId));
    }
}
