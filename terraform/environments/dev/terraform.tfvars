# terraform/environments/dev/terraform.tfvars
# Replace the values below with your actual GCP project details before running

project_id            = "your-gcp-project-id-dev"
region                = "us-central1"
env                   = "dev"
image_tag             = "latest"
artifact_registry_url = "us-central1-docker.pkg.dev/your-gcp-project-id-dev/cloudops"
gke_node_count        = 1
gke_machine_type      = "e2-standard-2"

# jwt_secret is sensitive — pass via env var:
#   export TF_VAR_jwt_secret="your-super-secret-key-min-32-chars"
