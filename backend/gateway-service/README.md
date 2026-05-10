# gateway-service

API ingress service for local Phase 2 flow.

It protects `/api/tasks/**` with JWT and routes requests to `task-service`.

## Run

```powershell
mvn spring-boot:run
```

Default URL: `http://localhost:8080`

## Test

```powershell
mvn test
```

## Local Flow

1. Start `auth-service` (`8082`)
2. Start `task-service` (`8081`)
3. Start `gateway-service` (`8080`)
4. Get token from auth, call task APIs via gateway
