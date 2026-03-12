package com.jacob.flowtrack.budget;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class CreateBudgetRequest {

    private String category;
    private BigDecimal monthlyLimit;

}