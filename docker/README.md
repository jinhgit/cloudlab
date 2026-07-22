# docker/

CloudLab 애플리케이션 Dockerfile 및 컨테이너 엔트리포인트.

| File | 설명 |
|------|------|
| `Dockerfile.backend` | Spring Boot Platform API (Java 21, multi-stage) |
| `Dockerfile.frontend` | Next.js Dashboard (Node 22, standalone) |
| `scripts/backend-entrypoint.sh` | `JAVA_OPTS` 주입 · 의존 서비스 대기 |

설계 문서: [docs/docker.md](../docs/docker.md)

## 빌드

저장소 **루트**에서 실행:

```bash
# 헬퍼
./scripts/docker-build.sh backend local
./scripts/docker-build.sh frontend local

# 또는 직접
docker build -f docker/Dockerfile.backend -t cloudlab-backend:local .
docker build -f docker/Dockerfile.frontend -t cloudlab-frontend:local .
```

## Step 상태

- **Step 2 (done):** Dockerfile 초안 · ignore · 문서
- **Step 3 이후:** backend 소스 존재 시 `cloudlab-backend` 빌드 성공
- **Step 4 이후:** frontend 소스 + `output: 'standalone'` 시 `cloudlab-frontend` 빌드 성공
- **Step 5 (done):** Docker Compose 스택 — 루트 `docker-compose.yml` · [docs/compose.md](../docs/compose.md)

```bash
# repo root
./scripts/compose-up.sh
docker compose ps
curl -s http://localhost:8080/actuator/health
```
