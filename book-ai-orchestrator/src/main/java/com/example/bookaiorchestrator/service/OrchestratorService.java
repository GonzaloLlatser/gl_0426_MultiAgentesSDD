package com.example.bookaiorchestrator.service;

import com.example.bookaiorchestrator.agent.AgentClient;
import com.example.bookaiorchestrator.agent.AgentRole;
import com.example.bookaiorchestrator.dto.AgentStepResult;
import com.example.bookaiorchestrator.dto.WorkflowResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrchestratorService {

    private static final Logger log = LoggerFactory.getLogger(OrchestratorService.class);

    private final AgentClient agentClient;
    private final FileSystemService fileSystemService;

    public OrchestratorService(AgentClient agentClient, FileSystemService fileSystemService) {
        this.agentClient = agentClient;
        this.fileSystemService = fileSystemService;
    }

    public WorkflowResponse run(String task) {
        log.info("Starting orchestrator workflow. task={}", task);

        WorkflowContext context = new WorkflowContext(task);
        List<AgentStepResult> steps = new ArrayList<>();

        for (AgentRole role : AgentRole.values()) {
            AgentStepResult result = runAgent(role, context);
            applyFileChanges(result);
            context.addOutput(role, result.rawOutput());
            steps.add(result);
        }

        log.info("Finished orchestrator workflow. steps={}", steps.size());

        return new WorkflowResponse(task, steps);
    }

    private AgentStepResult runAgent(AgentRole role, WorkflowContext context) {
        log.info("Running agent {}", role);
        AgentStepResult result = agentClient.run(role, context.buildContextFor(role));
        log.info("Finished agent {}. output={}", role, result.rawOutput());
        return result;
    }

    private void applyFileChanges(AgentStepResult result) {
        if (result.structuredOutput() == null || result.structuredOutput().fileChanges().isEmpty()) {
            return;
        }

        fileSystemService.applyChanges(result.structuredOutput().fileChanges());
    }
}
