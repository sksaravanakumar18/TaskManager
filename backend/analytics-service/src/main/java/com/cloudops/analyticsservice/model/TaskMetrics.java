package com.cloudops.analyticsservice.model;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskMetrics {
    private AtomicInteger totalCreated = new AtomicInteger(0);
    private AtomicInteger totalUpdated = new AtomicInteger(0);
    private AtomicInteger totalDeleted = new AtomicInteger(0);
    private AtomicInteger todoCount = new AtomicInteger(0);
    private AtomicInteger inProgressCount = new AtomicInteger(0);
    private AtomicInteger doneCount = new AtomicInteger(0);
    private Instant lastUpdate = Instant.now();

    public void recordCreated() {
        totalCreated.incrementAndGet();
        todoCount.incrementAndGet();
        lastUpdate = Instant.now();
    }

    public void recordUpdated(String newStatus) {
        totalUpdated.incrementAndGet();
        if ("TODO".equals(newStatus)) todoCount.incrementAndGet();
        else if ("IN_PROGRESS".equals(newStatus)) inProgressCount.incrementAndGet();
        else if ("DONE".equals(newStatus)) doneCount.incrementAndGet();
        lastUpdate = Instant.now();
    }

    public void recordDeleted() {
        totalDeleted.incrementAndGet();
        lastUpdate = Instant.now();
    }

    public int getTotalCreated() {
        return totalCreated.get();
    }

    public int getTotalUpdated() {
        return totalUpdated.get();
    }

    public int getTotalDeleted() {
        return totalDeleted.get();
    }

    public int getTodoCount() {
        return todoCount.get();
    }

    public int getInProgressCount() {
        return inProgressCount.get();
    }

    public int getDoneCount() {
        return doneCount.get();
    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }
}
