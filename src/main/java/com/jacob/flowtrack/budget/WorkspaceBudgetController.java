package com.jacob.flowtrack.budget;

import com.jacob.flowtrack.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
public class WorkspaceBudgetController {

    private final WorkspaceBudgetService budgetService;

    @PostMapping("/{workspaceId}/budgets")
    public ResponseEntity<ApiResponse<String>> createBudget(@PathVariable Long workspaceId, @RequestBody CreateBudgetRequest request, Authentication authentication) {

        budgetService.createBudget(workspaceId, request, authentication.getName());

        return ResponseEntity.ok(ApiResponse.success("Budget created"));
    }

    @GetMapping("/{workspaceId}/budgets")
    public List<WorkspaceBudget> getBudgets(@PathVariable Long workspaceId, Authentication authentication) {

        return budgetService.getBudgets(workspaceId, authentication.getName());
    }

    @GetMapping("/{workspaceId}/budgets/status")
    public ResponseEntity<ApiResponse<List<BudgetStatusResponse>>> getBudgetStatus(@PathVariable Long workspaceId, Authentication authentication) {
        List<BudgetStatusResponse> response = budgetService.getBudgetStatus(workspaceId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}