package com.jacob.flowtrack.dto;

import com.jacob.flowtrack.entity.WorkspaceRole;
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
