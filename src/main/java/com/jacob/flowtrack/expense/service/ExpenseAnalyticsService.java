package com.jacob.flowtrack.expense.service;

import com.jacob.flowtrack.expense.repository.ExpenseRepository;
import com.jacob.flowtrack.expense.ExpenseSummaryResponse;
import com.jacob.flowtrack.member.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service @RequiredArgsConstructor
public class ExpenseAnalyticsService {

    private final ExpenseRepository repository;

    public ExpenseSummaryResponse getSummary(User user) {

        BigDecimal totalSpent = repository.getTotalSpentByUser(user);
        long totalExpenses = repository.countByUser(user);

        return new ExpenseSummaryResponse(totalSpent, totalExpenses);
    }

}