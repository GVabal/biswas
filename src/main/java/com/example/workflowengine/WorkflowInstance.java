package com.example.workflowengine;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class WorkflowInstance {

    private String salesCaseId;
    private Instant timedOutUntil;
    private boolean waitingForHumanTaskToBeCompleted;
    private boolean hasIncident;
    private WorkflowSetup.Stage stageId;
    private WorkflowStep workflowStep;
    private final Map<String, Object> variables = new HashMap<>();

    public String getSalesCaseId() {
        return salesCaseId;
    }

    public void setSalesCaseId(String salesCaseId) {
        this.salesCaseId = salesCaseId;
    }

    public Instant getTimedOutUntil() {
        return timedOutUntil;
    }

    public void setTimedOutUntil(Instant timedOutUntil) {
        this.timedOutUntil = timedOutUntil;
    }

    public boolean isWaitingForHumanTaskToBeCompleted() {
        return waitingForHumanTaskToBeCompleted;
    }

    public void setWaitingForHumanTaskToBeCompleted(boolean waitingForHumanTaskToBeCompleted) {
        this.waitingForHumanTaskToBeCompleted = waitingForHumanTaskToBeCompleted;
    }

    public boolean hasIncident() {
        return hasIncident;
    }

    public void setHasIncident(boolean hasIncident) {
        this.hasIncident = hasIncident;
    }

    public WorkflowSetup.Stage getStageId() {
        return stageId;
    }

    public void setStageId(WorkflowSetup.Stage stageId) {
        this.stageId = stageId;
    }

    public WorkflowStep getStepId() {
        return workflowStep;
    }

    public void setStepId(WorkflowStep workflowStep) {
        this.workflowStep = workflowStep;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void addVariable(String name, Object value) {
        variables.put(name, value);
    }
}
