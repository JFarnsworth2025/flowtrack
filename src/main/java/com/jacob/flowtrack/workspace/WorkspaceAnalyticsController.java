package com.jacob.flowtrack.workspace;

import com.jacob.flowtrack.common.DashboardResponse;
import com.jacob.flowtrack.expense.ExpenseSummaryResponse;
import com.jacob.flowtrack.expense.service.ExpenseQueryService;
import com.jacob.flowtrack.member.WorkspaceMemberResponse;
import com.jacob.flowtrack.common.ApiResponse;
import com.jacob.flowtrack.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
public class WorkspaceAnalyticsController {

    private final ExpenseQueryService queryService;

    @GetMapping("/{workspaceId}/analytics/summary")
    public ResponseEntity<ApiResponse<ExpenseSummaryResponse>> getWorkspaceSummary(@PathVariable Long workspaceId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ExpenseSummaryResponse summary = queryService.getWorkspaceSummary(workspaceId, customUserDetails.getUser());

        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/{workspaceId}/analytics/categories")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getWorkspaceCategories(@PathVariable Long workspaceId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Map<String, BigDecimal> categories = queryService.getWorkspaceCategoryBreakdown(workspaceId, customUserDetails.getUser());

        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/{workspaceId}/analytics/monthly")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getWorkspaceMonthly(@PathVariable Long workspaceId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Map<String, BigDecimal> monthly = queryService.getWorkspaceMonthlyBreakdown(workspaceId, customUserDetails.getUser());

        return ResponseEntity.ok(ApiResponse.success(monthly));
    }

    @GetMapping("/{workspaceId}/analytics/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getExpenseActivity(@PathVariable Long workspaceId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        DashboardResponse dashboardResponse = queryService.getWorkspaceDashboard(workspaceId, customUserDetails.getUser());

        return ResponseEntity.ok(ApiResponse.success(dashboardResponse));
    }

    @GetMapping("/{workspaceId}/members")
    public ResponseEntity<ApiResponse<List<WorkspaceMemberResponse>>> getWorkspaceMembers(@PathVariable Long workspaceId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        List<WorkspaceMemberResponse> members = queryService.getWorkspaceMembers(workspaceId, customUserDetails.getUser());

        return ResponseEntity.ok(ApiResponse.success(members));
    }

}