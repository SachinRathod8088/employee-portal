package com.example.employee.controller;

import com.example.employee.model.Skill;
import com.example.employee.repository.SkillRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillRepository skillRepo;

    public SkillController(SkillRepository skillRepo) {
        this.skillRepo = skillRepo;
    }

    @GetMapping
    public List<Skill> list() {
        return skillRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return skillRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Skill s) {
        s.setSkillId(null);
        return ResponseEntity.ok(skillRepo.save(s));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Skill updated) {
        return skillRepo.findById(id).map(s -> {
            s.setSkillName(updated.getSkillName());
            s.setSkillCategory(updated.getSkillCategory());
            s.setSkillExperienceCategory(updated.getSkillExperienceCategory());
            return ResponseEntity.ok(skillRepo.save(s));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!skillRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        skillRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
