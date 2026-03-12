package com.jacob.flowtrack.invite;

import com.jacob.flowtrack.workspace.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceInviteRepository extends JpaRepository<WorkspaceInvite, Long> {

    Optional<WorkspaceInvite> findByToken(String token);

    List<WorkspaceInvite> findByWorkspaceId(Long workspaceId);

    Boolean existsByEmailAndWorkspaceAndStatus(String email, Workspace workspace, InviteStatus status);

    List<WorkspaceInvite> findByWorkspace(Workspace workspace);

}