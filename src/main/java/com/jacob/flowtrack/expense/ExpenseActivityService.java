package com.jacob.flowtrack.expense;

import com.jacob.flowtrack.member.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor
public class ExpenseActivityService {

    private final ExpenseActivityRepository repository;

    public void log(Expense expense, User user, String action) {
        repository.save(ExpenseActivity.builder().expense(expense).user(user).action(action).createdAt(LocalDateTime.now()).build());
    }

    public List<ExpenseActivity> getActivitiesForExpense(Expense expense) {
        return repository.findByExpenseOrderByCreatedAtAsc(expense);
    }

}