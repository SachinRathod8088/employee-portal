package com.example.employee.controller;

import com.example.employee.model.Activity;
import com.example.employee.service.ActivityService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    private final ActivityService svc;

    public ActivityController(ActivityService svc) { this.svc = svc; }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(svc.listRecent(PageRequest.of(page, size)));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Activity a) {
        return ResponseEntity.ok(svc.create(a));
    }
}
