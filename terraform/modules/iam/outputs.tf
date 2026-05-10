output "wif_pool_name" {
  description = "Workload Identity Pool name"
  value       = google_iam_workload_identity_pool.github_pool.name
}

output "wif_provider_name" {
  description = "Workload Identity Provider name"
  value       = google_iam_workload_identity_pool_provider.github_provider.name
}

output "github_sa_email" {
  description = "GitHub Actions service account email"
  value       = google_service_account.github_sa.email
}