package com.jacob.flowtrack.controller;

import com.jacob.flowtrack.dto.InviteInfoDTO;
import com.jacob.flowtrack.service.InviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
public class WorkspaceInviteController {

    private final InviteService inviteService;

    @GetMapping("/{workspaceId}/invites")
    public List<InviteInfoDTO> getInvites(@PathVariable Long workspaceId, Authentication authentication) {
        return inviteService.getWorkspaceInvites(workspaceId, authentication.getName());
    }

    @DeleteMapping("/{workspaceId}/invites/{inviteId}")
    public ResponseEntity<String> cancelInvite(@PathVariable Long workspaceId, @PathVariable Long inviteId, Authentication authentication) {
        inviteService.cancelinvite(workspaceId, inviteId, authentication.getName());
        return ResponseEntity.ok("Invite canceled");
    }

}