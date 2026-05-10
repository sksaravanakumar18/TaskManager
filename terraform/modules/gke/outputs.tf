output "cluster_name"     { value = google_container_cluster.primary.name }
output "cluster_endpoint" { value = google_container_cluster.primary.endpoint }
output "node_sa_email"    { value = google_service_account.gke_nodes.email }
