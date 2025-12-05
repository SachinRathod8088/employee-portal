package com.example.employee.controller;

import com.example.employee.dto.LoginRequest;
import com.example.employee.dto.RegisterRequest;
import com.example.employee.dto.UserDto;
import com.example.employee.model.Employee;
import com.example.employee.model.EmployeeLogin;
import com.example.employee.service.AuthService;
import com.example.employee.config.JwtProvider;
import com.example.employee.repository.EmployeeLoginRepository;
import com.example.employee.repository.RegistrationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final EmployeeLoginRepository loginRepository;
    private final RegistrationRepository registrationRepository; // optional, may be unused

    public AuthController(AuthService authService,
                          JwtProvider jwtProvider,
                          EmployeeLoginRepository loginRepository,
                          RegistrationRepository registrationRepository) {
        this.authService = authService;
        this.jwtProvider = jwtProvider;
        this.loginRepository = loginRepository;
        this.registrationRepository = registrationRepository;
    }

    /**
     * Register: immediately creates Employee + EmployeeLogin (no admin approval).
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Validated @RequestBody RegisterRequest req) {
        try {
            Employee created = authService.register(req);
            return ResponseEntity.ok(Map.of("message", "Registered successfully", "employee", created));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(400).body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("message", "Server error"));
        }
    }

    /**
     * Login: validate credentials and return JWT + user DTO.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            EmployeeLogin login = authService.authenticate(req.username, req.password);
            String token = jwtProvider.generateToken(login.getUsername());
            Employee emp = login.getEmployee();

            UserDto user = new UserDto(
                    emp != null ? emp.getEmployeeId() : null,
                    login.getUsername(),
                    emp != null ? emp.getFirstName() : null,
                    emp != null ? emp.getLastName() : null,
                    emp != null ? emp.getEmail() : null,
                    login.getRole(),
                    login.getLastLogin()
            );

            return ResponseEntity.ok(Map.of("token", token, "user", user));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(401).body(Map.of("message", ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(400).body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("message", "Server error"));
        }
    }

    /**
     * Get current authenticated user's details.
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        String username = authentication.getName();
        EmployeeLogin login = loginRepository.findByUsername(username).orElse(null);
        if (login == null) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }
        return ResponseEntity.ok(Map.of("login", login, "employee", login.getEmployee()));
    }
}
