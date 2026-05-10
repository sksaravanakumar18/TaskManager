variable "project_id" {
  description = "GCP project ID"
  type        = string
}

variable "region" {
  description = "GCP region for all resources"
  type        = string
  default     = "us-central1"
}

variable "env" {
  description = "Deployment environment (dev | staging | prod)"
  type        = string
  default     = "dev"

  validation {
    condition     = contains(["dev", "staging", "prod"], var.env)
    error_message = "env must be one of: dev, staging, prod"
  }
}

variable "image_tag" {
  description = "Docker image tag to deploy (e.g. git SHA or semver)"
  type        = string
  default     = "latest"
}

variable "artifact_registry_url" {
  description = "Artifact Registry base URL, e.g. us-central1-docker.pkg.dev/my-project/cloudops"
  type        = string
}

variable "jwt_secret" {
  description = "Secret value for JWT signing key (stored in Secret Manager)"
  type        = string
  sensitive   = true
}

variable "gke_node_count" {
  description = "Initial number of nodes per zone in the GKE node pool"
  type        = number
  default     = 1
}

variable "gke_machine_type" {
  description = "Machine type for GKE nodes"
  type        = string
  default     = "e2-standard-2"
}
