package com.jacob.flowtrack.security;

import com.jacob.flowtrack.member.User;
import com.jacob.flowtrack.member.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkspaceSecurity {

    private final WorkspaceMemberRepository workspaceMemberRepository;

    public boolean isMember(Long workspaceId, Authentication authentication) {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();

        return workspaceMemberRepository.findByUserIdAndWorkspaceId(user.getId(), workspaceId);
    }

}
