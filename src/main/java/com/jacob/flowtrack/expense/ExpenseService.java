package com.jacob.flowtrack.expense;

import com.jacob.flowtrack.auth.UserRepository;
import com.jacob.flowtrack.common.DashboardResponse;
import com.jacob.flowtrack.member.WorkspaceMemberResponse;
import com.jacob.flowtrack.member.User;
import com.jacob.flowtrack.security.CustomUserDetails;
import com.jacob.flowtrack.workspace.Workspace;
import com.jacob.flowtrack.member.WorkspaceMember;
import com.jacob.flowtrack.workspace.WorkspaceRole;
import com.jacob.flowtrack.member.WorkspaceMemberRepository;
import com.jacob.flowtrack.workspace.WorkspaceRepository;
import com.jacob.flowtrack.exception.ResourceNotFoundException;
import com.jacob.flowtrack.budget.WorkspaceBudgetService;
import jakarta.persistence.PrePersist;
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
    private final WorkspaceBudgetService budgetService;
    private final UserRepository userRepository;
    private final ExpenseAuthorizationService authorizationService;
    private final ExpenseActivityService activityService;
    private final ExpenseAnalyticsService analyticsService;

    private Workspace getWorkspace(Long id) {
        return workspaceRepository.findById(id).orElseThrow(() -> new RuntimeException("Workspace not found"));
    }

    public ExpenseResponse addExpense(ExpenseRequest request, User user) {

        Workspace workspace = workspaceRepository.findById(request.getWorkspaceId()).orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));
        authorizationService.verifyWorkspaceMember(user, workspace);

        Expense expense = Expense.builder().description(request.getDescription()).amount(request.getAmount()).category(request.getCategory()).createdAt(LocalDateTime.now()).status(ExpenseStatus.PENDING).user(user).submittedBy(user).workspace(workspace).build();
        Expense saved = expenseRepository.save(expense);

        activityService.log(saved, user, "SUBMITTED");

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
                .submittedBy(expense.getSubmittedBy() != null ? expense.getSubmittedBy().getFullName() : null).approvedBy(expense.getApprovedBy() != null ? expense.getApprovedBy().getFullName() : null).build();
    }

    public Page<ExpenseResponse> getWorkspaceExpenses(Long workspaceId, User user, String category, BigDecimal min, BigDecimal max, int page, int size) {

        Workspace workspace = getWorkspace(workspaceId);
        authorizationService.verifyWorkspaceMember(user, workspace);

        Pageable pageable = PageRequest.of(page, size);

        Page<Expense> expenses = expenseRepository.filterWorkspaceExpenses(workspaceId, category, min, max, pageable);

        return expenses.map(this::mapToResponse);
    }

    public Map<String, BigDecimal> getCategoryBreakdown(User user) {

        List<CategorySummary> results = expenseRepository.getCategorySummary(user);

        return results.stream().collect(Collectors.toMap(CategorySummary::getCategory, CategorySummary::getTotal));
    }

    public Map<String, BigDecimal> getMonthlyBreakdown(User user) {

        List<MonthlySummary> results = expenseRepository.getMonthlySummary(user);

        return results.stream().collect(Collectors.toMap(r -> r.getYear() + "-" + String.format("%02d", r.getMonth()), MonthlySummary::getTotal, (a,b) -> a, LinkedHashMap::new));
    }

    public void approveExpense(Long id, String email) {

        User approver = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if(expense.getStatus() != ExpenseStatus.PENDING) {
            throw new RuntimeException("Expense has already been processed");
        }

        Workspace workspace = expense.getWorkspace();
        WorkspaceMember approverMembership = workspaceMemberRepository.findByUserAndWorkspace(approver, workspace).orElseThrow(() -> new RuntimeException("Not a workspace member"));
        WorkspaceMember submitterMembership = workspaceMemberRepository.findByUserAndWorkspace(expense.getUser(), workspace).orElseThrow(() -> new RuntimeException("Submitter not found in workspace"));
        WorkspaceRole approverRole = approverMembership.getRole();
        WorkspaceRole submitterRole = submitterMembership.getRole();

        if (approverRole == WorkspaceRole.USER) {
            throw new RuntimeException("Users cannot approve expenses");
        }

        if (approverRole == WorkspaceRole.ADMIN && submitterRole != WorkspaceRole.USER) {
            throw new RuntimeException("Admins can only approve user expenses");
        }

        expense.setStatus(ExpenseStatus.APPROVED);
        expense.setApprovedBy(approver);
        expenseRepository.save(expense);

        activityService.log(expense, approver, "APPROVED");

        log.info("Expense {} approved by {}", expense.getId(), approver.getFullName());

    }

    public void rejectExpense(Long id, String email) {

        User approver = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if(expense.getStatus() != ExpenseStatus.PENDING) {
            throw new RuntimeException("Expense has already been processed");
        }

        Workspace workspace = expense.getWorkspace();
        WorkspaceMember approverMembership = workspaceMemberRepository.findByUserAndWorkspace(approver, workspace).orElseThrow(() -> new RuntimeException("Not a workspace member"));
        WorkspaceMember submitterMembership = workspaceMemberRepository.findByUserAndWorkspace(expense.getUser(), workspace).orElseThrow(() -> new RuntimeException("Submitter not found in workspace"));
        WorkspaceRole approverRole = approverMembership.getRole();
        WorkspaceRole submitterRole = submitterMembership.getRole();


        if (approverRole == WorkspaceRole.USER) {
            throw new RuntimeException("Users cannot reject expenses");
        }

        if (approverRole == WorkspaceRole.ADMIN && submitterRole != WorkspaceRole.USER) {
            throw new RuntimeException("Admins can only reject user expenses");
        }

        expense.setStatus(ExpenseStatus.REJECTED);
        expense.setApprovedBy(approver);
        expenseRepository.save(expense);

        activityService.log(expense, approver, "REJECTED");

        log.info("Expense {} rejected by {}", expense.getId(), approver.getFullName());
    }

    public ExpenseSummaryResponse getSummary(User user) {
        return analyticsService.getSummary(user);
    }

    public List<ExpenseActivityResponse> getExpenseActivity(Long expenseId, User user) {

        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        Workspace workspace = expense.getWorkspace();
        authorizationService.verifyWorkspaceMember(user, workspace);

        List<ExpenseActivity> activities = activityService.getActivitiesForExpense(expense);

        return activities.stream().map(a -> new ExpenseActivityResponse(a.getAction(), a.getUser().getFullName(), a.getCreatedAt())).toList();

    }

    public DashboardResponse getDashboard(User user) {

        BigDecimal totalSpent = expenseRepository.getTotalSpentByUser(user);
        long totalExpenses = expenseRepository.countByUser(user);

        Map<String, BigDecimal> categoryBreakdown = getCategoryBreakdown(user);
        Map<String, BigDecimal> monthlyBreakdown = getMonthlyBreakdown(user);
        List<ExpenseResponse> recentExpenses = expenseRepository.findTop5ByUserOrderByCreatedAtDesc(user).stream().map(this::mapToResponse).toList();

        return DashboardResponse.builder().totalSpent(totalSpent).totalExpenses(totalExpenses).categoryBreakdown(categoryBreakdown).monthlyBreakdown(monthlyBreakdown).recentExpenses(recentExpenses).build();
    }

    public List<ExpenseResponse> getPendingExpenses(User user) {

        List<Expense> pendingExpenses = expenseRepository.findPendingForUserWorkspaces(user, ExpenseStatus.PENDING);

        return pendingExpenses.stream().map(this::mapToResponse).toList();
    }

    public ExpenseSummaryResponse getWorkspaceSummary(Long workspaceId, User user) {

        Workspace workspace = getWorkspace(workspaceId);
        authorizationService.verifyWorkspaceMember(user, workspace);

        BigDecimal totalSpent = expenseRepository.getTotalSpentByWorkspace(workspaceId);
        long totalExpenses = expenseRepository.countByWorkspace(workspaceId);

        return new ExpenseSummaryResponse(totalSpent, totalExpenses);
    }

    public Map<String, BigDecimal> getWorkspaceCategoryBreakdown(Long workspaceId, User user) {

        Workspace workspace = getWorkspace(workspaceId);
        authorizationService.verifyWorkspaceMember(user, workspace);

        List<CategorySummary> results = expenseRepository.getWorkspaceCategorySummary(workspaceId);

        return results.stream().collect(Collectors.toMap(CategorySummary::getCategory, CategorySummary::getTotal));
    }

    public Map<String, BigDecimal> getWorkspaceMonthlyBreakdown(Long workspaceId, User user) {

        Workspace workspace = getWorkspace(workspaceId);
        authorizationService.verifyWorkspaceMember(user, workspace);

        List<MonthlySummary> results = expenseRepository.getWorkspaceMonthlySummary(workspaceId);

        return results.stream().collect(Collectors.toMap(r -> r.getYear() + "-" + String.format("%02d", r.getMonth()), MonthlySummary::getTotal, (a,b) -> a, LinkedHashMap::new));
    }

    public DashboardResponse getWorkspaceDashboard(Long workspaceId, User user) {

        Workspace workspace = getWorkspace(workspaceId);
        authorizationService.verifyWorkspaceMember(user, workspace);

        BigDecimal totalSpent = expenseRepository.getTotalSpentByWorkspace(workspaceId);
        long totalExpenses = expenseRepository.countByWorkspace(workspaceId);

        Map<String, BigDecimal> categoryBreakdown = getWorkspaceCategoryBreakdown(workspaceId, user);
        Map<String, BigDecimal> monthlyBreakdown = getWorkspaceMonthlyBreakdown(workspaceId, user);
        List<ExpenseResponse> recentExpenses = expenseRepository.findTop5ByWorkspaceOrderByCreatedAtDesc(workspace).stream().map(this::mapToResponse).toList();

        return DashboardResponse.builder().totalSpent(totalSpent).totalExpenses(totalExpenses).categoryBreakdown(categoryBreakdown).monthlyBreakdown(monthlyBreakdown).recentExpenses(recentExpenses).build();
    }

    public List<WorkspaceMemberResponse> getWorkspaceMembers(Long workspaceId, User user) {

        Workspace workspace = getWorkspace(workspaceId);
        authorizationService.verifyWorkspaceMember(user, workspace);

        List<WorkspaceMember> members = workspaceMemberRepository.findByWorkspace(workspace);

        return members.stream().map(m -> new WorkspaceMemberResponse(m.getUser().getFullName(), m.getRole(), m.getJoinedAt())).toList();
    }
}