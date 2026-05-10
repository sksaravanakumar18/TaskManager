output "gke_cluster_name" {
  description = "Name of the GKE cluster"
  value       = module.gke.cluster_name
}

output "gke_cluster_endpoint" {
  description = "GKE cluster API endpoint"
  value       = module.gke.cluster_endpoint
  sensitive   = true
}

output "pubsub_topic_task_events" {
  description = "Full resource name of the task-events Pub/Sub topic"
  value       = module.pubsub.topic_task_events
}

output "cloud_run_gateway_url" {
  description = "Public URL of the gateway Cloud Run service"
  value       = module.cloudrun.gateway_url
}

output "cloud_run_task_url" {
  description = "URL of the task-service Cloud Run service"
  value       = module.cloudrun.task_service_url
}

output "cloud_run_analytics_url" {
  description = "URL of the analytics-service Cloud Run service"
  value       = module.cloudrun.analytics_url
}

output "cloud_run_notification_url" {
  description = "URL of the notification-service Cloud Run service"
  value       = module.cloudrun.notification_url
}

output "jwt_secret_id" {
  description = "Secret Manager secret ID for JWT signing key"
  value       = module.secrets.jwt_secret_id
}

output "wif_pool_name" {
  description = "Workload Identity Pool name"
  value       = module.iam.wif_pool_name
}

output "wif_provider_name" {
  description = "Workload Identity Provider name"
  value       = module.iam.wif_provider_name
}

output "github_sa_email" {
  description = "GitHub Actions service account email"
  value       = module.iam.github_sa_email
}
