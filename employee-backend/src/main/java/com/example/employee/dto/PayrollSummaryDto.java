package com.example.employee.dto;

import java.math.BigDecimal;

public class PayrollSummaryDto {
    private int payrolls;
    private BigDecimal totalPaid;

    public PayrollSummaryDto() {}

    public PayrollSummaryDto(int payrolls, BigDecimal totalPaid) {
        this.payrolls = payrolls;
        this.totalPaid = totalPaid;
    }

    public int getPayrolls() { return payrolls; }
    public void setPayrolls(int payrolls) { this.payrolls = payrolls; }

    public BigDecimal getTotalPaid() { return totalPaid; }
    public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }
}
