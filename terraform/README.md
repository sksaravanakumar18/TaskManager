# Terraform — CloudOps Playground Infrastructure (GCP)

This directory provisions the full GCP cloud infrastructure for the CloudOps Playground Platform using Terraform.

## Architecture

```
terraform/
├── main.tf              # Root module — wires all child modules
├── variables.tf         # Input variables
├── outputs.tf           # Key resource outputs
├── versions.tf          # Provider + backend config (GCS remote state)
├── .terraformignore
├── environments/
│   ├── dev/terraform.tfvars
│   └── prod/terraform.tfvars
└── modules/
    ├── networking/      # VPC, subnets, Cloud Router, Cloud NAT, Firewall
    ├── gke/             # GKE cluster + node pool + Workload Identity
    ├── pubsub/          # task-events topic + analytics/notification subscriptions
    ├── cloudrun/        # 5 Cloud Run services (gateway, task, auth, analytics, notification)
    ├── iam/             # Pub/Sub publisher/subscriber IAM bindings
    └── secrets/         # Secret Manager — JWT signing key
```

## What Gets Created

| Resource | Details |
|----------|---------|
| **VPC** | Custom mode, private subnet `10.0.0.0/20`, public subnet `10.1.0.0/24` |
| **GKE** | Regional cluster, private nodes, Workload Identity, auto-upgrade |
| **Pub/Sub** | `task-events` topic, analytics + notification pull subscriptions, dead-letter topic |
| **Cloud Run** | 5 services — gateway (public), task, auth, analytics, notification |
| **IAM** | Least-privilege SAs per service; publisher/subscriber roles |
| **Secret Manager** | JWT signing key with automatic replication |

## Prerequisites

1. A GCP project with billing enabled
2. APIs enabled: `container`, `run`, `pubsub`, `secretmanager`, `artifactregistry`
3. A GCS bucket for Terraform state: `gsutil mb gs://cloudops-tfstate-dev`
4. Workload Identity Federation configured for GitHub Actions
5. Artifact Registry repository with Docker images pushed

## Quick Start (Dev)

```bash
# 1. Set sensitive vars
export TF_VAR_jwt_secret="your-super-secret-jwt-key-minimum-32-chars"

# 2. Init with dev backend
cd terraform
terraform init \
  -backend-config="bucket=cloudops-tfstate-dev" \
  -backend-config="prefix=terraform/state/dev"

# 3. Plan
terraform plan -var-file="environments/dev/terraform.tfvars"

# 4. Apply
terraform apply -var-file="environments/dev/terraform.tfvars"
```

## GitHub Actions Secrets Required

| Secret | Description |
|--------|-------------|
| `GCP_WIF_PROVIDER` | Workload Identity Federation provider resource name |
| | Example: `projects/568842450714/locations/global/workloadIdentityPools/github-pool/providers/github-provider` |
| `GCP_SERVICE_ACCOUNT` | SA email used by GitHub Actions |
| `TF_STATE_BUCKET` | GCS bucket name for remote state |
| `JWT_SECRET` | JWT signing key value |

## Module Descriptions

### `networking`
Creates a VPC with private (GKE) and public subnets, Cloud Router, Cloud NAT (outbound internet for private nodes), and firewall rules for internal traffic and GKE health checks.

### `gke`
Provisions a regional GKE Standard cluster with:
- Private nodes (no external IPs)
- Workload Identity (pods authenticate as GCP service accounts)
- Network policy (Calico)
- Auto-repair, auto-upgrade, cluster autoscaler
- Release channel: REGULAR

### `pubsub`
Creates the `task-events` Pub/Sub topic used by task-service (publisher) and consumed by analytics-service and notification-service. Includes dead-letter topic for failed deliveries.

### `cloudrun`
Deploys all 5 microservices as Cloud Run v2 services with:
- Per-service IAM service accounts
- Health check probes (startup + liveness)
- Min/max instance scaling (0→5 dev, 1→20 prod)
- JWT secret injected via Secret Manager

### `iam`
Binds least-privilege IAM roles:
- `task-service` → `roles/pubsub.publisher`
- `analytics-service` → `roles/pubsub.subscriber`
- `notification-service` → `roles/pubsub.subscriber`
- All services → `roles/secretmanager.secretAccessor`, `roles/logging.logWriter`

### `secrets`
Stores the JWT signing key in Secret Manager with automatic multi-region replication.
