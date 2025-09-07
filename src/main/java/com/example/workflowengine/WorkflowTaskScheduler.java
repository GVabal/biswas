package com.example.workflowengine;

import com.example.customer.CustomerInfoWorkflow;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;

@Singleton
public class WorkflowTaskScheduler {

    private final WorkflowSetup workflowSetup;
    private final WorkflowTaskRepository workflowTaskRepository;

    public WorkflowTaskScheduler(WorkflowSetup workflowSetup,
                                 WorkflowTaskRepository workflowTaskRepository) {
        this.workflowSetup = workflowSetup;
        this.workflowTaskRepository = workflowTaskRepository;

        var sampleWorkflow = new WorkflowInstance();
        sampleWorkflow.setStageId(WorkflowSetup.Stage.CUSTOMER_INFO_WORKFLOW);
        sampleWorkflow.setStepId(CustomerInfoWorkflow.CustomerInfoStep.GET_CUSTOMER_INFO);
        var salesCaseId = this.workflowTaskRepository.save(sampleWorkflow);
        System.out.println("added case to workflow: " + salesCaseId);
    }

    @Scheduled(fixedDelay = "5s")
    public void handleTask() {
        workflowTaskRepository.findValidForHandling()
                .ifPresent(task -> {
                    workflowSetup.getWorkflowDefinition(task.getStageId())
                            .handle(task);

                    if (task.getStepId() == null) { // no more steps in current stage
                        var nextStageId = workflowSetup.getNextStageId(task.getStageId());
                        if (nextStageId != null) {
                            task.setStageId(nextStageId);
                            task.setStepId(workflowSetup.getFirstStepForStage(nextStageId));
                        } else {
                            System.out.println("Instance %s has finished it's journey lol".formatted(task.getSalesCaseId()));
                            // deal with finished process
                        }
                    }
                });

        // also handle scenario if az2 and az3 would take same task.
        // for example could first lock task with zone identity, then lookup in db again to see if it still belongs to him

        // TODO handle technical errors here - retries, technical incidents
    }
}
