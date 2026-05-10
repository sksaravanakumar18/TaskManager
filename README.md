# CloudOps Playground Platform

A cloud-native learning platform designed to maximize **GCP architecture hands-on** with minimal business complexity.

## What is implemented now (Phase 6)

- Five Spring Boot 3.3.5 microservices on Java 21
- JWT auth + API gateway + cached task-service
- Shared event library with local in-memory bus and GCP Pub/Sub adapters
- Analytics + notification consumers for task domain events
- Dockerfiles + full `docker-compose.yml` stack with Redis and Pub/Sub emulator
- Terraform for GCP networking, GKE, Cloud Run, Pub/Sub, IAM, and Secret Manager
- GitHub Actions for Terraform and Docker build/push flows
- Helm chart + raw Kubernetes manifests for GKE deployment practice
- Service tests passing across all five services

## Repository Structure

```text
backend/
  auth-service/
  analytics-service/
  gateway-service/
  notification-service/
  shared-events/
  task-service/
infra/
  helm/
    cloudops-platform/
  k8s/
    examples/
terraform/
  modules/
    networking/
    gke/
    cloudrun/
    pubsub/
    iam/
    secrets/
docs/
  architecture.md
  gke-deployment.md
.github/workflows/
  docker-build.yml
  terraform.yml
```

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.9+

### Run tests

```powershell
cd "c:\Users\rsaravanakumar\Documents\Practice\Task Manager\backend\auth-service"
mvn test

cd "c:\Users\rsaravanakumar\Documents\Practice\Task Manager\backend\analytics-service"
mvn test

cd "c:\Users\rsaravanakumar\Documents\Practice\Task Manager\backend\gateway-service"
mvn test

cd "c:\Users\rsaravanakumar\Documents\Practice\Task Manager\backend\notification-service"
mvn test

cd "c:\Users\rsaravanakumar\Documents\Practice\Task Manager\backend\task-service"
mvn test
```

### Run the local Docker stack

```powershell
cd "c:\Users\rsaravanakumar\Documents\Practice\Task Manager"
docker compose up --build
```

### Deploy to Kubernetes

See `docs/gke-deployment.md` and `infra/helm/cloudops-platform/README.md`.

### Run services manually (five terminals)

```powershell
cd "c:\Users\rsaravanakumar\Documents\Practice\Task Manager\backend\auth-service"
mvn spring-boot:run
```

```powershell
cd "c:\Users\rsaravanakumar\Documents\Practice\Task Manager\backend\task-service"
mvn spring-boot:run
```

```powershell
cd "c:\Users\rsaravanakumar\Documents\Practice\Task Manager\backend\analytics-service"
mvn spring-boot:run
```

```powershell
cd "c:\Users\rsaravanakumar\Documents\Practice\Task Manager\backend\notification-service"
mvn spring-boot:run
```

```powershell
cd "c:\Users\rsaravanakumar\Documents\Practice\Task Manager\backend\gateway-service"
mvn spring-boot:run
```

URLs:

- Gateway: `http://localhost:8080`
- Task Service: `http://localhost:8081`
- Auth Service: `http://localhost:8082`
- Analytics Service: `http://localhost:8083`
- Notification Service: `http://localhost:8084`

## API Endpoints

Auth:

- `POST /api/auth/token`
- `GET /api/auth/validate`

Task APIs (through gateway):

- `POST /api/tasks`
- `GET /api/tasks`
- `GET /api/tasks/{id}`
- `PUT /api/tasks/{id}`
- `PATCH /api/tasks/{id}/status?status=IN_PROGRESS`
- `DELETE /api/tasks/{id}`

## Example (PowerShell)

```powershell
$body = '{"username":"ravi","roles":["ADMIN","USER"]}'
$token = (Invoke-RestMethod -Method Post -Uri "http://localhost:8082/api/auth/token" -ContentType "application/json" -Body $body).token

$task = '{"title":"Prepare GCP deck","description":"Interview prep","assignee":"Ravi","status":"TODO"}'
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/tasks" -ContentType "application/json" -Headers @{ Authorization = "Bearer $token" } -Body $task

Invoke-RestMethod -Uri "http://localhost:8080/api/tasks?assignee=Ravi&status=TODO&query=GCP" -Headers @{ Authorization = "Bearer $token" }
```

## Next Phases

See `docs/architecture.md` for the broader roadmap, and use `docs/gke-deployment.md` for the new Kubernetes phase.

Good next hands-on extensions:

- GKE autoscaling with HPAs and PDBs
- Managed Redis / Memorystore integration
- Dataflow to BigQuery pipeline
- Vertex AI prediction service
- IAM + Workload Identity hardening
- Observability with OpenTelemetry + Prometheus + Grafana
- Progressive delivery with Argo Rollouts or Flagger
