package com.jacob.flowtrack.dto;

import java.math.BigDecimal;

public class ExpenseSummaryResponse {

    private BigDecimal totalSpent;
    private long totalExpenses;

    public ExpenseSummaryResponse(BigDecimal totalSpent, long totalExpenses) {
        this.totalSpent = totalSpent;
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public long getTotalExpenses() {
        return totalExpenses;
    }
}
