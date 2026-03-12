package com.jacob.flowtrack.expense;

import com.jacob.flowtrack.common.DashboardResponse;
import com.jacob.flowtrack.common.ApiResponse;
import com.jacob.flowtrack.expense.service.ExpenseQueryService;
import com.jacob.flowtrack.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseAnalyticsController {

    private final ExpenseQueryService queryService;

    @GetMapping("/analytics/summary")
    public ResponseEntity<ApiResponse<ExpenseSummaryResponse>> getOverallSummary(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ExpenseSummaryResponse summary = queryService.getSummary(customUserDetails.getUser());

        return ResponseEntity.ok(ApiResponse.success("Summary retrieved", summary));
    }

    @GetMapping("/analytics/categories")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getCategorySummary(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Map<String, BigDecimal> categories = queryService.getCategoryBreakdown(customUserDetails.getUser());

        return ResponseEntity.ok(ApiResponse.success("Category breakdown retrieved", categories));
    }

    @GetMapping("/analytics/monthly")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getMonthlySummary(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Map<String, BigDecimal> monthly = queryService.getMonthlyBreakdown(customUserDetails.getUser());

        return ResponseEntity.ok(ApiResponse.success("Monthly breakdown retrieved", monthly));
    }

    @GetMapping("/analytics/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        DashboardResponse dashboard = queryService.getDashboard(customUserDetails.getUser());

        return ResponseEntity.ok(ApiResponse.success("Dashboard Retrieved", dashboard));
    }


}