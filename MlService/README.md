# MlService

Spring Boot service in the BDSandbox repository.

## Build and run

```bash
cd MlService
mvn spring-boot:run
```

Service listens on **port 8081**. Health check: `GET http://localhost:8081/api/health`

## Structure

- `src/main/java/com/xod/bdsb/ml/` — main class and packages
- `src/main/resources/application.properties` — server port and app name
