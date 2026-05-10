# ── Task Events topic ────────────────────────────────────────────────────────
resource "google_pubsub_topic" "task_events" {
  name    = "cloudops-${var.env}-task-events"
  project = var.project_id

  labels = { env = var.env }

  message_retention_duration = "86600s" # 24 h
}

# Dead-letter topic for undeliverable messages
resource "google_pubsub_topic" "task_events_dead_letter" {
  name    = "cloudops-${var.env}-task-events-dead-letter"
  project = var.project_id
  labels  = { env = var.env }
}

# ── Subscriptions ─────────────────────────────────────────────────────────────

# Analytics service pull subscription
resource "google_pubsub_subscription" "analytics" {
  name    = "cloudops-${var.env}-analytics-sub"
  topic   = google_pubsub_topic.task_events.name
  project = var.project_id

  ack_deadline_seconds       = 30
  message_retention_duration = "604800s" # 7 days
  retain_acked_messages      = false

  expiration_policy { ttl = "" } # never expires

  dead_letter_policy {
    dead_letter_topic     = google_pubsub_topic.task_events_dead_letter.id
    max_delivery_attempts = 5
  }

  retry_policy {
    minimum_backoff = "10s"
    maximum_backoff = "300s"
  }

  labels = { env = var.env, consumer = "analytics" }
}

# Notification service pull subscription
resource "google_pubsub_subscription" "notification" {
  name    = "cloudops-${var.env}-notification-sub"
  topic   = google_pubsub_topic.task_events.name
  project = var.project_id

  ack_deadline_seconds       = 30
  message_retention_duration = "604800s"
  retain_acked_messages      = false

  expiration_policy { ttl = "" }

  dead_letter_policy {
    dead_letter_topic     = google_pubsub_topic.task_events_dead_letter.id
    max_delivery_attempts = 5
  }

  retry_policy {
    minimum_backoff = "10s"
    maximum_backoff = "300s"
  }

  labels = { env = var.env, consumer = "notification" }
}
