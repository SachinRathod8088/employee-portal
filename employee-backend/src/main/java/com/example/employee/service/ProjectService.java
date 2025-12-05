package com.example.employee.service;

import com.example.employee.model.Project;
import com.example.employee.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository repo;

    public ProjectService(ProjectRepository repo) {
        this.repo = repo;
    }

    public List<Project> listAll() { return repo.findAll(); }
    public Optional<Project> get(Long id) { return repo.findById(id); }
    public Project create(Project p) { return repo.save(p); }
    public Project update(Long id, Project updated) {
        return repo.findById(id).map(ex -> {
            ex.setProjectName(updated.getProjectName());
            ex.setProjectCode(updated.getProjectCode());
            ex.setClientName(updated.getClientName());
            ex.setStartDate(updated.getStartDate());
            ex.setEndDate(updated.getEndDate());
            ex.setStatus(updated.getStatus());
            return repo.save(ex);
        }).orElseThrow(() -> new RuntimeException("Project not found"));
    }
    public void delete(Long id) { repo.deleteById(id); }
}
