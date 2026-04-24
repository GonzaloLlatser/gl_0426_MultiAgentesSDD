package com.example.bookaiorchestrator.dto;

import java.util.List;

public record AgentStructuredOutput(
        String status,
        String summary,
        List<FileChange> fileChanges,
        String nextAction
) {
}
