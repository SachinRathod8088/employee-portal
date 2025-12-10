package com.example.employee.repository;

import com.example.employee.model.EmployeeSalary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeSalaryRepository extends JpaRepository<EmployeeSalary, Long> {
    List<EmployeeSalary> findByEmployeeIdOrderByEffectiveFromDesc(Long employeeId);
}
