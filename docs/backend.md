# Backend — Spring Boot Platform API (Step 3)

## Overview

| Item | Value |
|------|--------|
| Module | `backend/` |
| Artifact | `cloudlab-backend` |
| Package | `com.cloudlab` |
| Java | 21 (toolchain) |
| Spring Boot | 3.5.x |
| Default profile | `local` |

## Responsibilities

Single **control-plane API** that will adapt Docker, k3s, Prometheus, Loki, Alertmanager, and GitHub Actions for the Dashboard (PRD).

Step 3 delivers a **bootable skeleton** only:

- Application entrypoint
- Actuator health / info / prometheus endpoint exposure
- Common `ApiResponse` envelope
- Security skeleton (public health + platform info)
- Local profile without PostgreSQL/Redis hard dependency

## Profiles

| Profile | DB / Redis | Use |
|---------|------------|-----|
| `local` (default) | Auto-config **excluded** | Laptop dev without Compose |
| `prod` | PostgreSQL + Redis required | Docker / k3s runtime |

## Public endpoints (Step 3)

| Method | Path | Auth |
|--------|------|------|
| GET | `/actuator/health` | public |
| GET | `/actuator/info` | public |
| GET | `/actuator/prometheus` | public |
| GET | `/api/health` | public |
| GET | `/api/platform/info` | public |

Other `/api/**` routes require authentication (JWT in a later slice).

## Package layout

```text
com.cloudlab
├── CloudLabApplication
├── common/          # ApiResponse, exception handler
├── config/          # Security, (future) WebSocket, Jackson
├── controller/      # REST
├── service/         # business + adapters (later)
├── repository/      # JPA (later)
├── websocket/       # realtime (later)
└── monitoring/      # metrics helpers (later)
```

## Run locally

```bash
export JAVA_HOME="$(/usr/libexec/java_home -v 21)"
cd backend
./gradlew bootRun
# or
./gradlew test
curl -s http://localhost:8080/actuator/health
curl -s http://localhost:8080/api/health
```

## Docker

Image build uses `docker/Dockerfile.backend` (repo root context). Requires this module + Gradle Wrapper.

```bash
./scripts/docker-build.sh backend local
```

## Next (not Step 3)

- JWT login (`ADMIN` / `VIEWER`)
- Docker / Kubernetes / Prometheus adapters
- WebSocket channels
- JPA entities + Redis session when Compose is up
