package com.jacob.flowtrack.expense.mapper;

import com.jacob.flowtrack.expense.Expense;
import com.jacob.flowtrack.expense.ExpenseResponse;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {

    public ExpenseResponse toResponse(Expense expense) {
        return ExpenseResponse.builder().id(expense.getId()).description(expense.getDescription()).amount(expense.getAmount()).category(expense.getCategory()).createdAt(expense.getCreatedAt()).status(expense.getStatus()).submittedBy(expense.getSubmittedBy() != null ? expense.getSubmittedBy().getFullName() : null).approvedBy(expense.getApprovedBy() != null ? expense.getApprovedBy().getFullName() : null).build();
    }

}