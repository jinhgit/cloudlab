# Docker Compose (Step 5)

로컬/단일 호스트에서 CloudLab 핵심 스택을 기동한다.

## Services

| Service | Image / Build | Host port | Role |
|---------|---------------|-----------|------|
| `postgres` | `postgres:16-alpine` | 5432 | Platform DB |
| `redis` | `redis:7-alpine` | 6379 | Cache / session |
| `backend` | `docker/Dockerfile.backend` | 8080 | Platform API |
| `frontend` | `docker/Dockerfile.frontend` | 3000 | Dashboard |

Network: `cloudlab-net`  
Volumes: `cloudlab_pg_data`, `cloudlab_redis_data`

## Quick start

```bash
# repo root
cp .env.example .env   # first time
./scripts/compose-up.sh
# or
docker compose up -d --build
```

Stop:

```bash
./scripts/compose-down.sh
# wipe data:
./scripts/compose-down.sh -v
```

## Verify

```bash
docker compose ps
curl -s http://localhost:8080/actuator/health
curl -s http://localhost:8080/api/health
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:3000/
```

Browser: http://localhost:3000  
Header badge should show **API UP** when backend is healthy.

## Backend profile

Compose sets `SPRING_PROFILES_ACTIVE=compose` → `application-compose.yml`

- PostgreSQL + Redis **enabled**
- `ddl-auto: update` (entities 추가 전에도 기동 가능)
- `CLOUDLAB_WAIT_FOR=postgres:5432 redis:6379` entrypoint 대기

`local` 프로파일(IDE `bootRun`)은 여전히 DB 없이 동작한다.

## Frontend build args

Browser calls host-mapped ports, **not** Docker DNS names:

| Arg | Default |
|-----|---------|
| `NEXT_PUBLIC_API_URL` | `http://localhost:8080` |
| `NEXT_PUBLIC_WS_URL` | `ws://localhost:8080/ws` |

Change via `.env` before `docker compose build frontend`.

## Design notes

1. Backend never exposes docker.sock in Step 5 (optional later for Docker page).
2. Secrets stay in `.env` (gitignored).
3. Healthchecks + `depends_on: condition: service_healthy` order the boot.
4. Observability stack (Prometheus/Loki) arrives in Steps 7–8 — not in this compose yet.

## Troubleshooting

| Symptom | Check |
|---------|--------|
| backend unhealthy | `docker compose logs backend` · DB password · wait_for |
| frontend 빌드 실패 | `frontend/package-lock.json`, Node 22, `npm run build` 로컬 재현 |
| API Offline on UI | `NEXT_PUBLIC_API_URL`, CORS, backend health |
| port in use | `.env` 의 `SERVER_PORT` / `FRONTEND_PORT` / `POSTGRES_PORT` 변경 |
