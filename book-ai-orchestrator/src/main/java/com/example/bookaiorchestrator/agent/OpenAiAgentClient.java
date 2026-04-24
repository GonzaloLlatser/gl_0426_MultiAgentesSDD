package com.example.bookaiorchestrator.agent;

import com.example.bookaiorchestrator.dto.AgentStepResult;
import com.example.bookaiorchestrator.dto.AgentStructuredOutput;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@Profile("openai")
public class OpenAiAgentClient implements AgentClient {

    private final WebClient webClient;
    private final AgentPromptFactory agentPromptFactory;
    private final ObjectMapper objectMapper;
    private final String model;

    public OpenAiAgentClient(
            WebClient.Builder webClientBuilder,
            AgentPromptFactory agentPromptFactory,
            ObjectMapper objectMapper,
            @Value("${openai.api-key}") String apiKey,
            @Value("${openai.model}") String model
    ) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.openai.com")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
        this.agentPromptFactory = agentPromptFactory;
        this.objectMapper = objectMapper;
        this.model = model;
    }

    @Override
    public AgentStepResult run(AgentRole role, String task) {
        JsonNode response = webClient.post()
                .uri("/v1/responses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "model", model,
                        "input", agentPromptFactory.buildPrompt(role, task)
                ))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        String rawOutput = extractOutput(response);
        return new AgentStepResult(role, rawOutput, parseStructuredOutput(rawOutput));
    }

    private AgentStructuredOutput parseStructuredOutput(String rawOutput) {
        try {
            return objectMapper.readValue(rawOutput, AgentStructuredOutput.class);
        } catch (Exception e) {
            return null;
        }
    }

    private String extractOutput(JsonNode response) {
        if (response == null) {
            return "";
        }

        JsonNode outputText = response.get("output_text");
        if (outputText != null && outputText.isTextual()) {
            return outputText.asText();
        }

        return response.toPrettyString();
    }
}
