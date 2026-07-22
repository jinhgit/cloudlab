# PRD (Product Requirements Document)

# CloudLab

## Self-Hosted DevOps Platform

| Field | Value |
|-------|--------|
| **Version** | 1.0 |
| **Author** | AI + Developer |
| **Status** | Active |
| **Audience** | Platform / DevOps / Cloud Engineer portfolio |
| **Primary Goal** | Build a self-hosted cloud platform that allows monitoring, deployment, logging, infrastructure management, and service operation through a modern web dashboard without using paid cloud services. |

---

## 1. Vision

CloudLab은 **AWS Console처럼 동작하는 개인 DevOps Platform**이다.

사용자는 웹 브라우저 하나만으로 다음을 수행할 수 있어야 한다.

- 서버 상태 확인
- Kubernetes 관리
- Docker 관리
- 서비스 배포
- 로그 조회
- 모니터링
- 장애 확인

웹 서비스가 목적이 아니라 **DevOps 운영 플랫폼 구축이 목적**이다.

### Product Positioning

| 구분 | 설명 |
|------|------|
| **제품 유형** | 실무 기업에서 Platform Engineer가 만드는 **내부 운영 플랫폼** 수준 |
| **포트폴리오 포지션** | 단순 DevOps 실습이 아닌, 운영·관측·배포·복구가 한 화면에서 이어지는 플랫폼 |
| **데모 목표** | 면접 5분 시나리오로 강점 일괄 시연 가능 |

---

## 2. Project Goals

이 프로젝트는 다음 역량을 포트폴리오에서 보여주는 것을 목표로 한다.

| Domain | Skills |
|--------|--------|
| OS / Runtime | Linux, Docker, Docker Compose |
| Orchestration | Kubernetes (k3s), Helm |
| Delivery | GitHub Actions, CI/CD |
| Observability | Monitoring, Logging, Alerting |
| Edge | Reverse Proxy (Nginx), Cloudflare Tunnel |
| Application | Spring Boot, Next.js |
| Platform | Infrastructure, Observability |

### Success Criteria (v1)

1. 단일 Ubuntu 서버(또는 동등 환경)에서 Docker + k3s + 관측 스택이 동작한다.
2. Dashboard에서 서버/컨테이너/Pod/메트릭/로그/알림/배포 상태를 **실데이터**로 조회·조작할 수 있다.
3. GitHub Push → Actions → 이미지 빌드/푸시 → Rolling Update → Health Check → Discord 알림 파이프라인이 동작한다.
4. 면접 데모 시나리오(섹션 26)를 끊김 없이 재현할 수 있다.
5. README에 아키텍처·설치·운영·장애 복구·트러블슈팅이 문서화되어 있다.

---

## 3. High-Level Architecture

```text
                       Internet
                            │
                   Cloudflare Tunnel
                            │
                    Nginx Reverse Proxy
                            │
────────────────────────────────────────────────────
             CloudLab Dashboard (Next.js)
────────────────────────────────────────────────────
               REST API + WebSocket
────────────────────────────────────────────────────
               Spring Boot Platform API
────────────────────────────────────────────────────
 Docker Engine
 Kubernetes(k3s)
 PostgreSQL
 Redis
 Prometheus
 Grafana
 Loki
 Alertmanager
 Node Exporter
 cAdvisor
────────────────────────────────────────────────────
             Ubuntu Server
```

### Component Responsibilities

| Layer | Component | Responsibility |
|-------|-----------|----------------|
| Edge | Cloudflare Tunnel | 공인 IP/포트 노출 없이 HTTPS 진입 |
| Edge | Nginx | 리버스 프록시, 경로 라우팅, TLS 종료(필요 시) |
| UI | Next.js Dashboard | 운영 콘솔 UI, 실시간 상태 표시 |
| API | Spring Boot | Docker/K8s/Prometheus/Loki/Deploy 통합 API |
| Data | PostgreSQL | 플랫폼 메타데이터, 사용자, 감사 로그 |
| Cache | Redis | 세션/캐시/실시간 보조 |
| Runtime | Docker Engine | 컨테이너 실행·조회 |
| Orchestration | k3s | 워크로드 오케스트레이션 |
| Metrics | Prometheus + exporters | 메트릭 수집·저장·질의 |
| Logs | Loki + Promtail | 로그 수집·질의 |
| Alerts | Alertmanager | 알림 라우팅(Discord 등) |
| Viz | Grafana | 보조 시각화(선택 진입) |

---

## 4. Technology Stack

### Frontend

