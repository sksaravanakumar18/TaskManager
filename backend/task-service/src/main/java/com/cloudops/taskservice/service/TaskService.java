package com.cloudops.taskservice.service;

import com.cloudops.events.model.DomainEvent;
import com.cloudops.events.model.TaskCreatedEvent;
import com.cloudops.events.model.TaskDeletedEvent;
import com.cloudops.events.model.TaskUpdatedEvent;
import com.cloudops.events.publisher.EventPublisher;
import com.cloudops.taskservice.dto.TaskRequest;
import com.cloudops.taskservice.exception.TaskNotFoundException;
import com.cloudops.taskservice.model.Task;
import com.cloudops.taskservice.model.TaskStatus;
import com.cloudops.taskservice.repository.InMemoryTaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class TaskService {

    private final String tasksTopic;
    private final InMemoryTaskRepository repository;
    private final EventPublisher eventPublisher;

    public TaskService(InMemoryTaskRepository repository,
                       EventPublisher eventPublisher,
                       @Value("${pubsub.topic.task-events:task-events}") String tasksTopic) {
        this.repository    = repository;
        this.eventPublisher = eventPublisher;
        this.tasksTopic    = tasksTopic;
    }

    @CacheEvict(cacheNames = "tasksList", allEntries = true)
    public Task create(TaskRequest request) {
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setTitle(request.getTitle().trim());
        task.setDescription(request.getDescription());
        task.setAssignee(request.getAssignee());
        task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO);
        Instant now = Instant.now();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        Task saved = repository.save(task);
        
        TaskCreatedEvent payload = new TaskCreatedEvent(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getAssignee(),
                saved.getStatus().toString(),
                saved.getCreatedAt()
        );
        eventPublisher.publish(tasksTopic, DomainEvent.of("task.created", payload));
        
        return saved;
    }

    @Cacheable(cacheNames = "tasksList")
    public List<Task> list(String assignee, TaskStatus status, String query) {
        String loweredQuery = query == null ? null : query.toLowerCase(Locale.ROOT);

        return repository.findAll().stream()
                .filter(task -> assignee == null || assignee.isBlank() || assignee.equalsIgnoreCase(task.getAssignee()))
                .filter(task -> status == null || status == task.getStatus())
                .filter(task -> {
                    if (loweredQuery == null || loweredQuery.isBlank()) {
                        return true;
                    }
                    String title = task.getTitle() == null ? "" : task.getTitle().toLowerCase(Locale.ROOT);
                    String description = task.getDescription() == null ? "" : task.getDescription().toLowerCase(Locale.ROOT);
                    return title.contains(loweredQuery) || description.contains(loweredQuery);
                })
                .sorted(Comparator.comparing(Task::getCreatedAt).reversed())
                .toList();
    }

    @Cacheable(cacheNames = "tasksById", key = "#id")
    public Task getById(String id) {
        return repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "tasksById", key = "#id"),
            @CacheEvict(cacheNames = "tasksList", allEntries = true)
    })
    public Task update(String id, TaskRequest request) {
        Task existing = requireExistingTask(id);
        existing.setTitle(request.getTitle().trim());
        existing.setDescription(request.getDescription());
        existing.setAssignee(request.getAssignee());
        existing.setStatus(request.getStatus() != null ? request.getStatus() : existing.getStatus());
        existing.setUpdatedAt(Instant.now());
        Task saved = repository.save(existing);
        
        TaskUpdatedEvent payload = new TaskUpdatedEvent(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getAssignee(),
                saved.getStatus().toString(),
                saved.getUpdatedAt()
        );
        eventPublisher.publish(tasksTopic, DomainEvent.of("task.updated", payload));
        
        return saved;
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "tasksById", key = "#id"),
            @CacheEvict(cacheNames = "tasksList", allEntries = true)
    })
    public Task updateStatus(String id, TaskStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }
        Task existing = requireExistingTask(id);
        existing.setStatus(status);
        existing.setUpdatedAt(Instant.now());
        Task saved = repository.save(existing);
        
        TaskUpdatedEvent payload = new TaskUpdatedEvent(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getAssignee(),
                saved.getStatus().toString(),
                saved.getUpdatedAt()
        );
        eventPublisher.publish(tasksTopic, DomainEvent.of("task.updated", payload));
        
        return saved;
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "tasksById", key = "#id"),
            @CacheEvict(cacheNames = "tasksList", allEntries = true)
    })
    public void delete(String id) {
        Task existing = requireExistingTask(id);
        repository.deleteById(id);
        
        TaskDeletedEvent payload = new TaskDeletedEvent(id, Instant.now());
        eventPublisher.publish(tasksTopic, DomainEvent.of("task.deleted", payload));
    }

    private Task requireExistingTask(String id) {
        return repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }
}
