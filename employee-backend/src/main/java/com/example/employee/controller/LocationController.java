package com.example.employee.controller;

import com.example.employee.model.Location;
import com.example.employee.model.Project;
import com.example.employee.repository.LocationRepository;
import com.example.employee.repository.ProjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationRepository locationRepo;
    private final ProjectRepository projectRepo;

    public LocationController(LocationRepository locationRepo,
                              ProjectRepository projectRepo) {
        this.locationRepo = locationRepo;
        this.projectRepo = projectRepo;
    }

    @GetMapping
    public List<Location> list() {
        return locationRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Location l) {
        l.setLocationId(null);
        return ResponseEntity.ok(locationRepo.save(l));
    }

    // simple demo: return all projects (you can later filter by location_id column if you add it)
    @GetMapping("/{locationId}/projects")
    public ResponseEntity<?> projectsByLocation(@PathVariable Long locationId) {
        // for now return all projects; you can later add a location_id column in project_master and filter.
        List<Project> projects = projectRepo.findAll();
        return ResponseEntity.ok(projects);
    }
}
