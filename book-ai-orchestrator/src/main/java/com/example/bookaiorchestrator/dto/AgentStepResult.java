package com.example.bookaiorchestrator.dto;

import com.example.bookaiorchestrator.agent.AgentRole;

public record AgentStepResult(
        AgentRole agent,
        String rawOutput,
        AgentStructuredOutput structuredOutput
) {
}
