package com.jacob.flowtrack.member;

import com.jacob.flowtrack.auth.UserRepository;
import com.jacob.flowtrack.workspace.Workspace;
import com.jacob.flowtrack.workspace.WorkspaceRepository;
import com.jacob.flowtrack.workspace.WorkspaceRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkspaceMemberService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository memberRepository;
    private final UserRepository userRepository;

    public void updateMemberRole(Long workspaceId, Long userId, WorkspaceRole newRole, String email) {

        User requester = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
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

    public void removeMember(Long workspaceId, Long userId, String email) {

        User requester = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
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