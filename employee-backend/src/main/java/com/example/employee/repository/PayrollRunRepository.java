package com.example.employee.repository;

import com.example.employee.model.PayrollRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollRunRepository extends JpaRepository<PayrollRun, Long> {
    List<PayrollRun> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
}
