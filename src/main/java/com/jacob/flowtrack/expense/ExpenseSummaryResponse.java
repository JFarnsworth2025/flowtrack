package com.jacob.flowtrack.expense;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ExpenseSummaryResponse {

    private BigDecimal totalSpent;
    private long totalExpenses;

}