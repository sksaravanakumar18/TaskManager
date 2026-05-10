# task-service

Spring Boot service for Task Manager CRUD/search APIs.

Caching is enabled for list/read operations.

- Default local cache: in-memory (`spring.cache.type=simple`)
- Optional Redis cache profile: `redis`

## Run

```powershell
mvn spring-boot:run
```

## Test

```powershell
mvn test
```

## Optional Redis Profile

```powershell
$env:REDIS_HOST="localhost"
$env:REDIS_PORT="6379"
mvn spring-boot:run -Dspring-boot.run.profiles=redis
```
