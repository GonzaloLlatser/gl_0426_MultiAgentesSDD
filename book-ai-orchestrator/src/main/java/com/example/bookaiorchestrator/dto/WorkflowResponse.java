package com.example.bookaiorchestrator.dto;

import java.util.List;

public record WorkflowResponse(String task, List<AgentStepResult> steps) {
}
