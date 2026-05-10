package com.cloudops.notificationservice.controller;

import com.cloudops.notificationservice.consumer.TaskEventNotifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final TaskEventNotifier notifier;

    public NotificationController(TaskEventNotifier notifier) {
        this.notifier = notifier;
    }

    @GetMapping
    public List<String> getNotifications() {
        return notifier.getNotifications();
    }
}
