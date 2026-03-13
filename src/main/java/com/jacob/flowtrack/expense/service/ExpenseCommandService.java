package com.jacob.flowtrack.expense.service;

import com.jacob.flowtrack.exception.ResourceNotFoundException;
import com.jacob.flowtrack.expense.*;
import com.jacob.flowtrack.expense.mapper.ExpenseMapper;
import com.jacob.flowtrack.expense.repository.ExpenseRepository;
import com.jacob.flowtrack.member.User;
import com.jacob.flowtrack.workspace.Workspace;
import com.jacob.flowtrack.workspace.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExpenseCommandService {

    private final ExpenseRepository expenseRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ExpenseAuthorizationService authorizationService;
    private final ExpenseActivityService activityService;
    private final ExpenseMapper mapper;

    public ExpenseResponse addExpense(ExpenseRequest request, User user, MultipartFile receipt) {

        Workspace workspace = workspaceRepository.findById(request.getWorkspaceId()).orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));
        

        Expense expense = Expense.builder().description(request.getDescription()).amount(request.getAmount()).category(request.getCategory()).createdAt(LocalDateTime.now()).status(ExpenseStatus.PENDING).user(user).submittedBy(user).workspace(workspace).build();
        Expense saved = expenseRepository.save(expense);

        activityService.log(saved, user, "SUBMITTED");

        return mapper.toResponse(saved);
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

        return mapper.toResponse(updated);
    }

    public void deleteExpense(Long id, User user) {

        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));

        if(!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to expense");
        }

        expenseRepository.delete(expense);
    }

}