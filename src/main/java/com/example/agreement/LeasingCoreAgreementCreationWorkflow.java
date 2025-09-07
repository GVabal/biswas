package com.example.agreement;

import com.example.workflowengine.WorkflowDefinition;
import com.example.workflowengine.WorkflowInstance;
import com.example.workflowengine.WorkflowStep;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

@Singleton
public class LeasingCoreAgreementCreationWorkflow implements WorkflowDefinition {

    private static final Logger log = LoggerFactory.getLogger(LeasingCoreAgreementCreationWorkflow.class);

    public enum LeasingCoreAgreementCreationStep implements WorkflowStep {
        VALIDATE_SOC_DATA,
        GET_AND_SAVE_CREDIT_FRAME_INFO,
        CREATE_RPA_PAYLOAD,
        WAIT_FOR_LC_WORKING_HOURS,
        CALL_RPA,
        CHECK_FOR_RPA_CALLBACK,
        HANDLE_SUCCESSFUL_AGREEMENT_CREATION,
        CREATE_RPA_FAILED_HUMAN_TASK,
        HANDLE_RPA_FAILED_HUMAN_TASK
    }

    public void createAgreementInLeasingCore(WorkflowInstance task) {
        final var step = (LeasingCoreAgreementCreationStep) task.getStepId();
        switch (step) {
            case VALIDATE_SOC_DATA -> {
                validateSocData(task);
            }
            case GET_AND_SAVE_CREDIT_FRAME_INFO -> {
                getAndSaveCreditFrameInfo();
                task.setStepId(LeasingCoreAgreementCreationStep.CREATE_RPA_PAYLOAD);
            }
            case CREATE_RPA_PAYLOAD -> {
                createRpaPayload();
                task.setStepId(LeasingCoreAgreementCreationStep.WAIT_FOR_LC_WORKING_HOURS);
            }
            case WAIT_FOR_LC_WORKING_HOURS -> {
                waitForLcWorkingHours(task);
            }
            case CALL_RPA -> {
                callRPA();
                task.setStepId(LeasingCoreAgreementCreationStep.CHECK_FOR_RPA_CALLBACK);
                task.setTimedOutUntil(Instant.now().plusSeconds(Duration.ofMinutes(1).toSeconds()));
            }
            case CHECK_FOR_RPA_CALLBACK -> {
                checkForRPACallback(task);
            }
            case HANDLE_SUCCESSFUL_AGREEMENT_CREATION -> {
                handleSuccessfullAgreementCreation();
                task.setStepId(null);
            }
            case CREATE_RPA_FAILED_HUMAN_TASK -> {
                createRPAFailedHumanTask();
                task.setStepId(LeasingCoreAgreementCreationStep.HANDLE_RPA_FAILED_HUMAN_TASK);
                task.setWaitingForHumanTaskToBeCompleted(true);
            }
            case HANDLE_RPA_FAILED_HUMAN_TASK -> {
                resetTaskToCreateRpaPayload();
                task.setStepId(LeasingCoreAgreementCreationStep.CREATE_RPA_PAYLOAD);
            }
        }
    }

    private static void waitForLcWorkingHours(WorkflowInstance task) {
        boolean lcWorkingHours = System.currentTimeMillis() % 2 == 0;
        if (!lcWorkingHours) {
            log.info("LC is not working at the moment, waiting until it does");
        } else {
            task.setStepId(LeasingCoreAgreementCreationStep.CALL_RPA);
        }
    }

    private static void validateSocData(WorkflowInstance task) {
        log.info("Validating soc data");
        boolean socDataValid = validateSoc();

        if (!socDataValid) {
            createInvalidSocIncident();
            task.setHasIncident(true);
            return;
        }
        log.info("soc data is valid, carry on");
        task.setStepId(LeasingCoreAgreementCreationStep.GET_AND_SAVE_CREDIT_FRAME_INFO);
    }

    private static void resetTaskToCreateRpaPayload() {
        log.info("resetting variables so new rpa request is created");
    }

    private static void createRPAFailedHumanTask() {
        log.info("Agreement creation failed, creating human task");
    }

    private static void handleSuccessfullAgreementCreation() {
        log.info("Agreement created successfully");
    }

    private static void checkForRPACallback(WorkflowInstance task) {
        if (!task.getVariables().containsKey("rpa-callback")) {
            log.info("rpa callback is taking too long, noting as breached SLA");
            task.addVariable("rpa-callback", new Object());
        } else {
            log.info("rpa callback received, checking if it is successful");
            if (System.currentTimeMillis() % 2 == 0) {
                task.setStepId(LeasingCoreAgreementCreationStep.HANDLE_SUCCESSFUL_AGREEMENT_CREATION);
            } else {
                task.setStepId(LeasingCoreAgreementCreationStep.CREATE_RPA_FAILED_HUMAN_TASK);
            }
        }
    }

    private static void callRPA() {
        log.info("making RPA request to create agreement");
    }

    private static void createRpaPayload() {
        log.info("Creating RPA payload");
    }

    private static void getAndSaveCreditFrameInfo() {
        log.info("Getting credit frame data");
        // fetch credit frame data
        log.info("Saving credit frame data");
        // save credit frame id, credit limits for customer, existing involved parties from LC
    }

    private static void createInvalidSocIncident() {
        log.info("Soc data invalid, creating incident");
    }

    private static boolean validateSoc() {
        return System.currentTimeMillis() % 2 == 0;
    }

    @Override
    public void handle(WorkflowInstance task) {
        createAgreementInLeasingCore(task);
    }
}
