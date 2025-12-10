package com.example.employee.repository;

import com.example.employee.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<ActivityLog, Long> {
}
