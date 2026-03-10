package com.jacob.flowtrack.controller;

import com.jacob.flowtrack.dto.UpdateMemberRoleRequest;
import com.jacob.flowtrack.service.WorkspaceMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
public class WorkspaceMemberController {

    private final WorkspaceMemberService memberService;

    @PutMapping("/{workspaceId}/members/{userId}/role")
    public ResponseEntity<String> updateMemberRole(@PathVariable Long workspaceId, @PathVariable Long userId, @RequestBody UpdateMemberRoleRequest request, Authentication authentication) {
        memberService.updateMemberRole(workspaceId, userId, request.getRole(), authentication.getName());
        return ResponseEntity.ok("Member role updated");
    }

    @DeleteMapping("/{workspaceId}/members/{userId}")
    public ResponseEntity<String> removeMember(@PathVariable Long workspaceId, @PathVariable Long userId, Authentication authentication) {
        memberService.removeMember(workspaceId, userId, authentication.getName());
        return ResponseEntity.ok("Member removed from workspace");
    }

}