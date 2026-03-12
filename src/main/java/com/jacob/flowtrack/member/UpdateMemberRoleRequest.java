package com.jacob.flowtrack.member;

import com.jacob.flowtrack.workspace.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateMemberRoleRequest {

    private WorkspaceRole role;

}