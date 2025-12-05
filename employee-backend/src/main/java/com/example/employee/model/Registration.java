package com.example.employee.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "registration")
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registration_id")
    private Long registrationId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @Column(name="temp_password_hash")
    private String tempPasswordHash;

    @Column(name="verification_token")
    private String verificationToken;

    @Column(name="token_expires_at")
    private Instant tokenExpiresAt;

    @Column
    private String status;

    @ManyToOne
    @JoinColumn(name = "linked_employee_id")
    private Employee linkedEmployee;

    @Column(name="created_at")
    private Instant createdAt = Instant.now();

    @Column(name="updated_at")
    private Instant updatedAt = Instant.now();

    @Column(name="first_name")
    private String firstName;
    @Column(name="last_name")
    private String lastName;
    @Column(name="gender")
    private String gender;
    @Column(name="date_of_birth")
    private LocalDate dateOfBirth;
    @Column(name="department_id")
    private Long departmentId;
    @Column(name="designation_id")
    private Long designationId;
    @Column(name="grade_id")
    private Long gradeId;
    @Column(name="employee_type_id")
    private Long employeeTypeId;
    @Column(name="location_id")
    private Long locationId;
    @Column(name="date_of_joining")
    private LocalDate dateOfJoining;

    // getters/setters...
}
