package com.example.customer;

import com.example.workflowengine.WorkflowStep;
import com.example.workflowengine.WorkflowDefinition;
import com.example.workflowengine.WorkflowInstance;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class CustomerInfoWorkflow implements WorkflowDefinition {
    
    private static final Logger log = LoggerFactory.getLogger(CustomerInfoWorkflow.class);
    
    private static Boolean customerExists = null;

    public enum CustomerInfoStep implements WorkflowStep {
        GET_CUSTOMER_INFO,
        REQUEST_TO_CREATE_CUSTOMER,
        WAIT_FOR_CUSTOMER_TO_BE_CREATED,
        CREATE_CUSTOMER_CREATION_INCIDENT
    }

    private void onboardCustomer(WorkflowInstance task) {
        final var step = (CustomerInfoStep) task.getStepId();
        switch (step) {
            case GET_CUSTOMER_INFO -> handleGetCustomerInfoStep(task);
            case REQUEST_TO_CREATE_CUSTOMER -> handleRequestToCreateCustomerStep(task);
            case WAIT_FOR_CUSTOMER_TO_BE_CREATED -> handleWaitForCustomerToBeCreatedStep(task);
            case CREATE_CUSTOMER_CREATION_INCIDENT -> handleCreateCustomerCreationIncidentStep(task);
        }
    }

    private static void handleCreateCustomerCreationIncidentStep(WorkflowInstance task) {
        createIncident();
        task.setHasIncident(true);
    }

    private void handleWaitForCustomerToBeCreatedStep(WorkflowInstance task) {
        log.info("Waiting for customer to be created");
        var customer = getCustomerInfo();
        if (customer == null) {
            int timesLooped = (int) task.getVariables().get("timesLoopedAfterRequestingToCreateCustomer");
            task.getVariables().put("timesLoopedAfterRequestingToCreateCustomer", ++timesLooped);

            if (timesLooped > 10) {
                task.setStepId(CustomerInfoStep.CREATE_CUSTOMER_CREATION_INCIDENT);
            }
            return;
        }
        task.setStepId(CustomerInfoStep.GET_CUSTOMER_INFO);
    }

    private static void handleRequestToCreateCustomerStep(WorkflowInstance task) {
        createCustomer();
        task.setStepId(CustomerInfoStep.WAIT_FOR_CUSTOMER_TO_BE_CREATED);
        task.getVariables().put("timesLoopedAfterRequestingToCreateCustomer", 0);
    }

    private void handleGetCustomerInfoStep(WorkflowInstance task) {
        log.info("Getting customer info");
        var customer = getCustomerInfo();
        if (customer == null) {
            log.info("customer does not exist - need to create customer then");
            task.setStepId(CustomerInfoStep.REQUEST_TO_CREATE_CUSTOMER);
        } else {
            saveCustomerInfo(customer);
            task.setStepId(null);
        }
    }

    private static void createIncident() {
        log.info("Customer is taking too long to be created. Creating incident");
    }

    private static void createCustomer() {
        log.info("requesting WnO to create customer");
    }

    private Object getCustomerInfo() {
        log.info("Calling customer recognition client to get customer info");
        if (customerExists == null) {
            customerExists = System.currentTimeMillis() % 2 == 0;
        }
        if (customerExists) {
            log.info("customer exists");
            return new Object();
        }
        log.info("customer does not exist");
        customerExists = null;
        return null;
    }

    private static void saveCustomerInfo(Object customerInfo) {
        log.info("customer exits, saving needed information about customer to db");
    }

    @Override
    public void handle(WorkflowInstance task) {
        onboardCustomer(task);
    }
}
