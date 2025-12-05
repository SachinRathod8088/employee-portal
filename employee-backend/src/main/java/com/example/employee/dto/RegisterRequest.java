package com.example.employee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class RegisterRequest {
    @NotBlank
    public String username;

    @NotBlank
    @Email
    public String email;

    public String phone;

    @NotBlank
    public String password;

    public String firstName;
    public String lastName;
    public String gender;
    public LocalDate dateOfBirth;
    public Long departmentId;
    public Long designationId;
    public Long gradeId;
    public Long employeeTypeId;
    public Long locationId;
    public LocalDate dateOfJoining;
    public String status;
}
