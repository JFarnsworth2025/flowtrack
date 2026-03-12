package com.jacob.flowtrack.expense.service;

import com.jacob.flowtrack.exception.ResourceNotFoundException;
import com.jacob.flowtrack.expense.Expense;
import com.jacob.flowtrack.expense.ExpenseActivity;
import com.jacob.flowtrack.expense.ExpenseActivityResponse;
import com.jacob.flowtrack.expense.repository.ExpenseActivityRepository;
import com.jacob.flowtrack.expense.repository.ExpenseRepository;
import com.jacob.flowtrack.member.User;
import com.jacob.flowtrack.workspace.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor
public class ExpenseActivityService {

    private final ExpenseActivityRepository activityRepository;
    private final ExpenseAuthorizationService authorizationService;
    private final ExpenseRepository expenseRepository;

    public void log(Expense expense, User user, String action) {
        activityRepository.save(ExpenseActivity.builder().expense(expense).user(user).action(action).createdAt(LocalDateTime.now()).build());
    }

    public List<ExpenseActivity> getActivitiesForExpense(Expense expense) {
        return activityRepository.findByExpenseOrderByCreatedAtAsc(expense);
    }

    public List<ExpenseActivityResponse> getExpenseActivity(Long expenseId, User user) {

        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        Workspace workspace = expense.getWorkspace();
        authorizationService.verifyWorkspaceMember(user, workspace);

        List<ExpenseActivity> activities = getActivitiesForExpense(expense);

        return activities.stream().map(a -> new ExpenseActivityResponse(a.getAction(), a.getUser().getFullName(), a.getCreatedAt())).toList();

    }

}