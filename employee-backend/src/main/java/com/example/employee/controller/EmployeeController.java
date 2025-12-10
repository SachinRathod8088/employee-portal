package com.example.employee.controller;

import com.example.employee.model.Employee;
import com.example.employee.repository.EmployeeRepository;
import com.example.employee.repository.ActivityRepository;
import com.example.employee.model.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository empRepo;
    private final ActivityRepository activityRepo;

    public EmployeeController(EmployeeRepository empRepo, ActivityRepository activityRepo) {
        this.empRepo = empRepo;
        this.activityRepo = activityRepo;
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(defaultValue = "") String q) {

        List<Employee> all;
        if (q == null || q.isBlank()) {
            Page<Employee> p = empRepo.findAll(PageRequest.of(page, size));
            return ResponseEntity.ok(p);
        } else {
            all = empRepo.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    q, q, q
            );
            int from = page * size;
            int to = Math.min(from + size, all.size());
            List<Employee> sub = from > all.size() ? List.of() : all.subList(from, to);
            Page<Employee> p = new PageImpl<>(sub, PageRequest.of(page, size), all.size());
            return ResponseEntity.ok(p);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return empRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Employee employee) {
        employee.setEmployeeId(null);
        employee.setCreatedAt(Instant.now());
        employee.setUpdatedAt(Instant.now());
        if (employee.getEmployeeCode() == null || employee.getEmployeeCode().isBlank()) {
            employee.setEmployeeCode("EMP" + (System.currentTimeMillis() % 1000000));
        }
        Employee saved = empRepo.save(employee);
        logActivity("Employee created", "Created employee " + saved.getFirstName() + " " + saved.getLastName());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Employee updated) {
        return empRepo.findById(id).map(e -> {
            e.setFirstName(updated.getFirstName());
            e.setLastName(updated.getLastName());
            e.setEmail(updated.getEmail());
            e.setPhone(updated.getPhone());
            e.setStatus(updated.getStatus());
            e.setEmployeeCode(updated.getEmployeeCode());
            e.setUpdatedAt(Instant.now());
            empRepo.save(e);
            logActivity("Employee updated", "Updated employee " + e.getFirstName() + " " + e.getLastName());
            return ResponseEntity.ok(e);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return empRepo.findById(id).map(e -> {
            empRepo.delete(e);
            logActivity("Employee deleted", "Deleted employee " + e.getFirstName() + " " + e.getLastName());
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
