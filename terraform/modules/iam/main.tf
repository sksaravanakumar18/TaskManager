# ── task-service: publish to task-events topic ───────────────────────────────
resource "google_pubsub_topic_iam_member" "task_publisher" {
  project = var.project_id
  topic   = var.pubsub_topic_task_events
  role    = "roles/pubsub.publisher"
  member  = "serviceAccount:${var.task_sa_email}"
}

# ── analytics-service: subscribe to task-events ───────────────────────────────
resource "google_project_iam_member" "analytics_subscriber" {
  project = var.project_id
  role    = "roles/pubsub.subscriber"
  member  = "serviceAccount:${var.analytics_sa_email}"
}

resource "google_project_iam_member" "analytics_viewer" {
  project = var.project_id
  role    = "roles/pubsub.viewer"
  member  = "serviceAccount:${var.analytics_sa_email}"
}

# ── notification-service: subscribe to task-events ────────────────────────────
resource "google_project_iam_member" "notification_subscriber" {
  project = var.project_id
  role    = "roles/pubsub.subscriber"
  member  = "serviceAccount:${var.notification_sa_email}"
}

resource "google_project_iam_member" "notification_viewer" {
  project = var.project_id
  role    = "roles/pubsub.viewer"
  member  = "serviceAccount:${var.notification_sa_email}"
}

# ── All services: read secrets ────────────────────────────────────────────────
locals {
  app_service_accounts = [
    var.task_sa_email,
    var.analytics_sa_email,
    var.notification_sa_email,
  ]
}

resource "google_project_iam_member" "secret_accessor" {
  for_each = toset(local.app_service_accounts)
  project  = var.project_id
  role     = "roles/secretmanager.secretAccessor"
  member   = "serviceAccount:${each.value}"
}

# ── Cloud Logging ─────────────────────────────────────────────────────────────
resource "google_project_iam_member" "log_writer" {
  for_each = toset(local.app_service_accounts)
  project  = var.project_id
  role     = "roles/logging.logWriter"
  member   = "serviceAccount:${each.value}"
}
