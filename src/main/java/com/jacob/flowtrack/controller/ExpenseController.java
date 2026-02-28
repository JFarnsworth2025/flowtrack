package com.jacob.flowtrack.controller;

import com.jacob.flowtrack.response.PaginatedResponse;
import com.jacob.flowtrack.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;
import com.jacob.flowtrack.dto.ExpenseRequest;
import com.jacob.flowtrack.dto.ExpenseResponse;
import com.jacob.flowtrack.dto.ExpenseSummaryResponse;
import com.jacob.flowtrack.entity.User;
import com.jacob.flowtrack.response.ApiResponse;
import com.jacob.flowtrack.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> addExpense(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody ExpenseRequest request) {

        User user = customUserDetails.getUser();

        ExpenseResponse response = expenseService.addExpense(request, user);

        return ResponseEntity.ok(ApiResponse.success("Expense added successfully", response));

    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<ExpenseResponse>>> getExpenses(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam(required = false) String category, @RequestParam(required = false) BigDecimal min, @RequestParam(required = false) BigDecimal max, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        User user = customUserDetails.getUser();

        Page<ExpenseResponse> expenses = expenseService.getUserExpenses(user, category, min, max, page, size);

        PaginatedResponse<ExpenseResponse> response = PaginatedResponse.from(expenses);

        return ResponseEntity.ok(ApiResponse.success("Expenses retrieved successfully", response));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<ExpenseSummaryResponse>> getSummary(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();

        ExpenseSummaryResponse summary = expenseService.getSummary(user);

        return ResponseEntity.ok(ApiResponse.success("Summary retrieved successfully", summary));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {

        User user = customUserDetails.getUser();

        ExpenseResponse response = expenseService.updateExpense(id, request, user);

        return ResponseEntity.ok(ApiResponse.success("Expense updated successfully", response));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long id) {

        User user = customUserDetails.getUser();
        expenseService.deleteExpense(id, user);

        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully", null));
    }

    @PostMapping("/bulk")
    public List<ExpenseResponse> addExpenses(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody List<ExpenseRequest> requests) {

        User user = customUserDetails.getUser();

        return requests.stream().map(request -> expenseService.addExpense(request, user)).toList();
    }

    @GetMapping("/summary/categories")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getCategorySummary(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();

        Map<String, BigDecimal> data = expenseService.getCategoryBreakdown(user.getId());

        return ResponseEntity.ok(ApiResponse.success("Category summary retrieved", data));
    }

    @GetMapping("/summary/monthly")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getMonthlySummary(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();

        Map<String, BigDecimal> data = expenseService.getMonthlyBreakdown(user.getId());

        return ResponseEntity.ok(ApiResponse.success("Monthly summary retrieved", data));
    }

}