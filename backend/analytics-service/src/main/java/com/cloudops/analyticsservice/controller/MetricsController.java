package com.cloudops.analyticsservice.controller;

import com.cloudops.analyticsservice.model.TaskMetrics;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final TaskMetrics metrics;

    public MetricsController(TaskMetrics metrics) {
        this.metrics = metrics;
    }

    @GetMapping("/tasks")
    public Map<String, Object> getTaskMetrics() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalCreated", metrics.getTotalCreated());
        response.put("totalUpdated", metrics.getTotalUpdated());
        response.put("totalDeleted", metrics.getTotalDeleted());
        response.put("statusBreakdown", Map.of(
                "todo", metrics.getTodoCount(),
                "inProgress", metrics.getInProgressCount(),
                "done", metrics.getDoneCount()
        ));
        return response;
    }
}
