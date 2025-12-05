package com.example.employee.service;

import com.example.employee.model.Activity;
import com.example.employee.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ActivityService {
    private final ActivityRepository repo;

    public ActivityService(ActivityRepository repo) { this.repo = repo; }

    public Page<Activity> listRecent(Pageable pageable) {
        return repo.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Activity create(Activity a) { return repo.save(a); }
}
