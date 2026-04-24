package com.example.bookaiorchestrator.agent;

import com.example.bookaiorchestrator.dto.AgentStepResult;
import com.example.bookaiorchestrator.dto.AgentStructuredOutput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("!openai")
public class FakeAgentClient implements AgentClient {

    private final ObjectMapper objectMapper;

    public FakeAgentClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public AgentStepResult run(AgentRole role, String task) {
        AgentStructuredOutput structuredOutput = new AgentStructuredOutput(
                "OK",
                responseFor(role, task),
                List.of(),
                "CONTINUE"
        );

        return new AgentStepResult(role, toRawOutput(structuredOutput), structuredOutput);
    }

    private String toRawOutput(AgentStructuredOutput structuredOutput) {
        try {
            return objectMapper.writeValueAsString(structuredOutput);
        } catch (JsonProcessingException e) {
            return structuredOutput.summary();
        }
    }

    private String responseFor(AgentRole role, String task) {
        return switch (role) {
            case SPEC_AGENT -> "Spec creada para: " + task;
            case DEVELOPER_AGENT -> "Implementacion simulada para: " + task;
            case TEST_AGENT -> "Tests simulados para: " + task;
            case REVIEWER_AGENT -> "Revision simulada sin incidencias para: " + task;
            case FIX_AGENT -> "No se requieren fixes para: " + task;
        };
    }
}
