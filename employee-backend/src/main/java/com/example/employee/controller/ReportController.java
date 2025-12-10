package com.example.employee.controller;

import com.example.employee.model.Employee;
import com.example.employee.model.Attendance;
import com.example.employee.model.LeaveRequest;
import com.example.employee.model.PayrollRun;
import com.example.employee.repository.EmployeeRepository;
import com.example.employee.repository.AttendanceRepository;
import com.example.employee.repository.LeaveRepository;
import com.example.employee.repository.PayrollRunRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final EmployeeRepository employeeRepo;
    private final AttendanceRepository attendanceRepo;
    private final LeaveRepository leaveRepo;
    private final PayrollRunRepository runRepo;

    public ReportController(EmployeeRepository employeeRepo,
                            AttendanceRepository attendanceRepo,
                            LeaveRepository leaveRepo,
                            PayrollRunRepository runRepo) {
        this.employeeRepo = employeeRepo;
        this.attendanceRepo = attendanceRepo;
        this.leaveRepo = leaveRepo;
        this.runRepo = runRepo;
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> employeeReport(@PathVariable Long employeeId,
                                            @RequestParam(required = false) String from,
                                            @RequestParam(required = false) String to) {
        Optional<Employee> eOpt = employeeRepo.findById(employeeId);
        if (eOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        LocalDate fromDate = from != null && !from.isBlank() ? LocalDate.parse(from) : null;
        LocalDate toDate = to != null && !to.isBlank() ? LocalDate.parse(to) : null;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("employee", eOpt.get());

        List<Attendance> attendance = attendanceRepo.findByEmployeeId(employeeId);
        List<LeaveRequest> leaves = leaveRepo.findByEmployeeId(employeeId);
        List<PayrollRun> runs = runRepo.findByEmployeeIdOrderByCreatedAtDesc(employeeId);

        result.put("attendance", attendance);
        result.put("leaves", leaves);
        result.put("payrollRuns", runs);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<?> allEmployeesReport(@RequestParam(required = false) String from,
                                                @RequestParam(required = false) String to) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("employeeCount", employeeRepo.count());
        result.put("payrollRuns", runRepo.findAll());
        result.put("leaves", leaveRepo.findAll());
        result.put("attendance", attendanceRepo.findAll());
        return ResponseEntity.ok(result);
    }
}
