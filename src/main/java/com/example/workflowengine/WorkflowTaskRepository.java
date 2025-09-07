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
                .filter(task -> task.getStageId() != null) // not finished
                .filter(task -> !task.hasIncident()) // not failed
                .filter(task -> !task.isWaitingForHumanTaskToBeCompleted()) // not waiting for human task to be completed
                .filter(task -> task.getTimedOutUntil() == null || task.getTimedOutUntil().isAfter(Instant.now())) // not delayed
                .findAny();
    }
}