| Tech | Role |
|------|------|
| Next.js | App Router 기반 Dashboard |
| TypeScript | 타입 안전성 |
| TailwindCSS | 스타일링 |
| shadcn/ui | UI 컴포넌트 |
| React Query | 서버 상태 / polling |
| Zustand | 클라이언트 UI 상태 |
| Socket.IO client | 실시간 스트림 |
| Recharts | 메트릭 차트 |

### Backend

| Tech | Role |
|------|------|
| Spring Boot | Platform API |
| Java 21 | Runtime |
| Spring Security + JWT | 인증/인가 |
| Spring Actuator | 앱 메트릭/헬스 |
| WebSocket / Socket.IO 호환 레이어 | 실시간 푸시 |
| JPA | 영속성 |
| Redis | 캐시/세션 |
| PostgreSQL | 주 DB |

### DevOps / Infra

| Tech | Role |
|------|------|
| Docker / Compose | 로컬·호스트 서비스 구성 |
| k3s / Helm | 클러스터·차트 배포 |
| GitHub Actions | CI/CD |
| Nginx | Reverse proxy |
| Cloudflare Tunnel | 외부 접근 |
| Prometheus / Grafana / Loki / Alertmanager | Observability |
| Node Exporter / cAdvisor | 호스트·컨테이너 메트릭 |

### Explicit Non-Goals (v1)

- 멀티 클러스터 / 멀티 테넌시
- 상용 클라우드 관리형 서비스 의존 (EKS, CloudWatch 등)
- 완전한 멀티 유저 SaaS 과금/조직 모델
- Terraform / Ansible 기반 풀 IaC (→ **v2**)

---

## 5. Dashboard UI

### Sidebar (고정 순서)

```text
🏠 Dashboard
☸ Kubernetes
🐳 Docker
🚀 Deployments
📊 Monitoring
📜 Logs
🗄 Database
⚡ Redis
🚨 Alerts
⚙ Settings
```

### UI Rules (AI MUST FOLLOW)

| Rule | Spec |
|------|------|
| Theme | **다크모드 기본** |
| Look & feel | Vercel Dashboard + Grafana 혼합 |
| Component system | **shadcn/ui + TailwindCSS only** (임의 UI 라이브러리 추가 금지) |
| Layout | 좌측 Sidebar + 상단 헤더(환경/상태) + 메인 콘텐츠 |
| Density | 운영 콘솔: 정보 밀도 높게, 카드/테이블 중심 |
| Empty / Error | 연결 실패 시 원인·재시도 표시 (빈 화면 금지) |
| Realtime indicator | 연결 상태(WS/Polling) 표시 |

---

## 6. Dashboard 기능

### 6.1 Home (Dashboard)

| Widget | Data Source | Notes |
|--------|-------------|-------|
| Server Status | Node Exporter / host API | Up/Down |
| CPU | Prometheus | % |
| Memory | Prometheus | used/total |
| Disk | Prometheus | used/total |
| Network | Prometheus | RX/TX |
| Uptime | host / actuator | human-readable |
| Container Count | Docker API | running/total |
| Pod Count | K8s API | by phase summary |
| Recent Deploy | GitHub Actions / deploy log | last N |
| Recent Alert | Alertmanager | last N |
| Recent Log | Loki | last N lines summary |

**Realtime:** 5초 Polling **또는** WebSocket. 기본은 WebSocket, 실패 시 Polling fallback.

---

## 7. Kubernetes Page

### 7.1 Pod List Columns

| Column | Field |
|--------|-------|
| Name | pod name |
| Namespace | namespace |
| Status | phase / ready |
| Restart Count | containerStatuses |
| CPU | metrics-server or Prometheus |
| Memory | metrics-server or Prometheus |
| Image Version | container image tag |
| Node | nodeName |
| Age | creationTimestamp |

### 7.2 Pod Detail Actions

- Log 보기
- Restart (delete pod → controller recreate)
- Delete
- Describe (YAML/JSON summary)
- Events

### 7.3 Deployment List Columns

| Column | Field |
|--------|-------|
| Name | deployment name |
| Namespace | namespace |
| Replica | desired |
| Ready | ready/desired |
| Image | primary container image |
| Version | image tag |
| Deploy Time | last update |

### 7.4 Deployment Actions

- Restart Deployment
- Rolling Update (image tag 지정)
- Scale (replicas)

**구현 제약:** 실제 k3s API 사용. Mock은 인터페이스 뒤에만, 서비스 미구축 단계에서만 허용.

---

## 8. Docker Page

### Container List Columns

