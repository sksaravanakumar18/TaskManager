resource "google_secret_manager_secret" "jwt_secret" {
  secret_id = "cloudops-${var.env}-jwt-secret"
  project   = var.project_id

  replication {
    auto {}
  }

  labels = { env = var.env }
}

resource "google_secret_manager_secret_version" "jwt_secret_v1" {
  secret      = google_secret_manager_secret.jwt_secret.id
  secret_data = var.jwt_secret
}
