package com.example.employee.service;

import com.example.employee.model.PayrollRun;
import com.example.employee.repository.PayrollRunRepository;
import com.example.employee.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

@Service
public class PayrollService {
    private final PayrollRunRepository payrollRepo;
    private final EmployeeRepository employeeRepo;

    public PayrollService(PayrollRunRepository payrollRepo, EmployeeRepository employeeRepo) {
        this.payrollRepo = payrollRepo;
        this.employeeRepo = employeeRepo;
    }

    /**
     * Generate a simple payroll run:
     * - For demo: counts employees & uses placeholder amount (employees * 1000)
     * - Stores a PayrollRun record
     */
    public PayrollRun generatePayrollRun() {
        long employees = employeeRepo.count();

        // placeholder totalPaid calculation (replace with real logic later)
        BigDecimal perEmployee = BigDecimal.valueOf(1000.00);
        BigDecimal totalPaid = perEmployee.multiply(BigDecimal.valueOf(employees));

        PayrollRun run = new PayrollRun();
        run.setRunReference("PR-" + UUID.randomUUID().toString().substring(0,8));
        run.setTotalEmployees((int) employees);
        run.setTotalPaid(totalPaid);
        return payrollRepo.save(run);
    }

    public List<PayrollRun> listRuns() { return payrollRepo.findAll(); }
}
