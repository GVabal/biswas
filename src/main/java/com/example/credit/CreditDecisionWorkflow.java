package com.example.credit;

import com.example.workflowengine.WorkflowStep;
import com.example.workflowengine.WorkflowDefinition;
import com.example.workflowengine.WorkflowInstance;
import jakarta.inject.Singleton;

@Singleton
public class CreditDecisionWorkflow implements WorkflowDefinition {

    public enum CreditDecisionStep implements WorkflowStep {
        CREATE_CREDIT_FRAME, // only split steps into smaller ones if we expect to fail somewhere specific and want to be able to continue from that point instead of doing whole step from the beginning
        CREATE_CREDIT_DECISION_HUMAN_TASK,
        MAKE_CREDIT_DECISION
    }

    public void makeCreditDecision(WorkflowInstance task) {
        final var step = (CreditDecisionStep) task.getStepId();
        switch (step) {
            case CREATE_CREDIT_FRAME -> {
                createCreditFrame();
                task.setStepId(CreditDecisionStep.CREATE_CREDIT_DECISION_HUMAN_TASK);
            }
            case CREATE_CREDIT_DECISION_HUMAN_TASK -> {
                createCreditDecisionHumanTask();
                task.setWaitingForHumanTaskToBeCompleted(true);
            }
            case MAKE_CREDIT_DECISION -> {
                handleHumanTaskResult(task.getVariables().get("task-MAKE_CREDIT_DECISION"));
            }
        }
    }

    private static void handleHumanTaskResult(Object creditDecisionResult) {
        boolean creditApproved = System.currentTimeMillis() % 2 == 0; // check result
        if (creditApproved) {
            System.out.println("Credit approved");
        } else {
            System.out.println("Credit rejected");
        }
    }

    private static void createCreditDecisionHumanTask() {
        System.out.println("Creating credit decision human task");
    }

    private static void createCreditFrame() {
        System.out.println("Creating credit frame");
    }

    @Override
    public void handle(WorkflowInstance task) {
        makeCreditDecision(task);
    }
}
