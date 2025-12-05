package com.example.employee.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "employee_code", unique = true)
    private String employeeCode;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    // map Postgres enum gender_enum
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", columnDefinition = "gender_enum")
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "alternate_phone")
    private String alternatePhone;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "designation_id")
    private Long designationId;

    @Column(name = "grade_id")
    private Long gradeId;

    @Column(name = "employee_type_id")
    private Long employeeTypeId;

    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining;

    @Column(name = "date_of_leaving")
    private LocalDate dateOfLeaving;

    // map Postgres enum emp_status_enum
    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "emp_status_enum")
    private EmployeeStatus status;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Employee() {}

    // getters & setters (full set)

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone(){ return phone; }
    public void setPhone(String phone){ this.phone = phone; }

    public String getAlternatePhone(){ return alternatePhone; }
    public void setAlternatePhone(String alternatePhone){ this.alternatePhone = alternatePhone; }

    public Long getDepartmentId(){ return departmentId; }
    public void setDepartmentId(Long departmentId){ this.departmentId = departmentId; }

    public Long getDesignationId(){ return designationId; }
    public void setDesignationId(Long designationId){ this.designationId = designationId; }

    public Long getGradeId(){ return gradeId; }
    public void setGradeId(Long gradeId){ this.gradeId = gradeId; }

    public Long getEmployeeTypeId(){ return employeeTypeId; }
    public void setEmployeeTypeId(Long employeeTypeId){ this.employeeTypeId = employeeTypeId; }

    public Long getLocationId(){ return locationId; }
    public void setLocationId(Long locationId){ this.locationId = locationId; }

    public LocalDate getDateOfJoining(){ return dateOfJoining; }
    public void setDateOfJoining(LocalDate dateOfJoining){ this.dateOfJoining = dateOfJoining; }

    public LocalDate getDateOfLeaving(){ return dateOfLeaving; }
    public void setDateOfLeaving(LocalDate dateOfLeaving){ this.dateOfLeaving = dateOfLeaving; }

    public EmployeeStatus getStatus(){ return status; }
    public void setStatus(EmployeeStatus status){ this.status = status; }

    public Boolean getIsDeleted(){ return isDeleted; }
    public void setIsDeleted(Boolean isDeleted){ this.isDeleted = isDeleted; }

    public Long getCreatedBy(){ return createdBy; }
    public void setCreatedBy(Long createdBy){ this.createdBy = createdBy; }

    public Instant getCreatedAt(){ return createdAt; }
    public void setCreatedAt(Instant createdAt){ this.createdAt = createdAt; }

    public Instant getUpdatedAt(){ return updatedAt; }
    public void setUpdatedAt(Instant updatedAt){ this.updatedAt = updatedAt; }
}
