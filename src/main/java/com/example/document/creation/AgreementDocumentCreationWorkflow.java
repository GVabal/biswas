package com.example.document.creation;

import com.example.workflowengine.WorkflowDefinition;
import com.example.workflowengine.WorkflowInstance;
import jakarta.inject.Singleton;

@Singleton
public class AgreementDocumentCreationWorkflow implements WorkflowDefinition {

    public void createAgreementDocument() {
        System.out.println("Creating Agreement Document");

        // variables
        boolean documentCreationRequested = false;
        var documentCreationResponse = new Object();
        boolean documentCreationFailureIncidentCreated = false;

        if (!documentCreationRequested) {
            requestToGenerateDocument();
            return;
        }

        boolean documentCreated = documentCreationResponse != null; // from response
        if (documentCreated) {
            handleCreatedDocument();
            return;
        }

        System.out.println("document must have failed to be created");
        if (!documentCreationFailureIncidentCreated) {
            createIncident();
            return;
        }

        System.out.println("must be restarting document creation");
        resetTaskToRequestToGenerateDocument();
    }

    private static void resetTaskToRequestToGenerateDocument() {
        System.out.println("reset variables to try creating document again from beginning");
    }

    private static void createIncident() {
        System.out.println("Document creation failed, creating incident");
    }

    private static void handleCreatedDocument() {
        System.out.println("Document created");
    }

    private static void requestToGenerateDocument() {
        System.out.println("Requesting to generate agreement document, will wait for callback");
    }

    @Override
    public void handle(WorkflowInstance task) {
        createAgreementDocument();
    }
}
