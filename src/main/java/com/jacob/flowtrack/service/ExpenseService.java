package com.jacob.flowtrack.service;

import com.jacob.flowtrack.dto.*;
import com.jacob.flowtrack.entity.*;
import com.jacob.flowtrack.exception.ResourceNotFoundException;
import com.jacob.flowtrack.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ExpenseActivityRepository expenseActivityRepository;
    private final WorkspaceBudgetService budgetService;

    public ExpenseResponse addExpense(ExpenseRequest request, User user) {

        Workspace workspace = workspaceRepository.findById(request.getWorkspaceId()).orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));
        boolean isMember = workspaceMemberRepository.existsByUserAndWorkspace(user, workspace);

        if(!isMember) {
            throw new RuntimeException("You are not a member of this workspace.");
        }

        Expense expense = Expense.builder().description(request.getDescription()).amount(request.getAmount()).category(request.getCategory()).createdAt(LocalDateTime.now()).status(ExpenseStatus.PENDING).user(user).submittedBy(user).workspace(workspace).build();
        BigDecimal remainingBudget = budgetService.checkBudgetRemaining(workspace, request.getCategory());

        if(remainingBudget != null && remainingBudget.compareTo(request.getAmount()) < 0) {
            System.out.println("WARNING: Budget exceeded for category " + request.getCategory());
        }

        Expense saved = expenseRepository.save(expense);

        expenseActivityRepository.save(ExpenseActivity.builder().expense(saved).user(user).action("SUBMITTED").createdAt(LocalDateTime.now()).build());

        log.info("User {} submitted expense {} in workspace {}", user.getUsername(), saved.getId(), workspace.getId());

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
        return ExpenseResponse.builder().id(expense.getId()).description(expense.getDescription()).amount(expense.getAmount()).category(expense.getCategory()).createdAt(expense.getCreatedAt()).status(expense.getStatus())
                .submittedBy(expense.getSubmittedBy() != null ? expense.getSubmittedBy().getUsername() : null).approvedBy(expense.getApprovedBy() != null ? expense.getApprovedBy().getUsername() : null).build();
    }

    public Page<ExpenseResponse> getWorkspaceExpenses(Long workspaceId, User user, String category, BigDecimal min, BigDecimal max, int page, int size) {

        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));

        boolean isMember = workspaceMemberRepository.existsByUserAndWorkspace(user, workspace);

        if(!isMember) {
             throw new RuntimeException("You are not a member of this workspace");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Expense> expenses = expenseRepository.filterWorkspaceExpenses(workspaceId, category, min, max, pageable);

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

    public void approveExpense(Long id, User approver) {

        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if(expense.getStatus() != ExpenseStatus.PENDING) {
            throw new RuntimeException("Expense has already been processed");
        }

        Workspace workspace = expense.getWorkspace();
        WorkspaceMember approverMembership = workspaceMemberRepository.findByUserAndWorkspace(approver, workspace).orElseThrow(() -> new RuntimeException("Not a workspace member"));
        WorkspaceMember submitterMembership = workspaceMemberRepository.findByUserAndWorkspace(expense.getUser(), workspace).orElseThrow(() -> new RuntimeException("Submitter not found in workspace"));
        WorkspaceRole approverRole = approverMembership.getRole();
        WorkspaceRole submitterRole = submitterMembership.getRole();

        if(approverRole == WorkspaceRole.OWNER) {

        } else if(approverRole == WorkspaceRole.ADMIN) {
            if(submitterRole != WorkspaceRole.USER) {
                throw new RuntimeException("Admins can only approve user expenses");
            }
        }

        else {
            throw new RuntimeException("Users cannot approve expenses");
        }

        expense.setStatus(ExpenseStatus.APPROVED);
        expense.setApprovedBy(approver);
        expenseRepository.save(expense);

        log.info("Expense {} approved by {}", expense.getId(), approver.getUsername());

        expenseActivityRepository.save(ExpenseActivity.builder().expense(expense).user(approver).action("APPROVED").createdAt(LocalDateTime.now()).build());

    }

    public void rejectExpense(Long id, User approver) {

        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if(expense.getStatus() != ExpenseStatus.PENDING) {
            throw new RuntimeException("Expense has already been processed");
        }

        Workspace workspace = expense.getWorkspace();
        WorkspaceMember approverMembership = workspaceMemberRepository.findByUserAndWorkspace(approver, workspace).orElseThrow(() -> new RuntimeException("Not a workspace member"));
        WorkspaceMember submitterMembership = workspaceMemberRepository.findByUserAndWorkspace(expense.getUser(), workspace).orElseThrow(() -> new RuntimeException("Submitter not found in workspace"));
        WorkspaceRole approverRole = approverMembership.getRole();
        WorkspaceRole submitterRole = submitterMembership.getRole();


        if(approverRole == WorkspaceRole.OWNER) {

        } else if(approverRole == WorkspaceRole.ADMIN) {
            if(submitterRole != WorkspaceRole.USER) {
                throw new RuntimeException("Admins can only reject user expenses");
            }
        }

        else {
            throw new RuntimeException("Users cannot reject expenses");
        }

        expense.setStatus(ExpenseStatus.REJECTED);
        expense.setApprovedBy(approver);
        expenseRepository.save(expense);

        log.info("Expense {} rejected by {}", expense.getId(), approver.getUsername());

        expenseActivityRepository.save(ExpenseActivity.builder().expense(expense).user(approver).action("REJECTED").createdAt(LocalDateTime.now()).build());
    }

    public List<ExpenseActivityResponse> getExpenseActivity(Long expenseId, User user) {

        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        Workspace workspace = expense.getWorkspace();

        boolean isMember = workspaceMemberRepository.existsByUserAndWorkspace(user, workspace);

        if(!isMember) {
            throw new RuntimeException("Unauthorized access");
        }

        List<ExpenseActivity> activities = expenseActivityRepository.findByExpenseOrderByCreatedAtAsc(expense);

        return activities.stream().map(a -> new ExpenseActivityResponse(a.getAction(), a.getUser().getUsername(), a.getCreatedAt())).toList();

    }

    public DashboardResponse getDashboard(User user) {

        BigDecimal totalSpent = expenseRepository.getTotalSpentByUser(user);
        long totalExpenses = expenseRepository.countByUser(user);

        Map<String, BigDecimal> categoryBreakdown = getCategoryBreakdown(user.getId());
        Map<String, BigDecimal> monthlyBreakdown = getMonthlyBreakdown(user.getId());
        List<ExpenseResponse> recentExpenses = expenseRepository.findTop5ByUserOrderByCreatedAtDesc(user).stream().map(this::mapToResponse).toList();

        return DashboardResponse.builder().totalSpent(totalSpent).totalExpenses(totalExpenses).categoryBreakdown(categoryBreakdown).monthlyBreakdown(monthlyBreakdown).recentExpenses(recentExpenses).build();
    }

    public List<ExpenseResponse> getPendingExpenses(User user) {

        List<WorkspaceMember> memberships = workspaceMemberRepository.findByUser(user);
        List<Workspace> workspaces = memberships.stream().map(WorkspaceMember::getWorkspace).toList();
        List<Expense> pendingExpenses = expenseRepository.findByStatus(ExpenseStatus.PENDING);

        return pendingExpenses.stream().filter(expense -> workspaces.contains(expense.getWorkspace())).map(this::mapToResponse).toList();
    }

    public ExpenseSummaryResponse getWorkspaceSummary(Long workspaceId, User user) {

        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));
        boolean isMember = workspaceMemberRepository.existsByUserAndWorkspace(user, workspace);

        if(!isMember) {
            throw new RuntimeException("Unauthorized workspace access");
        }

        BigDecimal totalSpent = expenseRepository.getTotalSpentByWorkspace(workspaceId);
        long totalExpenses = expenseRepository.countByWorkspace(workspaceId);

        return new ExpenseSummaryResponse(totalSpent, totalExpenses);
    }

    public Map<String, BigDecimal> getWorkspaceCategoryBreakdown(Long workspaceId, User user) {

        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));
        boolean isMember = workspaceMemberRepository.existsByUserAndWorkspace(user, workspace);

        if(!isMember) {
            throw new RuntimeException("Unauthorized workspace access");
        }

        List<CategorySummary> results = expenseRepository.getWorkspaceCategorySummary(workspaceId);

        return results.stream().collect(Collectors.toMap(CategorySummary::getCategory, CategorySummary::getTotal));
    }

    public Map<String, BigDecimal> getWorkspaceMonthlyBreakdown(Long workspaceId, User user) {

        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));
        boolean isMember = workspaceMemberRepository.existsByUserAndWorkspace(user, workspace);

        if(!isMember) {
            throw new RuntimeException("Unauthorized workspace access");
        }

        List<MonthlySummary> results = expenseRepository.getWorkspaceMonthlySummary(workspaceId);

        return results.stream().collect(Collectors.toMap(r -> r.getYear() + "-" + String.format("%02d", r.getMonth()), MonthlySummary::getTotal, (a,b) -> a, LinkedHashMap::new));
    }

    public DashboardResponse getWorkspaceDashboard(Long workspaceId, User user) {

        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));
        boolean isMember = workspaceMemberRepository.existsByUserAndWorkspace(user, workspace);

        if(!isMember) {
            throw new RuntimeException("Unauthorized workspace access");
        }

        BigDecimal totalSpent = expenseRepository.getTotalSpentByWorkspace(workspaceId);
        long totalExpenses = expenseRepository.countByWorkspace(workspaceId);

        Map<String, BigDecimal> categoryBreakdown = getWorkspaceCategoryBreakdown(workspaceId, user);
        Map<String, BigDecimal> monthlyBreakdown = getWorkspaceMonthlyBreakdown(workspaceId, user);
        List<ExpenseResponse> recentExpenses = expenseRepository.findTop5ByWorkspaceOrderByCreatedAtDesc(workspace).stream().map(this::mapToResponse).toList();

        return DashboardResponse.builder().totalSpent(totalSpent).totalExpenses(totalExpenses).categoryBreakdown(categoryBreakdown).monthlyBreakdown(monthlyBreakdown).recentExpenses(recentExpenses).build();
    }

    public List<WorkspaceMemberResponse> getWorkspaceMembers(Long workspaceId, User user) {

        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));
        boolean isMember = workspaceMemberRepository.existsByUserAndWorkspace(user, workspace);

        if(!isMember) {
            throw new RuntimeException("Unauthorized workspace access");
        }

        List<WorkspaceMember> members = workspaceMemberRepository.findByWorkspace(workspace);

        return members.stream().map(m -> new WorkspaceMemberResponse(m.getUser().getUsername(), m.getRole(), m.getJoinedAt())).toList();
    }



}