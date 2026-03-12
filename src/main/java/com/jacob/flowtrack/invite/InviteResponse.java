package com.jacob.flowtrack.invite;

import com.jacob.flowtrack.workspace.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
