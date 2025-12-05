package com.example.employee.dto;

import com.example.employee.model.Employee;

public class LoginResponse {
    public String token;
    public Employee employee;

    public LoginResponse(String token, Employee employee) {
        this.token = token;
        this.employee = employee;
    }

    // getters optional
}
