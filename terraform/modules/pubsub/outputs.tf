output "topic_task_events"           { value = google_pubsub_topic.task_events.id }
output "subscription_analytics"      { value = google_pubsub_subscription.analytics.id }
output "subscription_notification"   { value = google_pubsub_subscription.notification.id }
output "dead_letter_topic"           { value = google_pubsub_topic.task_events_dead_letter.id }
