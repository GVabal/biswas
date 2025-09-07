package com.example.workflowengine;

public interface WorkflowDefinition {

    void handle(WorkflowInstance task);
}
