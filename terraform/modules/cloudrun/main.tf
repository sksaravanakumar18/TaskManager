locals {
  image_base = var.artifact_registry_url
  tag        = var.image_tag
}

# ── Service Accounts ──────────────────────────────────────────────────────────
resource "google_service_account" "task" {
  account_id   = "cloudops-${var.env}-task-sa"
  display_name = "CloudOps ${var.env} task-service SA"
  project      = var.project_id
}

resource "google_service_account" "auth" {
  account_id   = "cloudops-${var.env}-auth-sa"
  display_name = "CloudOps ${var.env} auth-service SA"
  project      = var.project_id
}

resource "google_service_account" "gateway" {
  account_id   = "cloudops-${var.env}-gateway-sa"
  display_name = "CloudOps ${var.env} gateway-service SA"
  project      = var.project_id
}

resource "google_service_account" "analytics" {
  account_id   = "cloudops-${var.env}-analytics-sa"
  display_name = "CloudOps ${var.env} analytics-service SA"
  project      = var.project_id
}

resource "google_service_account" "notification" {
  account_id   = "cloudops-${var.env}-notification-sa"
  display_name = "CloudOps ${var.env} notification-service SA"
  project      = var.project_id
}

# ── auth-service ──────────────────────────────────────────────────────────────
resource "google_cloud_run_v2_service" "auth" {
  name     = "cloudops-${var.env}-auth"
  location = var.region
  project  = var.project_id

  template {
    service_account = google_service_account.auth.email

    scaling {
      min_instance_count = var.env == "prod" ? 1 : 0
      max_instance_count = var.env == "prod" ? 20 : 5
    }

    containers {
      image = "${local.image_base}/auth-service:${local.tag}"
      ports { container_port = 8082 }

      env {
        name = "JWT_SECRET"
        value_source {
          secret_key_ref {
            secret  = var.jwt_secret_id
            version = "latest"
          }
        }
      }

      resources {
        limits = {
          cpu    = "1"
          memory = "512Mi"
        }
      }

      startup_probe {
        http_get { path = "/actuator/health" }
        initial_delay_seconds = 10
        period_seconds        = 5
        failure_threshold     = 6
      }

      liveness_probe {
        http_get { path = "/actuator/health" }
        period_seconds    = 15
        failure_threshold = 3
      }
    }
  }

  labels = { env = var.env, service = "auth" }
}

# ── task-service ──────────────────────────────────────────────────────────────
resource "google_cloud_run_v2_service" "task" {
  name     = "cloudops-${var.env}-task"
  location = var.region
  project  = var.project_id

  template {
    service_account = google_service_account.task.email

    scaling {
      min_instance_count = var.env == "prod" ? 1 : 0
      max_instance_count = var.env == "prod" ? 20 : 5
    }

    containers {
      image = "${local.image_base}/task-service:${local.tag}"
      ports { container_port = 8081 }

      env {
        name  = "PUBSUB_TOPIC_TASK_EVENTS"
        value = var.pubsub_topic
      }
      env {
        name  = "GCP_PROJECT_ID"
        value = var.project_id
      }

      resources {
        limits = {
          cpu    = "1"
          memory = "512Mi"
        }
      }

      startup_probe {
        http_get { path = "/actuator/health" }
        initial_delay_seconds = 10
        period_seconds        = 5
        failure_threshold     = 6
      }

      liveness_probe {
        http_get { path = "/actuator/health" }
        period_seconds    = 15
        failure_threshold = 3
      }
    }
  }

  labels = { env = var.env, service = "task" }
}

# ── analytics-service ─────────────────────────────────────────────────────────
resource "google_cloud_run_v2_service" "analytics" {
  name     = "cloudops-${var.env}-analytics"
  location = var.region
  project  = var.project_id

  template {
    service_account = google_service_account.analytics.email

    scaling {
      min_instance_count = var.env == "prod" ? 1 : 0
      max_instance_count = var.env == "prod" ? 10 : 3
    }

    containers {
      image = "${local.image_base}/analytics-service:${local.tag}"
      ports { container_port = 8083 }

      env {
        name  = "GCP_PROJECT_ID"
        value = var.project_id
      }

      resources {
        limits = {
          cpu    = "1"
          memory = "512Mi"
        }
      }

      startup_probe {
        http_get { path = "/actuator/health" }
        initial_delay_seconds = 10
        period_seconds        = 5
        failure_threshold     = 6
      }

      liveness_probe {
        http_get { path = "/actuator/health" }
        period_seconds    = 15
        failure_threshold = 3
      }
    }
  }

  labels = { env = var.env, service = "analytics" }
}

# ── notification-service ──────────────────────────────────────────────────────
resource "google_cloud_run_v2_service" "notification" {
  name     = "cloudops-${var.env}-notification"
  location = var.region
  project  = var.project_id

  template {
    service_account = google_service_account.notification.email

    scaling {
      min_instance_count = var.env == "prod" ? 1 : 0
      max_instance_count = var.env == "prod" ? 10 : 3
    }

    containers {
      image = "${local.image_base}/notification-service:${local.tag}"
      ports { container_port = 8084 }

      env {
        name  = "GCP_PROJECT_ID"
        value = var.project_id
      }

      resources {
        limits = {
          cpu    = "1"
          memory = "512Mi"
        }
      }

      startup_probe {
        http_get { path = "/actuator/health" }
        initial_delay_seconds = 10
        period_seconds        = 5
        failure_threshold     = 6
      }

      liveness_probe {
        http_get { path = "/actuator/health" }
        period_seconds    = 15
        failure_threshold = 3
      }
    }
  }

  labels = { env = var.env, service = "notification" }
}

# ── gateway-service (public-facing ingress) ───────────────────────────────────
resource "google_cloud_run_v2_service" "gateway" {
  name     = "cloudops-${var.env}-gateway"
  location = var.region
  project  = var.project_id

  template {
    service_account = google_service_account.gateway.email

    scaling {
      min_instance_count = var.env == "prod" ? 1 : 0
      max_instance_count = var.env == "prod" ? 20 : 5
    }

    containers {
      image = "${local.image_base}/gateway-service:${local.tag}"
      ports { container_port = 8080 }

      env {
        name  = "AUTH_SERVICE_URL"
        value = google_cloud_run_v2_service.auth.uri
      }
      env {
        name  = "TASK_SERVICE_URL"
        value = google_cloud_run_v2_service.task.uri
      }
      env {
        name = "JWT_SECRET"
        value_source {
          secret_key_ref {
            secret  = var.jwt_secret_id
            version = "latest"
          }
        }
      }

      resources {
        limits = {
          cpu    = "1"
          memory = "512Mi"
        }
      }

      startup_probe {
        http_get { path = "/actuator/health" }
        initial_delay_seconds = 10
        period_seconds        = 5
        failure_threshold     = 6
      }

      liveness_probe {
        http_get { path = "/actuator/health" }
        period_seconds    = 15
        failure_threshold = 3
      }
    }
  }

  labels = { env = var.env, service = "gateway" }
}

# ── Allow unauthenticated requests to gateway only ────────────────────────────
resource "google_cloud_run_v2_service_iam_member" "gateway_public" {
  project  = var.project_id
  location = var.region
  name     = google_cloud_run_v2_service.gateway.name
  role     = "roles/run.invoker"
  member   = "allUsers"
}