| Column | Field |
|--------|-------|
| Name | Names |
| Status | Running / Stopped / Paused |
| Image | Image |
| CPU | stats |
| Memory | stats |
| Ports | Ports |
| Volume | Mounts summary |

### Actions

- Start / Stop / Restart / Remove
- Logs
- Inspect

**구현 제약:** Docker Engine API (unix socket 또는 TCP). 권한·보안은 Settings/시크릿으로 관리.

---

## 9. Monitoring Page

**Data source:** Prometheus HTTP API (Grafana 임베드만으로 대체하지 않음 — Dashboard 자체 차트 필수).

### Required Charts

| Chart | Example Metric Direction |
|-------|-------------------------|
| CPU Usage | node / container / pod |
| Memory Usage | node / container / pod |
| Disk Usage | filesystem |
| Filesystem | mountpoints |
| Network RX / TX | node network |
| JVM Heap | Spring Actuator / Micrometer |
| GC | JVM GC |
| Thread Count | JVM threads |
| HTTP TPS | http_server requests |
| HTTP Response Time | latency histogram/summary |
| DB Connections | Hikari / postgres exporter if present |
| Docker Stats | cAdvisor |
| Pod Stats | kube-state / cAdvisor |

UI: Recharts, 시간 범위 선택(15m / 1h / 6h / 24h).

---

## 10. Logs Page

**Data source:** Loki Query API.

### Filters

- Container
- Namespace
- Level
- Date range
- Keyword

### Features

- 검색
- 로그 스트리밍
- 실시간 Auto Scroll
- Download (현재 결과)

---

## 11. Deployments Page

**Integration:** GitHub Actions API (+ 자체 deploy 이력 저장 권장).

### List Fields

- Commit
- Author
- Duration
- Branch
- Workflow
- Status

### Actions

- Deploy Latest
- Rollback
- View Logs

Dashboard에서 배포 진행률(WebSocket) 표시 필수.

---

## 12. Alerts Page

**Integration:** Alertmanager API.

| Feature | Spec |
|---------|------|
| Severities | Critical / Warning / Info |
| History | firing / resolved |
| Recovery Time | resolved - startsAt |
| Acknowledged | 플랫폼 측 ACK 상태(DB 저장) |

Discord 알림은 Alertmanager receiver로 연동.

---

## 13. Database Page

**Target:** PostgreSQL (플랫폼 DB).

| Metric / Feature |
|------------------|
| DB Size |
| Connections |
| Tables |
| Indexes |
| Slow Query (가능 범위 내) |
| Backup Status |
| Restore Status |

---

## 14. Redis Page

| Metric |
|--------|
| Memory |
| Key Count |
| Hit Ratio |
| TTL (샘플/요약) |
| Eviction |

---

## 15. Settings

| Setting | Notes |
|---------|-------|
| Dark Mode | 기본 on, 토글 가능 |
| API URL | 프론트 기준 backend base URL |
| Polling Time | default 5000ms |
| Discord Webhook | secret, 서버 측 저장 권장 |
| GitHub Token | secret |
| Cloudflare Token | secret |

시크릿은 프론트 번들에 하드코딩 금지. 환경변수 / 서버 시크릿 스토어 사용.

---

## 16. Backend APIs

Spring Boot가 **단일 Platform API**로 외부 시스템을 어댑트한다.

### 최소 엔드포인트 (v1 필수)

```text
# Auth
POST   /api/auth/login
POST   /api/auth/refresh
GET    /api/auth/me

# Server
GET    /api/server/status

# Docker
GET    /api/docker/containers
GET    /api/docker/containers/{id}
GET    /api/docker/containers/{id}/logs
POST   /api/docker/containers/{id}/start
POST   /api/docker/containers/{id}/stop
POST   /api/docker/containers/{id}/restart
DELETE /api/docker/containers/{id}

# Kubernetes
GET    /api/kubernetes/pods
GET    /api/kubernetes/pods/{namespace}/{name}
GET    /api/kubernetes/pods/{namespace}/{name}/logs
GET    /api/kubernetes/pods/{namespace}/{name}/events
POST   /api/kubernetes/pods/{namespace}/{name}/restart
DELETE /api/kubernetes/pods/{namespace}/{name}
GET    /api/kubernetes/deployments
POST   /api/kubernetes/deployments/{namespace}/{name}/restart
POST   /api/kubernetes/deployments/{namespace}/{name}/scale
POST   /api/kubernetes/deployments/{namespace}/{name}/rolling-update

# Prometheus
GET    /api/prometheus/query
GET    /api/prometheus/query_range
GET    /api/prometheus/cpu
GET    /api/prometheus/memory
# (추가 메트릭은 query_range 래핑으로 확장)

# Logs
GET    /api/logs
GET    /api/logs/stream   # SSE or WS bridge

# Deployments (CI/CD)
GET    /api/deployments
POST   /api/deploy
POST   /api/rollback
GET    /api/deployments/{id}/logs

# Alerts
GET    /api/alerts
POST   /api/alerts/{id}/ack

# Database / Redis
GET    /api/database/status
GET    /api/redis/status

# Settings (admin)
GET    /api/settings
PUT    /api/settings
```

