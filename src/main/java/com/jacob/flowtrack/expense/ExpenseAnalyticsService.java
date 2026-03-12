package com.jacob.flowtrack.expense;

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