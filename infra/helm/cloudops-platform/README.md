# CloudOps Platform Helm Chart

This chart packages the five Spring Boot services for local Kubernetes or GKE:

- `auth-service`
- `gateway-service`
- `task-service`
- `analytics-service`
- `notification-service`

It also supports optional local-only dependencies:

- `redis`
- `pubsub-emulator`

## Values files

- `values.yaml` — shared defaults
- `values-local.yaml` — local cluster with Redis + Pub/Sub emulator
- `values-gke.yaml` — GKE-friendly overrides using real Pub/Sub and an existing JWT secret

## Render locally

```bash
helm template cloudops ./infra/helm/cloudops-platform \
  -f ./infra/helm/cloudops-platform/values.yaml \
  -f ./infra/helm/cloudops-platform/values-local.yaml
```

## Install on GKE

```bash
helm upgrade --install cloudops ./infra/helm/cloudops-platform \
  --namespace cloudops-dev \
  --create-namespace \
  -f ./infra/helm/cloudops-platform/values.yaml \
  -f ./infra/helm/cloudops-platform/values-gke.yaml \
  --set imageDefaults.registry=us-central1-docker.pkg.dev/YOUR_PROJECT/cloudops \
  --set imageDefaults.tag=YOUR_TAG \
  --set jwt.existingSecretName=cloudops-jwt
```

## Notes

- `gateway-service` gets in-cluster URLs for `auth-service` and `task-service` automatically.
- `task-service`, `analytics-service`, and `notification-service` switch to Pub/Sub via `SPRING_PROFILES_ACTIVE=gcp`.
- For local Kubernetes, `task-service` adds the `redis` profile to use the bundled Redis deployment.
- For local Kubernetes, a `pubsub-bootstrap` job creates the emulator topic and subscriptions before the consumers start.
- `auth-service` and `gateway-service` use TCP probes because they do not currently expose Actuator health endpoints.
