package com.cloudops.taskservice.controller;

import com.cloudops.taskservice.dto.TaskRequest;
import com.cloudops.taskservice.model.Task;
import com.cloudops.taskservice.model.TaskStatus;
import com.cloudops.taskservice.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@Valid @RequestBody TaskRequest request) {
        return taskService.create(request);
    }

    @GetMapping
    public List<Task> list(@RequestParam(required = false) String assignee,
                           @RequestParam(required = false) TaskStatus status,
                           @RequestParam(required = false) String query) {
        return taskService.list(assignee, status, query);
    }

    @GetMapping("/{id}")
    public Task getById(@PathVariable String id) {
        return taskService.getById(id);
    }

    @PutMapping("/{id}")
    public Task update(@PathVariable String id, @Valid @RequestBody TaskRequest request) {
        return taskService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public Task updateStatus(@PathVariable String id, @RequestParam TaskStatus status) {
        return taskService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        taskService.delete(id);
    }
}
