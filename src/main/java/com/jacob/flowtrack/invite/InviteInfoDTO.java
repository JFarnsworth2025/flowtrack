package com.jacob.flowtrack.invite;

import com.jacob.flowtrack.workspace.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class InviteInfoDTO {

    private String email;
    private WorkspaceRole role;
    private InviteStatus status;
    private String invitedBy;
    private LocalDateTime expiresAt;

}