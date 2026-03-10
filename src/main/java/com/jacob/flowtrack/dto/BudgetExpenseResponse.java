package com.jacob.flowtrack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class BudgetExpenseResponse {

    private BigDecimal budgetSpent;
    private long totalBudget;

}