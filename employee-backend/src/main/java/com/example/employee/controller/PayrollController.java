package com.example.employee.controller;

import com.example.employee.model.PayrollRun;
import com.example.employee.service.PayrollService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody Map<String, String> body) {
        try {
            Long employeeId = Long.valueOf(body.get("employeeId"));
            LocalDate from = body.get("from") != null ? LocalDate.parse(body.get("from")) : LocalDate.now().withDayOfMonth(1);
            LocalDate to = body.get("to") != null ? LocalDate.parse(body.get("to")) : LocalDate.now();
            PayrollRun run = payrollService.generatePayroll(employeeId, from, to);
            return ResponseEntity.ok(run);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping
    public List<PayrollRun> list(@RequestParam(required = false) Long employeeId) {
        return payrollService.listRuns(employeeId);
    }

    @DeleteMapping("/{runId}")
    public ResponseEntity<?> delete(@PathVariable Long runId) {
        payrollService.deleteRun(runId);
        return ResponseEntity.noContent().build();
    }
}
