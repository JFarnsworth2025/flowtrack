package com.jacob.flowtrack.invite;

import com.jacob.flowtrack.workspace.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InviteRequest {

    private String email;
    private WorkspaceRole role;

}
