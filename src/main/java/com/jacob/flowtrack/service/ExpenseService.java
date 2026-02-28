package com.jacob.flowtrack.service;

import com.jacob.flowtrack.dto.ExpenseRequest;
import com.jacob.flowtrack.dto.ExpenseResponse;
import com.jacob.flowtrack.dto.ExpenseSummaryResponse;
import com.jacob.flowtrack.entity.Expense;
import com.jacob.flowtrack.entity.User;
import com.jacob.flowtrack.exception.ResourceNotFoundException;
import com.jacob.flowtrack.repository.CategorySummary;
import com.jacob.flowtrack.repository.ExpenseRepository;
import com.jacob.flowtrack.repository.MonthlySummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseResponse addExpense(ExpenseRequest request, User user) {

        Expense expense = Expense.builder().description(request.getDescription()).amount(request.getAmount()).category(request.getCategory()).createdAt(LocalDateTime.now()).user(user).build();
        Expense saved = expenseRepository.save(expense);

        return mapToResponse(saved);

    }

    public ExpenseResponse updateExpense(Long id, ExpenseRequest request, User user) {

        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if(!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to expense");
        }

        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());

        Expense updated = expenseRepository.save(expense);

        return mapToResponse(updated);
    }

    public void deleteExpense(Long id, User user) {

        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));

        if(!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to expense");
        }

        expenseRepository.delete(expense);
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        return ExpenseResponse.builder().id(expense.getId()).description(expense.getDescription()).amount(expense.getAmount()).category(expense.getCategory()).createdAt(expense.getCreatedAt()).build();
    }

    public Page<ExpenseResponse> getUserExpenses(User user, String category, BigDecimal min, BigDecimal max, int page, int size) {

        if(category != null) {
            category = category.toLowerCase();
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Expense> expenses = expenseRepository.filterExpenses(user, category, min, max, pageable);

        return expenses.map(this::mapToResponse);
    }

    public ExpenseSummaryResponse getSummary(User user) {

        BigDecimal totalSpent = expenseRepository.getTotalSpentByUser(user);
        long totalExpenses = expenseRepository.countByUser(user);

        return new ExpenseSummaryResponse(totalSpent, totalExpenses);
    }

    public Map<String, BigDecimal> getCategoryBreakdown(Long userId) {

        List<CategorySummary> results = expenseRepository.getCategorySummary(userId);

        return results.stream().collect(Collectors.toMap(CategorySummary::getCategory, CategorySummary::getTotal));
    }

    public Map<String, BigDecimal> getMonthlyBreakdown(Long userId) {

        List<MonthlySummary> results = expenseRepository.getMonthlySummary(userId);

        return results.stream().collect(Collectors.toMap(r -> r.getYear() + "-" + String.format("%02d", r.getMonth()), MonthlySummary::getTotal, (a,b) -> a, LinkedHashMap::new));
    }

}