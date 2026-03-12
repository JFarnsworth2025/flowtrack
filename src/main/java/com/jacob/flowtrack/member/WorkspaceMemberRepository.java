package com.jacob.flowtrack.member;

import com.jacob.flowtrack.workspace.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Long> {

    List<WorkspaceMember> findByUser(User user);

    Optional<WorkspaceMember> findByUserAndWorkspace(User user, Workspace workspace);

    boolean existsByUserAndWorkspace(User user, Workspace workspace);

    List<WorkspaceMember> findByWorkspace(Workspace workspace);

}