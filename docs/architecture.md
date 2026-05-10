# CloudOps Playground – Architecture & Learning Roadmap

## Product Scope

Small product, rich infrastructure:

1. **Task Manager page**
   - Create/update tasks
   - Assign status
   - Filter/search
2. **Analytics Dashboard page**
   - Task trends
   - Event metrics
   - AI predictions
   - Logs/alerts overview

## Target Microservices

- `gateway-service`
- `auth-service`
- `task-service`
- `analytics-service`
- `ai-service`
- `notification-service`
- `config-service`

## GCP Mapping (Concept → Implementation)

### Compute

- `gateway-service` on **Cloud Run**
- Core services on **GKE**
- Learn: autoscaling, rolling updates, serverless vs containers

### Networking

- Custom **VPC**, subnets, firewall rules
- Global HTTP(S) Load Balancer + SSL certs
- Cloud DNS (+ optional CDN for frontend)

### Security

- OAuth2/JWT for app identity
- IAM least privilege with dedicated service accounts
- Secret Manager for credentials/keys
- Cloud Armor (WAF) + API rate limiting

### Data

- Cloud SQL (OLTP)
- BigQuery (analytics)
- Cloud Storage (exports/reports)
- Redis (caching)
- OpenSearch/Elasticsearch (search/log indexing)

### Messaging

- Publish `task-created` / `task-updated` on Pub/Sub
- Subscribers: analytics, notification, ai
- Optional Kafka sidecar for comparison experiment

### AI/ML

- Vertex AI endpoint for:
  - completion-time prediction
  - priority suggestion
  - anomaly detection

### Data Engineering

- Streaming: Pub/Sub → Dataflow → BigQuery
- Batch: Dataproc ETL

### Observability

- OpenTelemetry traces/metrics
- Prometheus + Grafana dashboards
- GCP Logging & alerting policies

### DevOps / IaC

- GitHub Actions: build/test/image/deploy checks
- Terraform modules: network, gke, cloudrun, pubsub, iam, secrets, monitoring

## Suggested Delivery Phases

### Phase 1 (implemented now)

- Monorepo starter
- Runnable `task-service`
- CRUD + filter/search endpoints
- Tests + local run instructions

### Phase 2

- Add `gateway-service` and `auth-service`
- JWT auth and route protection
- Redis cache for task reads

### Phase 3

- Add Pub/Sub events from `task-service`
- Add `analytics-service` consumer + metrics endpoint
- Add `notification-service` consumer

### Phase 4

- Provision GCP infra with Terraform modules
- Deploy gateway to Cloud Run
- Deploy services to GKE

### Phase 5

- Add Dataflow pipeline to BigQuery
- Add dashboard queries
- Add Vertex AI endpoint integration in `ai-service`

### Phase 6

- Full observability stack + SLO dashboards
- Load tests (JMeter) and autoscaling validation
- Cost and resilience tuning

## Interview Talking Points You’ll Get

- Cloud Run vs GKE trade-offs
- Event-driven architecture with Pub/Sub
- IAM boundaries and secrets strategy
- Service-to-service auth patterns
- Distributed tracing and incident diagnosis
- Terraform module design and CI/CD gates
- Reliability and cost optimization decisions
