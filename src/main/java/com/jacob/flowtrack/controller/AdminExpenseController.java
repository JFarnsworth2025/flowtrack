package com.jacob.flowtrack.controller;

import com.jacob.flowtrack.dto.ExpenseResponse;
import com.jacob.flowtrack.response.ApiResponse;
import com.jacob.flowtrack.security.CustomUserDetails;
import com.jacob.flowtrack.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/expenses")
@RequiredArgsConstructor
public class AdminExpenseController {

    private final ExpenseService expenseService;

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<String>> approveExpense(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        expenseService.approveExpense(id, customUserDetails.getUser());

        return ResponseEntity.ok(ApiResponse.success("Expense Approved", null));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<String>> rejectExpense(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        expenseService.rejectExpense(id, customUserDetails.getUser());

        return ResponseEntity.ok(ApiResponse.success("Expense Denied", null));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getPendingExpenses(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        List<ExpenseResponse> pending = expenseService.getPendingExpenses(customUserDetails.getUser());

        return ResponseEntity.ok(ApiResponse.success("Pending expenses retrieved", pending));
    }

}