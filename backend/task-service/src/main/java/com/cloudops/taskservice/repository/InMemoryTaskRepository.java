package com.cloudops.taskservice.repository;

import com.cloudops.taskservice.model.Task;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTaskRepository {

    private final ConcurrentHashMap<String, Task> store = new ConcurrentHashMap<>();

    public Task save(Task task) {
        store.put(task.getId(), task);
        return task;
    }

    public List<Task> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Task> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public void deleteById(String id) {
        store.remove(id);
    }
}
