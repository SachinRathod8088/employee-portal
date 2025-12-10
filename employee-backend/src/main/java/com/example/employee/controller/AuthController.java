package com.example.employee.controller;

import com.example.employee.config.JwtProvider;
import com.example.employee.dto.ChangePasswordRequest;
import com.example.employee.dto.LoginRequest;
import com.example.employee.dto.RegisterRequest;
import com.example.employee.dto.UserDto;
import com.example.employee.model.Employee;
import com.example.employee.model.EmployeeLogin;
import com.example.employee.repository.EmployeeLoginRepository;
import com.example.employee.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final EmployeeLoginRepository loginRepository;

    public AuthController(AuthService authService,
                          JwtProvider jwtProvider,
                          EmployeeLoginRepository loginRepository) {
        this.authService = authService;
        this.jwtProvider = jwtProvider;
        this.loginRepository = loginRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Validated @RequestBody RegisterRequest req) {
        try {
            Employee created = authService.register(req);
            return ResponseEntity.ok(Map.of(
                    "message", "Registered successfully",
                    "employee", created
            ));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("message", "Server error"));
        }
    }

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
                    "USER",
                    login.getLastLogin()
            );
            return ResponseEntity.ok(Map.of("token", token, "user", user));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(401).body(Map.of("message", ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("message", "Server error"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        String username = authentication.getName();
        EmployeeLogin login = loginRepository.findByUsername(username).orElse(null);
        if (login == null) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }
        Employee emp = login.getEmployee();
        UserDto user = new UserDto(
                emp != null ? emp.getEmployeeId() : null,
                login.getUsername(),
                emp != null ? emp.getFirstName() : null,
                emp != null ? emp.getLastName() : null,
                emp != null ? emp.getEmail() : null,
                "USER",
                login.getLastLogin()
        );
        return ResponseEntity.ok(Map.of("user", user));
    }

    // Settings - profile
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        String username = auth.getName();
        EmployeeLogin login = loginRepository.findByUsername(username).orElse(null);
        if (login == null || login.getEmployee() == null) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }
        Employee emp = login.getEmployee();
        return ResponseEntity.ok(Map.of(
                "firstName", emp.getFirstName(),
                "lastName", emp.getLastName(),
                "email", emp.getEmail(),
                "phone", emp.getPhone()
        ));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication auth,
                                           @RequestBody Map<String, Object> body) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        String username = auth.getName();
        EmployeeLogin login = loginRepository.findByUsername(username).orElse(null);
        if (login == null || login.getEmployee() == null) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }
        Employee emp = login.getEmployee();
        if (body.containsKey("firstName")) {
            emp.setFirstName((String) body.get("firstName"));
        }
        if (body.containsKey("lastName")) {
            emp.setLastName((String) body.get("lastName"));
        }
        if (body.containsKey("phone")) {
            emp.setPhone((String) body.get("phone"));
        }
        emp.setUpdatedAt(Instant.now());
        loginRepository.save(login); // cascade will update employee if necessary
        return ResponseEntity.ok(Map.of("message", "Profile updated"));
    }

    // change password
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(Authentication auth,
                                            @Validated @RequestBody ChangePasswordRequest req) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        String username = auth.getName();
        try {
            authService.changePassword(username, req.getCurrentPassword(), req.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Password changed"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(400).body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("message", "Server error"));
        }
    }

    // Email/mobile verification simulation
    @PostMapping("/request-email-verification")
    public ResponseEntity<?> requestEmailVerification(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        return ResponseEntity.ok(Map.of("message", "Verification email would be sent (demo)."));
    }

    @PostMapping("/request-mobile-verification")
    public ResponseEntity<?> requestMobileVerification(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        return ResponseEntity.ok(Map.of("message", "Mobile OTP would be sent (demo)."));
    }
}
