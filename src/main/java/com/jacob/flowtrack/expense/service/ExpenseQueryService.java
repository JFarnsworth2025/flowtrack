package com.jacob.flowtrack.expense.service;

import com.jacob.flowtrack.common.DashboardResponse;
import com.jacob.flowtrack.expense.*;
import com.jacob.flowtrack.expense.mapper.ExpenseMapper;
import com.jacob.flowtrack.expense.repository.ExpenseRepository;
import com.jacob.flowtrack.member.User;
import com.jacob.flowtrack.member.WorkspaceMember;
import com.jacob.flowtrack.member.WorkspaceMemberRepository;
import com.jacob.flowtrack.member.WorkspaceMemberResponse;
import com.jacob.flowtrack.workspace.Workspace;
import com.jacob.flowtrack.workspace.WorkspaceRepository;
import com.jacob.flowtrack.workspace.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseQueryService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseAnalyticsService analyticsService;
    private final ExpenseAuthorizationService authorizationService;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ExpenseMapper mapper;
    private final WorkspaceService workspaceService;

    private Workspace getWorkspace(Long id) {
        return workspaceRepository.findById(id).orElseThrow(() -> new RuntimeException("Workspace not found"));
    }

    public DashboardResponse getDashboard(User user) {

        BigDecimal totalSpent = expenseRepository.getTotalSpentByUser(user);
        long totalExpenses = expenseRepository.countByUser(user);

        Map<String, BigDecimal> categoryBreakdown = getCategoryBreakdown(user);
        Map<String, BigDecimal> monthlyBreakdown = getMonthlyBreakdown(user);
        List<ExpenseResponse> recentExpenses = expenseRepository.findTop5ByUserOrderByCreatedAtDesc(user).stream().map(mapper::toResponse).toList();

        return DashboardResponse.builder().totalSpent(totalSpent).totalExpenses(totalExpenses).categoryBreakdown(categoryBreakdown).monthlyBreakdown(monthlyBreakdown).recentExpenses(recentExpenses).build();
    }

    public ExpenseSummaryResponse getSummary(User user) {
        return analyticsService.getSummary(user);
    }

    public ExpenseSummaryResponse getWorkspaceSummary(Long workspaceId, User user) {

        Workspace workspace = getWorkspace(workspaceId);
        authorizationService.verifyWorkspaceMember(user, workspace);

        BigDecimal totalSpent = expenseRepository.getTotalSpentByWorkspace(workspaceId);
        long totalExpenses = expenseRepository.countByWorkspace(workspaceId);

        return new ExpenseSummaryResponse(totalSpent, totalExpenses);
    }

    public DashboardResponse getWorkspaceDashboard(Long workspaceId, User user) {

        Workspace workspace = getWorkspace(workspaceId);
        authorizationService.verifyWorkspaceMember(user, workspace);

        BigDecimal totalSpent = expenseRepository.getTotalSpentByWorkspace(workspaceId);
        long totalExpenses = expenseRepository.countByWorkspace(workspaceId);

        Map<String, BigDecimal> categoryBreakdown = getWorkspaceCategoryBreakdown(workspaceId, user);
        Map<String, BigDecimal> monthlyBreakdown = getWorkspaceMonthlyBreakdown(workspaceId, user);
        List<ExpenseResponse> recentExpenses = expenseRepository.findTop5ByWorkspaceOrderByCreatedAtDesc(workspace).stream().map(mapper::toResponse).toList();

        return DashboardResponse.builder().totalSpent(totalSpent).totalExpenses(totalExpenses).categoryBreakdown(categoryBreakdown).monthlyBreakdown(monthlyBreakdown).recentExpenses(recentExpenses).build();
    }

    public List<WorkspaceMemberResponse> getWorkspaceMembers(Long workspaceId, User user) {

        Workspace workspace = workspaceService.getWorkspace(workspaceId);
        authorizationService.verifyWorkspaceMember(user, workspace);

        List<WorkspaceMember> members = workspaceMemberRepository.findByWorkspace(workspace);

        return members.stream().map(m -> new WorkspaceMemberResponse(m.getUser().getFullName(), m.getRole(), m.getJoinedAt())).toList();
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

    public Page<ExpenseResponse> getWorkspaceExpenses(Long workspaceId, User user, String category, BigDecimal min, BigDecimal max, int page, int size) {

        Workspace workspace = getWorkspace(workspaceId);
        authorizationService.verifyWorkspaceMember(user, workspace);

        Pageable pageable = PageRequest.of(page, size);

        Page<Expense> expenses = expenseRepository.filterWorkspaceExpenses(workspaceId, category, min, max, pageable);

        return expenses.map(mapper::toResponse);
    }

    public Map<String, BigDecimal> getCategoryBreakdown(User user) {

        List<CategorySummary> results = expenseRepository.getCategorySummary(user);

        return results.stream().collect(Collectors.toMap(CategorySummary::getCategory, CategorySummary::getTotal));
    }

    public Map<String, BigDecimal> getMonthlyBreakdown(User user) {

        List<MonthlySummary> results = expenseRepository.getMonthlySummary(user);

        return results.stream().collect(Collectors.toMap(r -> r.getYear() + "-" + String.format("%02d", r.getMonth()), MonthlySummary::getTotal, (a,b) -> a, LinkedHashMap::new));
    }

    public List<ExpenseResponse> getPendingExpenses(User user) {

        List<Expense> pendingExpenses = expenseRepository.findPendingForUserWorkspaces(user, ExpenseStatus.PENDING);

        return pendingExpenses.stream().map(mapper::toResponse).toList();
    }

}