package com.example.agreement;

import com.example.workflowengine.WorkflowDefinition;
import com.example.workflowengine.WorkflowInstance;
import jakarta.inject.Singleton;

@Singleton
public class LeasingCoreAgreementCreationWorkflow implements WorkflowDefinition {

    public void createAgreementInLeasingCore() {
        System.out.println("Creating Leasing Core Agreement");

        // variables
        boolean socDataValidated = false;
        boolean creditFrameDataSaved = false;
        var rpaPayload = new Object();
        boolean lcWorkingHoursValid = false;
        boolean rpaCalled = false;
        var rpaResponse = new Object();
        boolean rpaResponseBreachedSLA = false;
        boolean slaBreachNoted = false;
        boolean agreementCreationFailureHumanTaskCreated = false;

        if (!socDataValidated) {
            System.out.println("Validating soc data");
            boolean socDataValid = validateSoc();

            if (!socDataValid) {
                createInvalidSocIncident();
                return;
            }
            System.out.println("soc data is valid, carry on");
        }

        if (!creditFrameDataSaved) {
            getAndSaveCreditFrameInfo();
            return;
        }

        if (rpaPayload == null) {
            createRpaPayload();
            return;
        }

        if (!lcWorkingHoursValid) {
            System.out.println("LC is not working at the moment, waiting until it does");
            return;
        }

        if (!rpaCalled) {
            callRPA();
            return;
        }

        if (rpaResponse == null) {
            checkForRPACallback(rpaResponseBreachedSLA, slaBreachNoted);
            return;
        }

        System.out.println("must have rpa response received");
        boolean agreementCreated = rpaResponse != null; // from response
        if (agreementCreated) {
            handleSuccessfullAgreementCreation();
            return;
        }

        if (!agreementCreationFailureHumanTaskCreated) {
            createRPAFailedHumanTask();
            return;
        }
        System.out.println("human task must be completed");
        resetTaskToCreateRpaPayload();
    }

    private static void resetTaskToCreateRpaPayload() {
        System.out.println("resetting variables so new rpa request is created");
    }

    private static void createRPAFailedHumanTask() {
        System.out.println("Agreement creation failed, creating human task");
    }

    private static void handleSuccessfullAgreementCreation() {
        System.out.println("Agreement created successfully");
    }

    private static void checkForRPACallback(boolean rpaResponseBreachedSLA, boolean slaBreachNoted) {
        System.out.println("still waiting for rpa callback, delaying task for lets say 1 minute");
        if (rpaResponseBreachedSLA) {
            if (!slaBreachNoted) {
                System.out.println("rpa callback is taking too long, noting as breached SLA");
            }
        }
    }

    private static void callRPA() {
        System.out.println("making RPA request to create agreement");
    }

    private static void createRpaPayload() {
        System.out.println("Creating RPA payload");
    }

    private static void getAndSaveCreditFrameInfo() {
        System.out.println("Getting credit frame data");
        // fetch credit frame data
        System.out.println("Saving credit frame data");
        // save credit frame id, credit limits for customer, existing involved parties from LC
    }

    private static void createInvalidSocIncident() {
        System.out.println("Soc data invalid, creating incident");
    }

    private static boolean validateSoc() {
        return false;
    }

    @Override
    public void handle(WorkflowInstance task) {
        createAgreementInLeasingCore();
    }
}
