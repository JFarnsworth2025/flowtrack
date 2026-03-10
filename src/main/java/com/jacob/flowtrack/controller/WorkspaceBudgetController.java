package com.jacob.flowtrack.controller;

import com.jacob.flowtrack.dto.CreateBudgetRequest;
import com.jacob.flowtrack.entity.WorkspaceBudget;
import com.jacob.flowtrack.service.WorkspaceBudgetService;
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
    public ResponseEntity<String> createBudget(@PathVariable Long workspaceId, @RequestBody CreateBudgetRequest request, Authentication authentication) {

        budgetService.createBudget(workspaceId, request, authentication.getName());

        return ResponseEntity.ok("Budget created");
    }

    @GetMapping("/{workspaceId}/budgets")
    public List<WorkspaceBudget> getBudgets(@PathVariable Long workspaceId, Authentication authentication) {

        return budgetService.getBudgets(workspaceId, authentication.getName());
    }

}