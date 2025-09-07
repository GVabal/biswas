package com.example.customer;

import com.example.workflowengine.WorkflowStep;
import com.example.workflowengine.WorkflowDefinition;
import com.example.workflowengine.WorkflowInstance;
import jakarta.inject.Singleton;

@Singleton
public class CustomerInfoWorkflow implements WorkflowDefinition {

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
        System.out.println("Waiting for customer to be created");
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
        System.out.println("Getting customer info");
        var customer = getCustomerInfo();
        if (customer == null) {
            System.out.println("customer does not exist");
            task.setStepId(CustomerInfoStep.REQUEST_TO_CREATE_CUSTOMER);
        } else {
            saveCustomerInfo(customer);
            task.setStepId(null);
        }
    }

    private static void createIncident() {
        System.out.println("Customer is taking too long to be created. Creating incident");
    }

    private static void createCustomer() {
        System.out.println("requesting WnO to create customer");
    }

    private Object getCustomerInfo() {
        System.out.println("Calling customer recognition client to get customer info");
        if (customerExists == null) {
            customerExists = System.currentTimeMillis() % 2 == 0;
        }
        if (customerExists) {
            System.out.println("customer exists");
            return new Object();
        }
        System.out.println("customer does not exist");
        customerExists = null;
        return null;
    }

    private static void saveCustomerInfo(Object customerInfo) {
        System.out.println("customer exits, saving needed information about customer to db");
    }

    @Override
    public void handle(WorkflowInstance task) {
        onboardCustomer(task);
    }
}
