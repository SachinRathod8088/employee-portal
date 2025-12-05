package com.example.employee.controller;

import com.example.employee.model.Employee;
import com.example.employee.repository.EmployeeRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeRepository empRepo;

    public EmployeeController(EmployeeRepository empRepo) {
        this.empRepo = empRepo;
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "50") int size,
                                  @RequestParam(defaultValue = "") String q) {
        // simple pageable implementation: search by first/last/email if q provided
        Pageable pageable = PageRequest.of(page, size);
        if (q != null && !q.isBlank()) {
            Page<Employee> p = empRepo.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndIsDeletedFalse(
                    q, q, q, pageable);
            return ResponseEntity.ok(p);
        } else {
            Page<Employee> p = empRepo.findByIsDeletedFalse(pageable);
            return ResponseEntity.ok(p);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return empRepo.findById(id)
                .filter(e -> e.getIsDeleted() == null || !e.getIsDeleted())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Employee employee) {
        employee.setCreatedAt(java.time.Instant.now());
        employee.setUpdatedAt(java.time.Instant.now());
        Employee saved = empRepo.save(employee);
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
            e.setUpdatedAt(java.time.Instant.now());
            empRepo.save(e);
            return ResponseEntity.ok(e);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return empRepo.findById(id).map(e -> {
            e.setIsDeleted(true);
            e.setUpdatedAt(java.time.Instant.now());
            empRepo.save(e);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