### API Design Rules

1. 응답 공통 래퍼: `{ "success": boolean, "data": T, "error": { "code", "message" } | null }`
2. 페이지네이션: `page`, `size`, `total` (목록 API)
3. 시간: ISO-8601 UTC
4. 권한: Admin 전용 mutation, Viewer는 read-only
5. 외부 시스템 오류: 502 + 원인 메시지 (연결 실패 숨기지 않음)

---

## 17. WebSocket

### Channels / Events

| Event | Payload (concept) |
|-------|-------------------|
| `metrics.server` | cpu, memory, disk, network |
| `alert.fired` / `alert.resolved` | alert summary |
| `logs.line` | stream chunk |
| `deploy.progress` | stage, percent, message |
| `container.status` | id, status |
| `pod.status` | ns, name, phase |

**Auth:** JWT (query token 또는 연결 후 첫 메시지로 authenticate).

**Fallback:** WebSocket 불가 시 REST polling (Settings Polling Time).

---

## 18. CI/CD

```text
GitHub Push
    ↓
GitHub Actions
    ↓
Test
    ↓
Gradle Build
    ↓
Docker Build
    ↓
Docker Hub Push
    ↓
SSH (or in-cluster apply)
    ↓
Rolling Update
    ↓
Health Check
    ↓
Discord Notification
```

Dashboard에서도 배포 진행률 확인 가능해야 한다.

### Pipeline Rules

- `main` (또는 `release`) 푸시 시 자동 배포
- PR에서는 test + build (deploy 제외) 권장
- 이미지 태그: `git sha` + `latest` (latest는 main만)
- Health check 실패 시 배포 실패 처리 + Discord 알림

---

## 19. Monitoring

| Component | Role |
|-----------|------|
| Prometheus | scrape + store |
| Node Exporter | host metrics |
| cAdvisor | container metrics |
| Spring Actuator + Micrometer | JVM/HTTP metrics |
| Grafana | 보조 대시보드 |
| CloudLab Monitoring page | **1차 사용자 대면 차트** |

모든 핵심 메트릭은 CloudLab Dashboard에서도 확인 가능해야 한다.

---

## 20. Logging

| Component | Role |
|-----------|------|
| Promtail | 수집 |
| Loki | 저장·질의 |
| Grafana | 보조 |
| CloudLab Logs page | 실시간 스트리밍 UI |

---

## 21. Security

| Control | Spec |
|---------|------|
| Auth | Spring Security + JWT Login |
| Roles | `ADMIN`, `VIEWER` |
| Transport | HTTPS (Cloudflare Tunnel / Nginx) |
| Secrets | 환경변수 분리, 레포 커밋 금지 |
| Docker/K8s access | API 서버만 접근, 브라우저에서 소켓 직접 노출 금지 |
| Audit | 주요 mutation(배포, restart, delete) 감사 로그 권장 |

---

## 22. Folder Structure

```text
cloudlab/   # repository root (this repo)

frontend/
  app/
  components/
  hooks/
  services/
  store/
  types/

backend/
  src/main/java/.../controller/
  src/main/java/.../service/
  src/main/java/.../repository/
  src/main/java/.../websocket/
  src/main/java/.../monitoring/
  src/main/resources/

docker/

kubernetes/

monitoring/

logging/

nginx/

scripts/

docs/

.github/
  workflows/

README.md
AGENTS.md
```

> 실제 루트 디렉터리명은 레포 이름(`infra` 등)과 무관하게 위 구조를 따른다. Java 패키지 base: `com.cloudlab`.

---

## 23. Development Rules

AI와 개발자는 **반드시** 아래 순서를 따른다. 단계를 건너뛰지 않는다.

