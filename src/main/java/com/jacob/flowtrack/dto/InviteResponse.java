package com.jacob.flowtrack.dto;

import com.jacob.flowtrack.entity.InviteStatus;
import com.jacob.flowtrack.entity.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class InviteResponse {

    private String email;
    private WorkspaceRole role;
    private InviteStatus status;
    private LocalDateTime expiresAt;

}
