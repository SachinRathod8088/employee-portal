package com.example.employee.service;

import com.example.employee.model.EmployeeSalary;
import com.example.employee.model.PayrollRun;
import com.example.employee.repository.EmployeeSalaryRepository;
import com.example.employee.repository.PayrollRunRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PayrollService {

    private final EmployeeSalaryRepository salaryRepo;
    private final PayrollRunRepository runRepo;

    public PayrollService(EmployeeSalaryRepository salaryRepo,
                          PayrollRunRepository runRepo) {
        this.salaryRepo = salaryRepo;
        this.runRepo = runRepo;
    }

    @Transactional
    public PayrollRun generatePayroll(Long employeeId, LocalDate from, LocalDate to) {
        List<EmployeeSalary> salaries = salaryRepo.findByEmployeeIdOrderByEffectiveFromDesc(employeeId);
        if (salaries.isEmpty()) {
            throw new RuntimeException("No salary record for employee " + employeeId);
        }

        EmployeeSalary current = salaries.get(0);
        BigDecimal paid = current.getNetSalary() != null ? current.getNetSalary() : BigDecimal.ZERO;

        PayrollRun run = new PayrollRun();
        run.setEmployeeId(employeeId);
        run.setPeriodStart(from);
        run.setPeriodEnd(to);
        run.setTotalPaid(paid);

        return runRepo.save(run);
    }

    public List<PayrollRun> listRuns(Long employeeId) {
        if (employeeId != null) {
            return runRepo.findByEmployeeIdOrderByCreatedAtDesc(employeeId);
        }
        return runRepo.findAll();
    }

    public void deleteRun(Long runId) {
        runRepo.deleteById(runId);
    }
}
