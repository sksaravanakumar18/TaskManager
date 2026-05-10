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

# ── Workload Identity Federation for GitHub Actions ──────────────────────────
resource "google_iam_workload_identity_pool" "github_pool" {
  workload_identity_pool_id = "github-pool"
  display_name              = "GitHub Actions Pool"
  description               = "Workload Identity Pool for GitHub Actions CI/CD"
}

resource "google_iam_workload_identity_pool_provider" "github_provider" {
  workload_identity_pool_id          = google_iam_workload_identity_pool.github_pool.workload_identity_pool_id
  workload_identity_pool_provider_id = "github-provider"
  display_name                       = "GitHub Provider"
  description                        = "OIDC provider for GitHub Actions"
  attribute_mapping = {
    "google.subject"             = "assertion.sub"
    "attribute.actor"            = "assertion.actor"
    "attribute.repository"       = "assertion.repository"
    "attribute.repository_owner" = "assertion.repository_owner"
  }
  oidc {
    issuer_uri = "https://token.actions.githubusercontent.com"
  }
  attribute_condition = "assertion.repository_owner == \"sksaravanakumar18\""
}

# ── Service Account for GitHub Actions ────────────────────────────────────────
resource "google_service_account" "github_sa" {
  account_id   = "github-sa"
  display_name = "GitHub Actions Service Account"
  description  = "Service account for GitHub Actions CI/CD pipelines"
}

# ── IAM binding for GitHub SA ─────────────────────────────────────────────────
resource "google_service_account_iam_member" "github_sa_workload_identity" {
  service_account_id = google_service_account.github_sa.name
  role               = "roles/iam.workloadIdentityUser"
  member             = "principalSet://iam.googleapis.com/${google_iam_workload_identity_pool.github_pool.name}/attribute.repository/sksaravanakumar18/TaskManager"
}

# ── Permissions for GitHub SA ─────────────────────────────────────────────────
resource "google_project_iam_member" "github_sa_editor" {
  project = var.project_id
  role    = "roles/editor"
  member  = "serviceAccount:${google_service_account.github_sa.email}"
}

resource "google_project_iam_member" "github_sa_secret_accessor" {
  project = var.project_id
  role    = "roles/secretmanager.secretAccessor"
  member  = "serviceAccount:${google_service_account.github_sa.email}"
}
