package com.example.document.creation;

import com.example.workflowengine.WorkflowDefinition;
import com.example.workflowengine.WorkflowInstance;
import com.example.workflowengine.WorkflowStep;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AgreementDocumentCreationWorkflow implements WorkflowDefinition {
    
    private static final Logger log = LoggerFactory.getLogger(AgreementDocumentCreationWorkflow.class);

    public enum AgreementDocumentCreationStep implements WorkflowStep {
        REQUEST_TO_GENERATE_DOCUMENT,
        HANDLE_CALLBACK,
        HANDLE_CREATED_DOCUMENT,
        CREATE_INCIDENT,
        HANDLE_INCIDENT
    }

    public void createAgreementDocument(WorkflowInstance task) {
        final var step = (AgreementDocumentCreationStep) task.getStepId();
        switch (step) {
            case REQUEST_TO_GENERATE_DOCUMENT -> {
                requestToGenerateDocument();
                task.setWaitingForCallback(true);
                task.setStepId(AgreementDocumentCreationStep.HANDLE_CALLBACK);
            }
            case HANDLE_CALLBACK -> {
                handleCallback(task);
            }
            case HANDLE_CREATED_DOCUMENT -> {
                handleCreatedDocument();
                task.setStepId(null);
            }
            case CREATE_INCIDENT -> {
                createIncident();
                task.setStepId(AgreementDocumentCreationStep.HANDLE_INCIDENT);
                task.setHasIncident(true);
            }
            case HANDLE_INCIDENT -> {
                resetTaskToRequestToGenerateDocument();
                task.setStepId(AgreementDocumentCreationStep.REQUEST_TO_GENERATE_DOCUMENT);
            }
        }
    }

    private static void resetTaskToRequestToGenerateDocument() {
        log.info("reset variables to try creating document again from beginning");
    }

    private static void createIncident() {
        log.info("Document creation failed, creating incident");
    }

    private static void handleCreatedDocument() {
        log.info("Document created");
    }

    private static void requestToGenerateDocument() {
        log.info("Requesting to generate agreement document, will wait for callback");
    }

    private void handleCallback(WorkflowInstance task) {
        log.info("Handling callback");
        if (System.currentTimeMillis() % 2 == 0) {
            log.info("looks good, carry on");
            task.setStepId(AgreementDocumentCreationStep.HANDLE_CREATED_DOCUMENT);
        } else {
            log.info("looks bad, create incident please");
            task.setStepId(AgreementDocumentCreationStep.CREATE_INCIDENT);
        }
    }

    @Override
    public void handle(WorkflowInstance task) {
        createAgreementDocument(task);
    }
}
