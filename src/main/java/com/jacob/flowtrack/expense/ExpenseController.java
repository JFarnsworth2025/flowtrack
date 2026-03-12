package com.jacob.flowtrack.expense;

import com.jacob.flowtrack.common.PaginatedResponse;
import com.jacob.flowtrack.expense.service.*;
import com.jacob.flowtrack.security.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;
import com.jacob.flowtrack.member.User;
import com.jacob.flowtrack.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseQueryService queryService;
    private final ExpenseCommandService commandService;
    private final ExpenseActivityService activityService;
    private final ExpenseSearchService searchService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> addExpense(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody ExpenseRequest request) {

        User user = customUserDetails.getUser();

        ExpenseResponse response = commandService.addExpense(request, user);

        return ResponseEntity.ok(ApiResponse.success(response));

    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<ExpenseResponse>>> getExpenses(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam Long workspaceId, @RequestParam(required = false) String category, @RequestParam(required = false) BigDecimal min, @RequestParam(required = false) BigDecimal max, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        User user = customUserDetails.getUser();

        Page<ExpenseResponse> expenses = queryService.getWorkspaceExpenses(workspaceId, user, category, min, max, page, size);

        PaginatedResponse<ExpenseResponse> response = PaginatedResponse.from(expenses);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<ExpenseSummaryResponse>> getSummary(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();

        ExpenseSummaryResponse summary = queryService.getSummary(user);

        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {

        User user = customUserDetails.getUser();

        ExpenseResponse response = commandService.updateExpense(id, request, user);

        return ResponseEntity.ok(ApiResponse.success(response));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteExpense(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long id) {

        User user = customUserDetails.getUser();
        commandService.deleteExpense(id, user);

        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully"));
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> addExpenses(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody List<ExpenseRequest> requests) {

        User user = customUserDetails.getUser();

        List<ExpenseResponse> responses = requests.stream().map(request -> commandService.addExpense(request, user)).toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/summary/categories")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getCategorySummary(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();

        Map<String, BigDecimal> data = queryService.getCategoryBreakdown(user);

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/summary/monthly")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getMonthlySummary(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();

        Map<String, BigDecimal> data = queryService.getMonthlyBreakdown(user);

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{id}/activity")
    public ResponseEntity<ApiResponse<List<ExpenseActivityResponse>>> getExpenseActivity(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long id, User user) {

        List<ExpenseActivityResponse> activity = activityService.getExpenseActivity(id, user);

        return ResponseEntity.ok(ApiResponse.success(activity));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PaginatedResponse<ExpenseResponse>>> searchExpenses(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                          @RequestParam(required = false) Long workspaceId,
                                                                                          @RequestParam(required = false) String keyword,
                                                                                          @RequestParam(required = false) String category,
                                                                                          @RequestParam(required = false) BigDecimal min,
                                                                                          @RequestParam(required = false) BigDecimal max,
                                                                                          @RequestParam(required = false) LocalDateTime startDate,
                                                                                          @RequestParam(required = false) LocalDateTime endDate,
                                                                                          @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
                                                                                          ) {

        User user = userDetails.getUser();
        Page<ExpenseResponse> results = searchService.searchExpenses(workspaceId, keyword, category, min, max, startDate, endDate, pageable);
        PaginatedResponse<ExpenseResponse> response = PaginatedResponse.from(results);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}