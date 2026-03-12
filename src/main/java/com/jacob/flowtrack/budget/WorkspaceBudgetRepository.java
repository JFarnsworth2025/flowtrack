package com.jacob.flowtrack.budget;

import com.jacob.flowtrack.workspace.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceBudgetRepository extends JpaRepository<WorkspaceBudget, Long> {

    List<WorkspaceBudget> findByWorkspace(Workspace workspace);
    Optional<WorkspaceBudget> findByWorkspaceAndCategory(Workspace workspace, String category);

}