package com.example.employee.service;

import com.example.employee.model.ActivityLog;
import com.example.employee.repository.ActivityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /**
     * Create and save a new activity log entry.
     *
     * @param title       Short title like "Employee created"
     * @param description More detailed description
     * @param createdBy   Username or system label (can be null or "system")
     * @return saved ActivityLog
     */
    public ActivityLog logActivity(String title, String description, String createdBy) {
        ActivityLog a = new ActivityLog();
        a.setTitle(title);
        a.setDescription(description);
        a.setCreatedBy(createdBy);
        a.setCreatedAt(Instant.now());
        return activityRepository.save(a);
    }

    /**
     * Overload: log activity without createdBy (default "system").
     */
    public ActivityLog logActivity(String title, String description) {
        return logActivity(title, description, "system");
    }

    /**
     * Get paginated recent activity for the RecentActivity page.
     */
    public Page<ActivityLog> getRecent(int page, int size) {
        return activityRepository.findAll(PageRequest.of(page, size));
    }
}
