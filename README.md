# CloudLab

**Self-Hosted DevOps Platform** — 개인/포트폴리오용 내부 운영 플랫폼.

AWS Console처럼 브라우저 하나로 서버·Kubernetes·Docker·배포·로그·모니터링·알림을 운영하는 것을 목표로 한다.

> 웹 서비스가 목적이 아니라 **DevOps 운영 플랫폼 구축**이 목적이다.

| | |
|---|---|
| **Version** | 1.0 (scaffolding) |
| **PRD** | [docs/PRD.md](docs/PRD.md) |
| **Architecture** | [docs/architecture.md](docs/architecture.md) |
| **AI contract** | [AGENTS.md](AGENTS.md) |
| **Demo** | [docs/demo-scenario.md](docs/demo-scenario.md) |

---

## 프로젝트 소개

CloudLab은 Platform Engineer가 만드는 **내부 운영 콘솔** 수준을 목표로 한다.

- 서버 상태, Pod/컨테이너 관리
- GitHub Actions 기반 CI/CD와 배포 진행률
- Prometheus / Loki / Alertmanager 기반 Observability
- 장애 주입 → 자동 복구 → 알림 → 로그 확인까지 한 흐름 시연

포트폴리오에서 보여 줄 역량: Linux, Docker, Compose, k3s, Helm, GitHub Actions, CI/CD, Monitoring, Logging, Nginx, Cloudflare Tunnel, Spring Boot, Next.js.

---

## 시스템 아키텍처

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
 Docker Engine · k3s · PostgreSQL · Redis
 Prometheus · Grafana · Loki · Alertmanager
 Node Exporter · cAdvisor
────────────────────────────────────────────────────
             Ubuntu Server
```

상세: [docs/architecture.md](docs/architecture.md)

---

## 기술 스택

| Layer | Stack |
|-------|--------|
| Frontend | Next.js, TypeScript, TailwindCSS, shadcn/ui, React Query, Zustand, Socket.IO, Recharts |
| Backend | Spring Boot, Java 21, Spring Security, Actuator, WebSocket, JPA, Redis, PostgreSQL |
| DevOps | Docker, Compose, k3s, Helm, GitHub Actions, Nginx, Cloudflare Tunnel |
| Observability | Prometheus, Grafana, Loki, Alertmanager, Node Exporter, cAdvisor |

---

## 프로젝트 구조

```text
.
├── frontend/          # Next.js Dashboard
├── backend/           # Spring Boot Platform API
├── docker/            # Dockerfiles
├── kubernetes/        # k3s / Helm manifests
├── monitoring/        # Prometheus, Grafana, Alertmanager
├── logging/           # Loki, Promtail
├── nginx/             # Reverse proxy config
├── scripts/           # Ops scripts
├── docs/              # PRD, architecture, plans
├── .github/           # GitHub Actions
├── AGENTS.md          # AI development contract
└── README.md
```

---

## 설치 방법

> Step 1 완료 시점: 문서·골격만 존재. 실행 가능한 스택은 Step 2–5 이후 채워진다.

```bash
# 예정 (Compose 도입 후)
cp .env.example .env
# docker compose up -d
```

자세한 설치 절차는 각 단계 완료 시 이 섹션을 갱신한다.

---

## Kubernetes 구성

- 런타임: **k3s**
- 매니페스트/Helm: `kubernetes/`
- 상세 문서는 Step 6 이후 작성

---

## Docker 구성

- 이미지 정의: `docker/`
- 로컬 오케스트레이션: Docker Compose (Step 5)
- 상세 문서는 Step 2·5 이후 작성

---

## CI/CD 파이프라인

```text
Push → GitHub Actions → Test → Gradle Build → Docker Build
  → Registry Push → Deploy (Rolling Update) → Health Check → Discord
```

워크플로: `.github/workflows/` (Step 9)

---

## 모니터링 구성

- Prometheus + Node Exporter + cAdvisor + Spring Actuator
- Grafana (보조)
- CloudLab Monitoring 페이지가 1차 UI

설정: `monitoring/` (Step 7)

---

## 로그 수집 구성

- Promtail → Loki
- CloudLab Logs 페이지에서 스트리밍

설정: `logging/` (Step 8)

---

## Dashboard 설명

좌측 사이드바 (다크 모드 기본):

| Menu | 역할 |
|------|------|
| Dashboard | 서버·리소스·최근 배포/알림/로그 요약 |
| Kubernetes | Pod/Deployment 조회·조작 |
| Docker | 컨테이너 조회·조작 |
| Deployments | GitHub Actions / 배포·롤백 |
| Monitoring | Prometheus 차트 |
| Logs | Loki 검색·스트리밍 |
| Database | PostgreSQL 상태 |
| Redis | Redis 상태 |
| Alerts | Alertmanager |
| Settings | API URL, polling, 토큰 등 |

---

## 장애 복구 시나리오

1. 백엔드 Pod 강제 삭제
2. k3s Deployment 컨트롤러가 새 Pod 생성
3. Alertmanager → Discord 알림
4. Dashboard Logs/Monitoring에서 복구 확인

면접용 스크립트: [docs/demo-scenario.md](docs/demo-scenario.md)

---

## 성능 테스트

> TODO (Step 11+): API latency, dashboard polling load, Prometheus cardinality notes.

---

## 트러블슈팅

| Symptom | Check |
|---------|--------|
| API 502 | Platform API → Docker/k8s/Prometheus 연결, 로그 |
| 메트릭 공백 | Prometheus targets, scrape config |
| 로그 없음 | Promtail labels, Loki ready |
| 배포 실패 | Actions logs, image pull, health endpoint |

---

## 향후 개선 사항 (v2)

- **Terraform**: 서버·네트워크 프로비저닝
- **Ansible**: 런타임·에이전트·앱 구성
- 버튼 한 번으로 **인프라 구축 → 배포 → 모니터링** 자동 구성 (IaC 플랫폼)

스토리: **CloudLab v1 운영 플랫폼 → v2 인프라 자동화 플랫폼**

---

## 개발 순서

PRD Development Rules를 따른다. 현재 진행: [docs/development-plan.md](docs/development-plan.md)

## License

Private portfolio project (update if you open-source).
