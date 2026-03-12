package com.jacob.flowtrack.workspace;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    public Workspace getWorkspace(Long workspaceId) {
        return workspaceRepository.findById(workspaceId).orElseThrow(() -> new RuntimeException("Workspace not found"));
    }

}