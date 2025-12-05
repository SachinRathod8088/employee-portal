package com.example.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.employee.model.EmployeeLogin;
import java.util.Optional;

public interface EmployeeLoginRepository extends JpaRepository<EmployeeLogin, Long> {
    Optional<EmployeeLogin> findByUsername(String username);
}
