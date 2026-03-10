package com.jacob.flowtrack.service;

import com.jacob.flowtrack.dto.InviteInfoDTO;
import com.jacob.flowtrack.dto.InviteRequest;
import com.jacob.flowtrack.dto.InviteResponse;
import com.jacob.flowtrack.entity.*;
import com.jacob.flowtrack.repository.UserRepository;
import com.jacob.flowtrack.repository.WorkspaceInviteRepository;
import com.jacob.flowtrack.repository.WorkspaceMemberRepository;
import com.jacob.flowtrack.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceInviteRepository inviteRepository;
    private final WorkspaceMemberRepository memberRepository;
    private final UserRepository userRepository;

    public InviteResponse createInvite(Long workspaceId, InviteRequest request, String username) {

        User inviter = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new RuntimeException("Workspace not found"));
        WorkspaceMember member = memberRepository.findByUserAndWorkspace(inviter, workspace).orElseThrow(() -> new RuntimeException("Not a workspace member"));
        User existingUser = userRepository.findByEmail(request.getEmail()).orElse(null);

        if(member.getRole() == WorkspaceRole.USER) {
            throw new RuntimeException("Not authorized to invite users");
        }

        if(existingUser != null && memberRepository.existsByUserAndWorkspace(existingUser, workspace)) {
            throw new RuntimeException("User is already a workspace member");
        }

        if(inviteRepository.existsByEmailAndWorkspaceAndStatus(request.getEmail(), workspace, InviteStatus.PENDING)) {
            throw new RuntimeException("User already has a pending invite");
        }

        WorkspaceInvite invite = new WorkspaceInvite();

        invite.setEmail(request.getEmail());
        invite.setRole(request.getRole());
        invite.setToken(UUID.randomUUID().toString());
        invite.setStatus(InviteStatus.PENDING);
        invite.setCreatedAt(LocalDateTime.now());
        invite.setExpiresAt(LocalDateTime.now().plusDays(7));
        invite.setWorkspace(workspace);
        invite.setInvitedBy(inviter);
        inviteRepository.save(invite);

        return new InviteResponse(invite.getEmail(), invite.getRole(), invite.getStatus(), invite.getExpiresAt());
    }

    public void acceptInvite(String token, String username) {

        WorkspaceInvite invite = inviteRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid invite"));

        if(invite.getStatus() != InviteStatus.PENDING) {
            throw new RuntimeException("Invite already used");
        }

        if(invite.getExpiresAt().isBefore(LocalDateTime.now())) {
            invite.setStatus(InviteStatus.EXPIRED);
            inviteRepository.save(invite);
            throw new RuntimeException("Invite expired");
        }

        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        WorkspaceMember member = new WorkspaceMember();
        member.setWorkspace(invite.getWorkspace());
        member.setUser(user);
        member.setRole(invite.getRole());
        member.setJoinedAt(LocalDateTime.now());
        memberRepository.save(member);

        invite.setStatus(InviteStatus.ACCEPTED);
        inviteRepository.save(invite);
    }

    public List<InviteInfoDTO> getWorkspaceInvites(Long workspaceId, String username) {

        User requester = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new RuntimeException("Workspace not found"));
        memberRepository.findByUserAndWorkspace(requester, workspace).orElseThrow(() -> new RuntimeException("Not a workspace member"));

        List<WorkspaceInvite> invites = inviteRepository.findByWorkspace(workspace);

        return invites.stream().map(invite -> new InviteInfoDTO(invite.getEmail(), invite.getRole(), invite.getStatus(), invite.getInvitedBy().getUsername(), invite.getExpiresAt())).toList();
    }

    public void cancelinvite(Long workspaceId, Long inviteId, String username) {

        User requester = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new RuntimeException("Workspace not found"));
        WorkspaceMember member = memberRepository.findByUserAndWorkspace(requester, workspace).orElseThrow(() -> new RuntimeException("Not a workspace member"));

        if(member.getRole() == WorkspaceRole.USER) {
            throw new RuntimeException("Not authorized to cancel invites");
        }

        WorkspaceInvite invite = inviteRepository.findById(inviteId).orElseThrow(() -> new RuntimeException("Invite not found"));

        if(!invite.getWorkspace().getId().equals(workspaceId)) {
            throw new RuntimeException("Invite does not belong to this workspace");
        }

        inviteRepository.delete(invite);
    }

}