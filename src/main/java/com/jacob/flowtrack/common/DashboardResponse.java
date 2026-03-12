package com.jacob.flowtrack.common;

import com.jacob.flowtrack.expense.ExpenseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class DashboardResponse {

    private BigDecimal totalSpent;
    private long totalExpenses;

    private Map<String, BigDecimal> categoryBreakdown;
    private Map<String, BigDecimal> monthlyBreakdown;

    private List<ExpenseResponse> recentExpenses;

}