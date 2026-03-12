package com.jacob.flowtrack.expense.service;

import com.jacob.flowtrack.auth.UserRepository;
import com.jacob.flowtrack.exception.ResourceNotFoundException;
import com.jacob.flowtrack.expense.Expense;
import com.jacob.flowtrack.expense.repository.ExpenseRepository;
import com.jacob.flowtrack.expense.ExpenseStatus;
import com.jacob.flowtrack.member.User;
import com.jacob.flowtrack.member.WorkspaceMember;
import com.jacob.flowtrack.member.WorkspaceMemberRepository;
import com.jacob.flowtrack.workspace.Workspace;
import com.jacob.flowtrack.workspace.WorkspaceRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseApprovalService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ExpenseActivityService activityService;

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

}