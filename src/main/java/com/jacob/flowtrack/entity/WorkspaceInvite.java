package com.jacob.flowtrack.entity;

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