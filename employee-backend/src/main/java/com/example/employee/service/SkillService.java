package com.example.employee.service;

import com.example.employee.model.Skill;
import com.example.employee.repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillService {
    private final SkillRepository repo;

    public SkillService(SkillRepository repo) { this.repo = repo; }

    public List<Skill> listAll() { return repo.findAll(); }
    public Skill create(Skill s) { return repo.save(s); }
}
