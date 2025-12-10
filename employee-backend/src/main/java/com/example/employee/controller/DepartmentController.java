package com.example.employee.controller;

import com.example.employee.model.Department;
import com.example.employee.repository.DepartmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentRepository deptRepo;

    public DepartmentController(DepartmentRepository deptRepo) {
        this.deptRepo = deptRepo;
    }

    @GetMapping
    public List<Department> list() {
        return deptRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Department d) {
        d.setDepartmentId(null);
        return ResponseEntity.ok(deptRepo.save(d));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!deptRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        deptRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
