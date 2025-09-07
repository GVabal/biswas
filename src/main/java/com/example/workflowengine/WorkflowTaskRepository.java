package com.example.workflowengine;

import jakarta.inject.Singleton;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class WorkflowTaskRepository {

    private static final Map<String, WorkflowInstance> workflowTasks = new HashMap<>();
    private static int nextId = 1000000;

    public Optional<WorkflowInstance> findById(String id) {
        return Optional.ofNullable(workflowTasks.get(id));
    }

    public String save(WorkflowInstance workflowInstance) {
        String salesCaseId = String.valueOf(nextId++);
        workflowInstance.setSalesCaseId(salesCaseId);
        workflowTasks.put(salesCaseId, workflowInstance);
        return salesCaseId;
    }

    public Optional<WorkflowInstance> findValidForHandling() {
        return workflowTasks.values()
                .stream()
                // add filter that task is not already taken by az2 or az3 (not being handled right now)
                .filter(task -> !task.isWaitingForCallback())
                .filter(task -> !task.isWaitingForHumanTaskToBeCompleted())
                .filter(task -> !task.hasIncident())
                .filter(task -> task.getTimedOutUntil() == null || Instant.now().isAfter(task.getTimedOutUntil()))
                .findAny();
    }
}
