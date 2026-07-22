# Docker 설계 (Step 2)

CloudLab 애플리케이션 이미지 규약. Compose 오케스트레이션은 Step 5, k3s 배포는 Step 6.

## 이미지 목록

| Image | Dockerfile | Base (runtime) | Port | User |
|-------|------------|----------------|------|------|
| `cloudlab-backend` | `docker/Dockerfile.backend` | `eclipse-temurin:21-jre-jammy` | 8080 | `cloudlab` (uid 10001) |
| `cloudlab-frontend` | `docker/Dockerfile.frontend` | `node:22-alpine` | 3000 | `cloudlab` (uid 10001) |

## 빌드 컨텍스트

**항상 저장소 루트**를 컨텍스트로 사용한다.

```bash
# Backend
docker build -f docker/Dockerfile.backend -t cloudlab-backend:local .

# Frontend (build-time public env)
docker build -f docker/Dockerfile.frontend -t cloudlab-frontend:local \
  --build-arg NEXT_PUBLIC_API_URL=http://localhost:8080 \
  --build-arg NEXT_PUBLIC_WS_URL=ws://localhost:8080/ws .
```

헬퍼: `scripts/docker-build.sh`

## 설계 결정

| 결정 | 내용 | 이유 |
|------|------|------|
| Multi-stage | builder + runtime 분리 | 이미지 크기 · 공격면 감소 |
| Non-root | uid/gid **10001** | k8s `runAsNonRoot` 정합 |
| Backend JRE | JDK로 빌드, JRE로 실행 | 런타임 슬림 |
| Frontend standalone | Next.js `output: 'standalone'` | node_modules 전체 불필요 |
| HEALTHCHECK | 컨테이너 자체 헬스 | Compose/k8s 프로브와 일관 |
| 시크릿 | 이미지에 넣지 않음 | 런타임 env / 마운트 |
| docker.sock | **runtime mount only** (API) | 프론트 이미지에 소켓 없음 |

## Backend 런타임 env (대표)

| Variable | Default | 설명 |
|----------|---------|------|
| `JAVA_OPTS` | container-aware heap | JVM 튜닝 |
| `SERVER_PORT` | 8080 | HTTP |
| `SPRING_PROFILES_ACTIVE` | prod | 프로파일 |
| `CLOUDLAB_WAIT_FOR` | (empty) | `host:port` 대기 (Compose용) |

## Frontend 빌드 arg

| Arg | 설명 |
|-----|------|
| `NEXT_PUBLIC_API_URL` | 브라우저 → API base |
| `NEXT_PUBLIC_WS_URL` | 브라우저 → WebSocket |

> `NEXT_PUBLIC_*` 는 **빌드 타임**에 번들된다. 환경별 이미지가 달라질 수 있음.

## 보안 메모

1. 브라우저는 Docker Engine에 직접 붙지 않는다 (PRD).
2. `cloudlab-backend` 만 (선택) `/var/run/docker.sock` 마운트.
3. kubeconfig는 읽기 전용 마운트 권장.
4. Root 파일시스템에 시크릿 베이크 금지.

## Step 의존성

| Step | 필요 산출물 | 이미지 빌드 |
|------|-------------|-------------|
| 2 (현재) | Dockerfile · entrypoint · ignore | 초안 완료, **앱 소스 전 빌드 실패 정상** |
| 3 | `backend/` Spring Boot + Gradle Wrapper | `cloudlab-backend` 빌드 가능 |
| 4 | `frontend/` Next.js + `output: 'standalone'` | `cloudlab-frontend` 빌드 가능 |
| 5 | Compose | 로컬 스택 기동 → 완료 (`docker-compose.yml`, [compose.md](./compose.md)) |

## 태그 전략 (CI, Step 9)

- `git` short SHA: `cloudlab-backend:a1b2c3d`
- `main` 추가 태그: `latest`
