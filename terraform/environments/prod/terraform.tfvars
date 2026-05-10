# terraform/environments/prod/terraform.tfvars
project_id            = "your-gcp-project-id-prod"
region                = "us-central1"
env                   = "prod"
image_tag             = "REPLACE_WITH_GIT_SHA"
artifact_registry_url = "us-central1-docker.pkg.dev/your-gcp-project-id-prod/cloudops"
gke_node_count        = 2
gke_machine_type      = "e2-standard-4"

# jwt_secret — set via GitHub Actions secret: TF_VAR_jwt_secret
