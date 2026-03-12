package com.jacob.flowtrack.invite;

import com.jacob.flowtrack.member.User;
import com.jacob.flowtrack.workspace.Workspace;
import com.jacob.flowtrack.workspace.WorkspaceRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class WorkspaceInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    private String email;

    @Enumerated(EnumType.STRING)
    private WorkspaceRole role;

    private String token;

    @Enumerated(EnumType.STRING)
    private InviteStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    @ManyToOne
    private Workspace workspace;

    @ManyToOne
    private User invitedBy;

}