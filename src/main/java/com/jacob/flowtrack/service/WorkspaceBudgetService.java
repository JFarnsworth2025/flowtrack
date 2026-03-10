package com.jacob.flowtrack.service;

import com.jacob.flowtrack.dto.BudgetExpenseResponse;
import com.jacob.flowtrack.dto.CreateBudgetRequest;
import com.jacob.flowtrack.entity.*;
import com.jacob.flowtrack.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceBudgetService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository memberRepository;
    private final WorkspaceBudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    public void createBudget(Long workspaceId, CreateBudgetRequest request, String username) {

        User requester = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new RuntimeException("Workspace not found"));
        WorkspaceMember member = memberRepository.findByUserAndWorkspace(requester, workspace).orElseThrow(() -> new RuntimeException("Not a workspace member"));

        if(member.getRole() == WorkspaceRole.USER) {
            throw new RuntimeException("Not authorized to create budgets");
        }

        WorkspaceBudget existingBudget = budgetRepository.findByWorkspaceAndCategory(workspace, request.getCategory()).orElse(null);

        if(existingBudget != null) {
            throw new RuntimeException("Budget for this category already exists");
        }

        WorkspaceBudget budget = new WorkspaceBudget();
        budget.setWorkspace(workspace);
        budget.setCategory(request.getCategory());
        budget.setMonthlyLimit(request.getMonthlyLimit());

        budgetRepository.save(budget);
    }

    public List<WorkspaceBudget> getBudgets(Long workspaceId, String username) {

        User requester = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new RuntimeException("Workspace not found"));
        WorkspaceMember member = memberRepository.findByUserAndWorkspace(requester, workspace).orElseThrow(() -> new RuntimeException("Not a workspace member"));

        return budgetRepository.findByWorkspace(workspace);
    }

    public BigDecimal checkBudgetRemaining(Workspace workspace, String category) {

        WorkspaceBudget budget = budgetRepository.findByWorkspaceAndCategory(workspace, category).orElse(null);

        if(budget == null) { return null; }

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        BigDecimal spent = expenseRepository.sumCategorySpendingForMonth(workspace, category, startOfMonth);

        return budget.getMonthlyLimit().subtract(spent);
    }

    public BigDecimal getRemainingBudget(Workspace workspace, String category) {

        WorkspaceBudget budget = budgetRepository.findByWorkspaceAndCategory(workspace, category).orElse(null);
        if(budget == null) { return null; }

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        BigDecimal spent = expenseRepository.sumCategorySpendingForMonth(workspace, category, startOfMonth);

        return budget.getMonthlyLimit().subtract(spent);
    }

}