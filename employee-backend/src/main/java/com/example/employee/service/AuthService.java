package com.example.employee.service;

import com.example.employee.dto.RegisterRequest;
import com.example.employee.model.Employee;
import com.example.employee.model.EmployeeLogin;
import com.example.employee.model.Registration;
import com.example.employee.repository.EmployeeLoginRepository;
import com.example.employee.repository.EmployeeRepository;
import com.example.employee.repository.RegistrationRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import com.example.employee.model.Gender;
import com.example.employee.model.EmployeeStatus;

@Service
public class AuthService {

    private final RegistrationRepository registrationRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeLoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(RegistrationRepository registrationRepository,
                       EmployeeRepository employeeRepository,
                       EmployeeLoginRepository loginRepository,
                       PasswordEncoder passwordEncoder) {
        this.registrationRepository = registrationRepository;
        this.employeeRepository = employeeRepository;
        this.loginRepository = loginRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Employee register(RegisterRequest req) {
        if (loginRepository.findByUsername(req.username).isPresent()) {
            throw new RuntimeException("Username already taken");
        }
        if (employeeRepository.findByEmail(req.email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Employee e = new Employee();
        String code = "EMP" + (System.currentTimeMillis() % 1000000);
        e.setEmployeeCode(code);
        e.setFirstName(req.firstName != null ? req.firstName : "");
        e.setLastName(req.lastName != null ? req.lastName : "");
        e.setEmail(req.email);
        e.setPhone(req.phone);
        e.setDateOfBirth(req.dateOfBirth);
        e.setDepartmentId(req.departmentId);
        e.setDesignationId(req.designationId);
        e.setGradeId(req.gradeId);
        e.setEmployeeTypeId(req.employeeTypeId);
        e.setLocationId(req.locationId);
        e.setDateOfJoining(req.dateOfJoining);

        // ✅ Parse gender safely into enum
        if (req.gender != null) {
            Gender g = Gender.fromString(req.gender);
            if (g != null) {
                e.setGender(g);
            }
        }

        // ✅ Parse status safely into enum (default to Active)
        if (req.status != null) {
            EmployeeStatus st = EmployeeStatus.fromString(req.status);
            if (st != null) {
                e.setStatus(st);
            } else {
                e.setStatus(EmployeeStatus.Active);
            }
        } else {
            e.setStatus(EmployeeStatus.Active);
        }

        e.setCreatedAt(Instant.now());
        e.setUpdatedAt(Instant.now());

        e = employeeRepository.save(e);

        EmployeeLogin login = new EmployeeLogin();
        login.setEmployee(e);
        login.setUsername(req.username);
        login.setPasswordHash(passwordEncoder.encode(req.password));
        login.setRole("USER");
        login.setCreatedAt(Instant.now());
        login.setUpdatedAt(Instant.now());
        loginRepository.save(login);

        // optionally create a registration row for audit — omitted

        return e;
    }

    public EmployeeLogin authenticate(String username, String password) {
        EmployeeLogin login = loginRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (login.getIsLocked() != null && login.getIsLocked()) {
            throw new IllegalArgumentException("Account locked");
        }

        if (login.getPasswordHash() == null || !passwordEncoder.matches(password, login.getPasswordHash())) {
            login.setFailedAttempts((login.getFailedAttempts() == null ? 0 : login.getFailedAttempts()) + 1);
            if (login.getFailedAttempts() != null && login.getFailedAttempts() >= 5) {
                login.setIsLocked(true);
            }
            loginRepository.save(login);
            throw new IllegalArgumentException("Invalid username or password");
        }

        login.setFailedAttempts(0);
        login.setLastLogin(Instant.now());
        loginRepository.save(login);
        return login;
    }
}