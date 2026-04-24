package com.example.bookaiorchestrator.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrchestratorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void runReturnsAgentSteps() throws Exception {
        mockMvc.perform(post("/orchestrator/run")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "task": "Crear endpoint POST /books/reserve que reciba title y userName"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.task").value("Crear endpoint POST /books/reserve que reciba title y userName"))
                .andExpect(jsonPath("$.steps.length()").value(5))
                .andExpect(jsonPath("$.steps[0].agent").value("SPEC_AGENT"))
                .andExpect(jsonPath("$.steps[0].structuredOutput.status").value("OK"))
                .andExpect(jsonPath("$.steps[1].agent").value("DEVELOPER_AGENT"))
                .andExpect(jsonPath("$.steps[2].agent").value("TEST_AGENT"))
                .andExpect(jsonPath("$.steps[3].agent").value("REVIEWER_AGENT"))
                .andExpect(jsonPath("$.steps[4].agent").value("FIX_AGENT"));
    }
}
