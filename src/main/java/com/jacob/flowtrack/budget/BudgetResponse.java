package com.jacob.flowtrack.budget;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class BudgetResponse {

    private String category;
    private BigDecimal monthlyLimit;

}