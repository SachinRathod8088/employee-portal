package com.example.employee.controller;

import com.example.employee.model.Attendance;
import com.example.employee.repository.AttendanceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceRepository attendanceRepo;

    public AttendanceController(AttendanceRepository attendanceRepo) {
        this.attendanceRepo = attendanceRepo;
    }

    @GetMapping
    public List<Attendance> list(@RequestParam(required = false) Long employeeId) {
        if (employeeId != null) {
            return attendanceRepo.findByEmployeeId(employeeId);
        }
        return attendanceRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Attendance a) {
        a.setAttendanceId(null);
        return ResponseEntity.ok(attendanceRepo.save(a));
    }
}
