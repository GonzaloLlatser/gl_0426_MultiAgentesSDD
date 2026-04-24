package com.example.bookaiorchestrator.service;

import com.example.bookaiorchestrator.agent.AgentRole;

import java.util.EnumMap;
import java.util.Map;

public class WorkflowContext {

    private final String originalTask;
    private final Map<AgentRole, String> outputs = new EnumMap<>(AgentRole.class);

    public WorkflowContext(String originalTask) {
        this.originalTask = originalTask;
    }

    public void addOutput(AgentRole role, String output) {
        outputs.put(role, output);
    }

    public String getOutput(AgentRole role) {
        return outputs.get(role);
    }

    public String buildContextFor(AgentRole role) {
        StringBuilder context = new StringBuilder();
        context.append("Original task:\n")
                .append(originalTask)
                .append("\n");

        for (AgentRole previousRole : AgentRole.values()) {
            if (previousRole == role) {
                break;
            }

            String output = outputs.get(previousRole);
            if (output != null) {
                context.append("\n")
                        .append(previousRole)
                        .append(" output:\n")
                        .append(output)
                        .append("\n");
            }
        }

        return context.toString();
    }
}
