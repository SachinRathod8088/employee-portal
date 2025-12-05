package com.example.employee.repository;

import com.example.employee.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Skill findBySkillName(String name);
}
