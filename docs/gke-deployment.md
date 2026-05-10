# Phase 6 — GKE Deployment Guide

This phase adds a practical Kubernetes packaging layer on top of the existing Docker, Pub/Sub, and Terraform work.

## What was added

- A reusable Helm chart in `infra/helm/cloudops-platform`
- A raw manifest example in `infra/k8s/examples/cloudops-gke.yaml`
- Environment-specific Helm values for local Kubernetes and GKE

## How it maps to your current platform

- Terraform already provisions the GKE cluster in `terraform/modules/gke`
- Terraform already provisions the Pub/Sub topic and subscriptions in `terraform/modules/pubsub`
- The new Helm chart deploys the five application workloads into the cluster
- The GKE values file is prepared for Workload Identity annotations per service account

## Recommended deploy flow

1. Apply Terraform to provision the cluster and Pub/Sub resources.
2. Build and push service images with the GitHub Actions Docker pipeline.
3. Create a Kubernetes secret for JWT if you are not letting Helm create one.
4. Install or upgrade the chart using `values-gke.yaml`.
5. Expose `gateway-service` through the chart's ingress.

## Connect kubectl to the Terraform cluster

```bash
gcloud container clusters get-credentials YOUR_CLUSTER_NAME \
  --region YOUR_REGION \
  --project YOUR_PROJECT_ID
```

You can get the cluster name and endpoint from Terraform outputs:

```bash
cd terraform
terraform output gke_cluster_name
terraform output gke_cluster_endpoint
```

## Create the JWT secret in GKE

```bash
kubectl create namespace cloudops-dev --dry-run=client -o yaml | kubectl apply -f -
kubectl -n cloudops-dev create secret generic cloudops-jwt \
  --from-literal=JWT_SECRET='replace-with-a-real-secret' \
  --dry-run=client -o yaml | kubectl apply -f -
```

## Deploy with Helm

```bash
helm upgrade --install cloudops ./infra/helm/cloudops-platform \
  --namespace cloudops-dev \
  --create-namespace \
  -f ./infra/helm/cloudops-platform/values.yaml \
  -f ./infra/helm/cloudops-platform/values-gke.yaml \
  --set imageDefaults.registry=us-central1-docker.pkg.dev/YOUR_PROJECT/cloudops \
  --set imageDefaults.tag=YOUR_TAG \
  --set services.task-service.env.GCP_PROJECT_ID=YOUR_PROJECT \
  --set services.analytics-service.env.GCP_PROJECT_ID=YOUR_PROJECT \
  --set services.notification-service.env.GCP_PROJECT_ID=YOUR_PROJECT
```

## Local Kubernetes learning flow

Use `kind`, `minikube`, or Docker Desktop Kubernetes to practice the same topology with a local Pub/Sub emulator.

```bash
helm upgrade --install cloudops ./infra/helm/cloudops-platform \
  --namespace cloudops-local \
  --create-namespace \
  -f ./infra/helm/cloudops-platform/values.yaml \
  -f ./infra/helm/cloudops-platform/values-local.yaml \
  --set imageDefaults.registry=YOUR_LOCAL_REGISTRY_OR_REMOTE \
  --set imageDefaults.tag=latest
```

The local chart also runs a `pubsub-bootstrap` Kubernetes `Job` to create `task-events`, `analytics-sub`, and `notification-sub` inside the emulator.

## Useful checks

```bash
kubectl get pods -n cloudops-dev
kubectl get svc -n cloudops-dev
kubectl describe ingress gateway-service -n cloudops-dev
kubectl logs deploy/task-service -n cloudops-dev
kubectl logs deploy/analytics-service -n cloudops-dev
```

## Next logical Phase 6 additions

- HorizontalPodAutoscaler objects per service
- Config Connector or External Secrets for GCP-native secret sync
- OpenTelemetry collector sidecar or daemonset
- Managed Redis / Memorystore integration instead of in-cluster Redis
