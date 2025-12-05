package com.example.employee.repository;

import com.example.employee.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Page<Activity> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
