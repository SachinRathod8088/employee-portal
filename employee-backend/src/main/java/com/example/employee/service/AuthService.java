package com.example.employee.service;

import com.example.employee.dto.RegisterRequest;
import com.example.employee.model.Employee;
import com.example.employee.model.EmployeeLogin;
import com.example.employee.repository.EmployeeLoginRepository;
import com.example.employee.repository.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeLoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(EmployeeRepository employeeRepository,
                       EmployeeLoginRepository loginRepository,
                       PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.loginRepository = loginRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Employee register(RegisterRequest req) {
        Optional<Employee> existingEmail = employeeRepository.findByEmail(req.email);
        if (existingEmail.isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        if (loginRepository.findByUsername(req.username).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        Employee e = new Employee();
        e.setFirstName(req.firstName);
        e.setLastName(req.lastName);
        e.setEmail(req.email);
        e.setPhone(req.phone);
        e.setStatus("Active");
        e.setCreatedAt(Instant.now());
        e.setUpdatedAt(Instant.now());
        // simple code generator
        e.setEmployeeCode("EMP" + (System.currentTimeMillis() % 1000000));
        e = employeeRepository.save(e);

        EmployeeLogin login = new EmployeeLogin();
        login.setEmployee(e);
        login.setUsername(req.username);
        login.setPasswordHash(passwordEncoder.encode(req.password));
        loginRepository.save(login);

        return e;
    }

    @Transactional
    public EmployeeLogin authenticate(String username, String password) {
        EmployeeLogin login = loginRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(password, login.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        login.setLastLogin(Instant.now());
        loginRepository.save(login);
        return login;
    }

    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        EmployeeLogin login = loginRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(currentPassword, login.getPasswordHash())) {
            throw new IllegalArgumentException("Current password incorrect");
        }
        login.setPasswordHash(passwordEncoder.encode(newPassword));
        loginRepository.save(login);
    }
}
