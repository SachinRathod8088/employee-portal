package com.example.employee.dto;

import java.time.Instant;

public class UserDto {
    public Long employeeId;
    public String username;
    public String firstName;
    public String lastName;
    public String email;
    public String role;
    public Instant lastLogin;

    public UserDto() {}

    public UserDto(Long employeeId, String username, String firstName, String lastName, String email, String role, Instant lastLogin) {
        this.employeeId = employeeId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.lastLogin = lastLogin;
    }
}
