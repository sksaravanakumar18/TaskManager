package com.cloudops.taskservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createAndFetchTask() throws Exception {
        String payload = """
                {
                  \"title\": \"Prepare GCP interview\",
                  \"description\": \"Cover Cloud Run and GKE\",
                  \"assignee\": \"Ravi\",
                  \"status\": \"TODO\"
                }
                """;

        String response = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Prepare GCP interview"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = response.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(get("/api/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignee").value("Ravi"));
    }

    @Test
    void filterByAssigneeAndStatus() throws Exception {
        String payloadOne = """
                {
                  \"title\": \"Task One\",
                  \"assignee\": \"Alice\",
                  \"status\": \"TODO\"
                }
                """;

        String payloadTwo = """
                {
                  \"title\": \"Task Two\",
                  \"assignee\": \"Bob\",
                  \"status\": \"DONE\"
                }
                """;

        mockMvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content(payloadOne))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content(payloadTwo))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/tasks").param("assignee", "Alice").param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].assignee").value("Alice"));
    }
}
