package com.jacob.flowtrack.expense.service;

import com.jacob.flowtrack.expense.Expense;
import com.jacob.flowtrack.expense.ExpenseSpecification;
import com.jacob.flowtrack.expense.mapper.ExpenseMapper;
import com.jacob.flowtrack.expense.repository.ExpenseRepository;
import com.jacob.flowtrack.expense.ExpenseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseSearchService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper mapper;

    public Page<ExpenseResponse> search(Specification<Expense> spec, Pageable pageable) {
        Page<Expense> expenses = expenseRepository.findAll(spec, pageable);

        return expenses.map(mapper::toResponse);
    }

    public Page<ExpenseResponse> searchExpenses(Long workspaceId, String keyword, String category, BigDecimal min, BigDecimal max, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {

        var spec = ExpenseSpecification.search(workspaceId, keyword, category, min, max, startDate, endDate);
        Page<Expense> expenses = expenseRepository.findAll(spec, pageable);

        return expenses.map(mapper::toResponse);
    }

}