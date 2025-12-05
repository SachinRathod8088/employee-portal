package com.example.employee.controller;

import com.example.employee.model.Skill;
import com.example.employee.service.SkillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillController {
    private final SkillService svc;

    public SkillController(SkillService svc) { this.svc = svc; }

    @GetMapping
    public ResponseEntity<List<Skill>> list() {
        return ResponseEntity.ok(svc.listAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Skill s) {
        return ResponseEntity.ok(svc.create(s));
    }
}
