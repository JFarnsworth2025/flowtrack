package com.jacob.flowtrack.expense.service;

import com.jacob.flowtrack.member.User;
import com.jacob.flowtrack.member.WorkspaceMember;
import com.jacob.flowtrack.member.WorkspaceMemberRepository;
import com.jacob.flowtrack.workspace.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseAuthorizationService {

    private final WorkspaceMemberRepository workspaceMemberRepository;

    public void verifyWorkspaceMember(User user, Workspace workspace) {

        boolean isMember = workspaceMemberRepository.existsByUserAndWorkspace(user, workspace);
        if(!isMember) {
            throw new RuntimeException("You are not a member of this workspace");
        }

    }

    public WorkspaceMember getMembership(User user, Workspace workspace) {
        return workspaceMemberRepository.findByUserAndWorkspace(user, workspace).orElseThrow(() -> new RuntimeException("Not a workspace member"));
    }

}