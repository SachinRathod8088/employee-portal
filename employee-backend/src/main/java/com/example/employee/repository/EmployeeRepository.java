package com.example.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.employee.model.Employee;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    List<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String fn, String ln, String email
    );
}