| Step | Work | Exit Criteria |
|------|------|----------------|
| 1 | 프로젝트 구조 생성 | 폴더·문서·gitignore·README 골격 존재 |
| 2 | Docker | 베이스 이미지/런타임 Dockerfile 초안 |
| 3 | Spring Boot | 부트 앱 기동, health endpoint |
| 4 | Next.js | 앱 기동, 다크 레이아웃·사이드바 골격 |
| 5 | Docker Compose | backend/frontend/db/redis 로컬 기동 |
| 6 | Kubernetes | k3s 매니페스트/Helm, 샘플 배포 |
| 7 | Monitoring | Prometheus stack scrape 확인 |
| 8 | Logging | Loki/Promtail 수집 확인 |
| 9 | CI/CD | Actions pipeline 성공 |
| 10 | Dashboard 연결 | 실데이터 연동 (인터페이스 교체) |
| 11 | 테스트 | API/통합/핵심 E2E |
| 12 | README 작성 | 섹션 25 항목 충족 |

### Implementation Rules

1. 각 기능은 **작은 단위**로 구현 → 테스트 → 커밋.
2. Mock은 서비스 미구축 단계에서만, **포트/어댑터 인터페이스 뒤**에 둔다.
3. 새 기술 추가 시 `docs/`에 설계 문서 작성.
4. 우선순위: **Observability > Security > Maintainability > Feature 폭**.
5. 이 프로젝트는 CRUD 앱이 아니라 **운영 플랫폼**이다.

---

## 24. Git Commit Convention

```text
feat:
fix:
docs:
refactor:
test:
ci:
infra:
monitoring:
logging:
```

예: `feat: add pod list API`, `infra: add k3s deployment manifests`.

---

## 25. README Requirements

README(`.github/README.md`)에는 반드시 포함한다. **Step 12 완료 기준 — 모두 충족.**

- [x] 프로젝트 소개
- [x] 시스템 아키텍처
- [x] 기술 스택
- [x] 프로젝트 구조
- [x] 설치 방법
- [x] Kubernetes 구성
- [x] Docker 구성
- [x] CI/CD 파이프라인
- [x] 모니터링 구성
- [x] 로그 수집 구성
- [x] Dashboard 설명
- [x] 장애 복구 시나리오
- [x] 성능 테스트
- [x] 트러블슈팅
- [x] 향후 개선 사항 (v2 IaC 포함)

---

## 26. Interview Demo Scenario (5 min)

1. Dashboard 접속 → 서버, Pod, 컨테이너 상태 확인
2. GitHub에 코드 Push → GitHub Actions 실행 확인
3. 새 버전 자동 배포(Rolling Update) 진행 상황 확인
4. Dashboard에서 CPU·메모리·JVM 메트릭 실시간 확인
5. 백엔드 Pod 강제 삭제 → Kubernetes 자동 복구 확인
6. Alertmanager → Discord 장애 알림
7. Logs 화면에서 장애·복구 로그 확인
8. 서비스 정상 복구를 Dashboard에서 최종 확인

데모 스크립트 상세: `docs/demo-scenario.md`.

---

## 27. CloudLab v2 Roadmap (IaC)

v1 완성 후 **IaC 플랫폼**으로 확장한다.

| 항목 | 상태 |
|------|------|
| Terraform modules (network/SG/compute) + `envs/lab` | **Sketch done** (`iac/terraform/`) |
| Ansible roles (common/docker/k3s/cloudlab_app) | **Sketch done** (`iac/ansible/`) |
| Bootstrap orchestrator | **Sketch done** (`scripts/iac-bootstrap.sh`) |
| Design doc | [docs/v2-iac.md](./v2-iac.md) |
| Live AWS apply + destroy | Pending (credentials / cost) |
| Multi-cloud / HA | Future |

스토리라인: **v1 운영 플랫폼 → v2 인프라 자동화 플랫폼 (scaffold in-repo)**.

---

## 28. Decision Log (Lock-in for AI)

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Cluster | k3s | 단일 노드 self-hosted에 적합 |
| Backend | Spring Boot + Java 21 | 포트폴리오 JVM/엔터프라이즈 스택 |
| Frontend | Next.js + TS + shadcn | 현대적 운영 UI |
| Metrics | Prometheus first-class | Grafana는 보조 |
| Logs | Loki | Grafana stack 일관성 |
| Auth | JWT + ADMIN/VIEWER | 단순 역할 모델 |
| Edge | Cloudflare Tunnel + Nginx | 비용·보안 균형 |
| Real data | No permanent mocks | 어댑터 패턴으로만 일시 Mock |
| Package base | `com.cloudlab` | 일관된 Java 패키지 |
| Default theme | Dark | 운영 콘솔 UX |

이 표와 충돌하는 임의 기술 교체는 **문서 수정 PR 없이 금지**한다.
