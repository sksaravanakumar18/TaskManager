# auth-service

JWT token issuance and validation service for local Phase 2 security flow.

## Run

```powershell
mvn spring-boot:run
```

Default URL: `http://localhost:8082`

## Test

```powershell
mvn test
```

## Endpoints

- `POST /api/auth/token`
- `GET /api/auth/validate`

## Example

```powershell
$body = '{"username":"ravi","roles":["ADMIN","USER"]}'
$token = (Invoke-RestMethod -Method Post -Uri "http://localhost:8082/api/auth/token" -ContentType "application/json" -Body $body).token
Invoke-RestMethod -Uri "http://localhost:8082/api/auth/validate" -Headers @{ Authorization = "Bearer $token" }
```
