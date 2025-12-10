package com.example.employee.controller;

import com.example.employee.model.Project;
import com.example.employee.model.ActivityLog;
import com.example.employee.repository.ProjectRepository;
import com.example.employee.repository.ActivityRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectRepository projectRepo;
    private final ActivityRepository activityRepo;

    public ProjectController(ProjectRepository projectRepo, ActivityRepository activityRepo) {
        this.projectRepo = projectRepo;
        this.activityRepo = activityRepo;
    }

    @GetMapping
    public List<Project> list() {
        return projectRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return projectRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Project p) {
        p.setProjectId(null);
        p.setCreatedAt(Instant.now());
        p.setUpdatedAt(Instant.now());
        if (p.getProjectCode() == null || p.getProjectCode().isBlank()) {
            p.setProjectCode("PRJ" + (System.currentTimeMillis() % 1000000));
        }
        Project saved = projectRepo.save(p);
        logActivity("Project created", "Project " + saved.getProjectName() + " created");
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Project updated) {
        return projectRepo.findById(id).map(p -> {
            p.setProjectName(updated.getProjectName());
            p.setClientName(updated.getClientName());
            p.setStartDate(updated.getStartDate());
            p.setEndDate(updated.getEndDate());
            p.setStatus(updated.getStatus());
            p.setUpdatedAt(Instant.now());
            projectRepo.save(p);
            logActivity("Project updated", "Project " + p.getProjectName() + " updated");
            return ResponseEntity.ok(p);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return projectRepo.findById(id).map(p -> {
            projectRepo.delete(p);
            logActivity("Project deleted", "Project " + p.getProjectName() + " deleted");
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    private void logActivity(String title, String desc) {
        ActivityLog a = new ActivityLog();
        a.setTitle(title);
        a.setDescription(desc);
        a.setCreatedAt(Instant.now());
        activityRepo.save(a);
    }
}
