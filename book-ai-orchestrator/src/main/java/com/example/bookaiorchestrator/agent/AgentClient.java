package com.example.bookaiorchestrator.agent;

import com.example.bookaiorchestrator.dto.AgentStepResult;

public interface AgentClient {

    AgentStepResult run(AgentRole role, String task);
}
