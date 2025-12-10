package com.example.employee.controller;

import com.example.employee.model.LeaveRequest;
import com.example.employee.repository.LeaveRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveRepository leaveRepo;

    public LeaveController(LeaveRepository leaveRepo) {
        this.leaveRepo = leaveRepo;
    }

    @GetMapping
    public List<LeaveRequest> list(@RequestParam(required = false) Long employeeId) {
        if (employeeId != null) {
            return leaveRepo.findByEmployeeId(employeeId);
        }
        return leaveRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody LeaveRequest l) {
        l.setLeaveId(null);
        l.setStatus("PENDING");
        return ResponseEntity.ok(leaveRepo.save(l));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        return leaveRepo.findById(id).map(l -> {
            l.setStatus("APPROVED");
            leaveRepo.save(l);
            return ResponseEntity.ok(l);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        return leaveRepo.findById(id).map(l -> {
            l.setStatus("REJECTED");
            leaveRepo.save(l);
            return ResponseEntity.ok(l);
        }).orElse(ResponseEntity.notFound().build());
    }
}
