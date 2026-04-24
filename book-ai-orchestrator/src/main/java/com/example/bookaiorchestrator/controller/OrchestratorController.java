package com.example.bookaiorchestrator.controller;

import com.example.bookaiorchestrator.dto.TaskRequest;
import com.example.bookaiorchestrator.dto.WorkflowResponse;
import com.example.bookaiorchestrator.service.OrchestratorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orchestrator")
public class OrchestratorController {

    private final OrchestratorService orchestratorService;

    public OrchestratorController(OrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @PostMapping("/run")
    public WorkflowResponse run(@RequestBody TaskRequest request) {
        return orchestratorService.run(request.task());
    }
}
