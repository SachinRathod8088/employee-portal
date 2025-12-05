package com.example.employee.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.math.BigDecimal;

@Entity
@Table(name = "payroll_run")
public class PayrollRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payroll_id")
    private Long payrollId;

    @Column(name = "run_reference", unique = true)
    private String runReference;

    @Column(name = "run_date")
    private Instant runDate = Instant.now();

    @Column(name = "total_employees")
    private Integer totalEmployees = 0;

    // Use BigDecimal to match NUMERIC in DB
    @Column(name = "total_paid", precision = 14, scale = 2)
    private BigDecimal totalPaid = BigDecimal.ZERO;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    // getters/setters
    public Long getPayrollId() { return payrollId; }
    public void setPayrollId(Long payrollId) { this.payrollId = payrollId; }

    public String getRunReference() { return runReference; }
    public void setRunReference(String runReference) { this.runReference = runReference; }

    public Instant getRunDate() { return runDate; }
    public void setRunDate(Instant runDate) { this.runDate = runDate; }

    public Integer getTotalEmployees() { return totalEmployees; }
    public void setTotalEmployees(Integer totalEmployees) { this.totalEmployees = totalEmployees; }

    public BigDecimal getTotalPaid() { return totalPaid; }
    public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
