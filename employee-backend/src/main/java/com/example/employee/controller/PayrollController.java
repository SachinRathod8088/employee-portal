package com.example.employee.controller;

import com.example.employee.model.PayrollRun;
import com.example.employee.service.PayrollService;
import com.example.employee.dto.PayrollSummaryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {
    private final PayrollService svc;

    public PayrollController(PayrollService svc) { this.svc = svc; }

    @GetMapping("/summary")
    public ResponseEntity<PayrollSummaryDto> summary() {
        List<PayrollRun> runs = svc.listRuns();
        int count = runs.size();
        BigDecimal totalPaid = runs.stream()
                .map(r -> r.getTotalPaid() == null ? BigDecimal.ZERO : r.getTotalPaid())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        PayrollSummaryDto dto = new PayrollSummaryDto(count, totalPaid);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate() {
        PayrollRun run = svc.generatePayrollRun();
        return ResponseEntity.ok(run);
    }

    @GetMapping("/runs")
    public ResponseEntity<List<PayrollRun>> listRuns() {
        return ResponseEntity.ok(svc.listRuns());
    }
}
