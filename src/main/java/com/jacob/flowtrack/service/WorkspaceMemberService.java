package com.jacob.flowtrack.service;

import com.jacob.flowtrack.entity.User;
import com.jacob.flowtrack.entity.Workspace;
import com.jacob.flowtrack.entity.WorkspaceMember;
import com.jacob.flowtrack.entity.WorkspaceRole;
import com.jacob.flowtrack.repository.UserRepository;
import com.jacob.flowtrack.repository.WorkspaceMemberRepository;
import com.jacob.flowtrack.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkspaceMemberService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository memberRepository;
    private final UserRepository userRepository;

    public void updateMemberRole(Long workspaceId, Long userId, WorkspaceRole newRole, String requesterUsername) {

        User requester = userRepository.findByUsername(requesterUsername).orElseThrow(() -> new RuntimeException("User not found"));
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new RuntimeException("Workspace not found"));
        WorkspaceMember requesterMember = memberRepository.findByUserAndWorkspace(requester, workspace).orElseThrow(() -> new RuntimeException("Not a workspace member"));

        if(requesterMember.getRole() == WorkspaceRole.USER) {
            throw new RuntimeException("Not authorized to update roles");
        }

        User targetUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Target user not found"));
        WorkspaceMember targetMember = memberRepository.findByUserAndWorkspace(targetUser, workspace).orElseThrow(() -> new RuntimeException("User is not a workspace member"));

        if(targetMember.getRole() == WorkspaceRole.OWNER) {
            throw new RuntimeException("Cannot modify workspace owner");
        }

        targetMember.setRole(newRole);
        memberRepository.save(targetMember);
    }

    public void removeMember(Long workspaceId, Long userId, String requesterUsername) {

        User requester = userRepository.findByUsername(requesterUsername).orElseThrow(() -> new RuntimeException("User not found"));
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new RuntimeException("Workspace not found"));
        WorkspaceMember requesterMember = memberRepository.findByUserAndWorkspace(requester, workspace).orElseThrow(() -> new RuntimeException("Not a workspace member"));

        if(requesterMember.getRole() == WorkspaceRole.USER) {
            throw new RuntimeException("Not authorized to remove members");
        }

        User targetUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Target user not found"));
        WorkspaceMember targetMember = memberRepository.findByUserAndWorkspace(targetUser, workspace).orElseThrow(() -> new RuntimeException("User is not a workspace member"));

        if(targetMember.getRole() == WorkspaceRole.OWNER) {
            throw new RuntimeException("Cannot remove the workspace owner");
        }

        memberRepository.delete(targetMember);
    }

}