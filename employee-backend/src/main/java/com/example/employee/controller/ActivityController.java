package com.example.employee.controller;

import com.example.employee.model.ActivityLog;
import com.example.employee.repository.ActivityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    private final ActivityRepository activityRepo;

    public ActivityController(ActivityRepository activityRepo) {
        this.activityRepo = activityRepo;
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size) {
        Page<ActivityLog> p = activityRepo.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(p);
    }
}
