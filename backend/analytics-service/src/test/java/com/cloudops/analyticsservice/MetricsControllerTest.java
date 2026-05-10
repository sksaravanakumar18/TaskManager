package com.cloudops.analyticsservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnInitialMetrics() throws Exception {
        mockMvc.perform(get("/api/metrics/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCreated").value(0))
                .andExpect(jsonPath("$.statusBreakdown.todo").value(0));
    }
}
