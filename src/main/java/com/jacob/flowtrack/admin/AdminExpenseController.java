package com.jacob.flowtrack.admin;

import com.jacob.flowtrack.expense.ExpenseResponse;
import com.jacob.flowtrack.common.ApiResponse;
import com.jacob.flowtrack.expense.service.ExpenseApprovalService;
import com.jacob.flowtrack.expense.service.ExpenseQueryService;
import com.jacob.flowtrack.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/expenses")
@RequiredArgsConstructor
public class AdminExpenseController {

    private final ExpenseQueryService queryService;
    private final ExpenseApprovalService approvalService;

    @PostMapping("/{id}/approve")
    public ApiResponse<String> approveExpense(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        approvalService.approveExpense(id, customUserDetails.getUser().getFullName());

        return ApiResponse.success("Expense Approved");
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<String> rejectExpense(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        approvalService.rejectExpense(id, customUserDetails.getUser().getFullName());

        return ApiResponse.success("Expense Denied");
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getPendingExpenses(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        List<ExpenseResponse> pending = queryService.getPendingExpenses(customUserDetails.getUser());

        return ResponseEntity.ok(ApiResponse.success(pending));
    }

}