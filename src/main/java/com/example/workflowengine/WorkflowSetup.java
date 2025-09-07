package com.example.workflowengine;

import com.example.document.creation.AgreementDocumentCreationWorkflow;
import com.example.credit.CreditDecisionWorkflow;
import com.example.customer.CustomerInfoWorkflow;
import com.example.agreement.LeasingCoreAgreementCreationWorkflow;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class WorkflowSetup {

    public enum Stage {
        CUSTOMER_INFO_WORKFLOW,
        CREDIT_DECISION_WORKFLOW,
        LC_AGREEMENT_WORKFLOW,
        AGREEMENT_DOCUMENT_WORKFLOW
    }

    private final List<WorkflowDefinition> workflowDefinitions;

    public WorkflowSetup(CustomerInfoWorkflow customerInfoWorkflow,
                         CreditDecisionWorkflow creditDecisionWorkflow,
                         LeasingCoreAgreementCreationWorkflow leasingCoreAgreementCreationWorkflow,
                         AgreementDocumentCreationWorkflow agreementDocumentCreationWorkflow) {
        workflowDefinitions = List.of(
                customerInfoWorkflow,
                creditDecisionWorkflow,
                leasingCoreAgreementCreationWorkflow,
                agreementDocumentCreationWorkflow
        );
    }

    public WorkflowDefinition getWorkflowDefinition(Stage stageId) {
        return workflowDefinitions.get(stageId.ordinal());
    }

    public Stage getNextStageId(Stage stage) {
        return switch (stage) {
            case CUSTOMER_INFO_WORKFLOW -> Stage.CREDIT_DECISION_WORKFLOW; // would be best to have these stage names in their respective classes, so it is easy to find code with ctrl+click
            default -> null;
//            case CREDIT_DECISION_WORKFLOW -> Stage.LC_AGREEMENT_WORKFLOW;
//            case LC_AGREEMENT_WORKFLOW -> Stage.AGREEMENT_DOCUMENT_WORKFLOW;
//            case AGREEMENT_DOCUMENT_WORKFLOW -> null;
        };
    }

    public WorkflowStep getFirstStepForStage(Stage stage) {
        return switch (stage) {
            case CREDIT_DECISION_WORKFLOW -> CreditDecisionWorkflow.CreditDecisionStep.CREATE_CREDIT_FRAME;
            default -> null;
        };
    }
}
