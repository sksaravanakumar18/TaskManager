output "gateway_url"         { value = google_cloud_run_v2_service.gateway.uri }
output "task_service_url"    { value = google_cloud_run_v2_service.task.uri }
output "auth_url"            { value = google_cloud_run_v2_service.auth.uri }
output "analytics_url"       { value = google_cloud_run_v2_service.analytics.uri }
output "notification_url"    { value = google_cloud_run_v2_service.notification.uri }

output "task_sa_email"         { value = google_service_account.task.email }
output "analytics_sa_email"    { value = google_service_account.analytics.email }
output "notification_sa_email" { value = google_service_account.notification.email }
output "gateway_sa_email"      { value = google_service_account.gateway.email }
output "auth_sa_email"         { value = google_service_account.auth.email }
