package com.jacob.flowtrack.repository;

import com.jacob.flowtrack.entity.Workspace;
import com.jacob.flowtrack.entity.WorkspaceBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceBudgetRepository extends JpaRepository<WorkspaceBudget, Long> {

    List<WorkspaceBudget> findByWorkspace(Workspace workspace);
    Optional<WorkspaceBudget> findByWorkspaceAndCategory(Workspace workspace, String category);

}