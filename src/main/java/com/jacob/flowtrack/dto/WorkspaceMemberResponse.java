package com.jacob.flowtrack.dto;

import com.jacob.flowtrack.entity.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class WorkspaceMemberResponse {

    private String username;
    private WorkspaceRole role;
    private LocalDateTime joinedAt;

}