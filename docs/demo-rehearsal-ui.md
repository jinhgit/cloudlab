# 면접 데모 리허설 — UI 동선 + 설명 대본

**목표 시간:** 5분  
**전제:** `./scripts/demo-reset.sh` 성공, 탭 미리 오픈  
**큐 자동화:** `./scripts/demo-run.sh`  
**이력서 문구:** [resume-one-liners.md](./resume-one-liners.md)

브라우저 주소 기준: `http://localhost:3000`

---

## 0. 오프닝 (10초, 아직 클릭 전)

**자세:** 화면 공유 켠 뒤, 사이드바가 보이게 창 크기 확보.

**말:**

> “오늘은 CRUD 웹앱이 아니라, **Self-Hosted 운영 플랫폼 CloudLab**을 보여드리겠습니다.  
> Docker, Prometheus, Loki, GitHub Actions를 **Spring Boot 제어면 + Next.js 대시보드**로 묶었고,  
> 장애를 내고 복구하는 흐름까지 한 콘솔에서 닫습니다.”

---

## 1. Dashboard `/` (0:00–0:45)

### 클릭/시선

| 순서 | UI | 마우스/시선 |
|------|-----|-------------|
| 1 | 좌측 **CloudLab / Ops Platform** | 로고·제품명 가리키기 |
| 2 | 상단 헤더 **API UP** 배지 | “Platform API 연결” |
| 3 | 카드 **CPU / Memory / Disk / JVM Heap** | 위젯 한 바퀴 |
| 4 | **Containers** `running/total` | Docker 연동 숫자 |
| 5 | **Integrations** 배지 줄 | prometheus / loki / docker 등 |

### 말할 내용 (자세히)

> “첫 화면은 운영 현황 요약입니다.  
> 헤더의 API 배지는 Spring Boot Platform API 헬스고,  
> CPU·메모리는 Prometheus Node Exporter 메트릭을 백엔드가 조회해 내려줍니다.  
> 컨테이너 수는 Docker Engine API, 아래 Integrations는 업스트림 연결 상태입니다.  
> 중요한 설계는 **브라우저가 docker.sock이나 Prometheus에 직접 안 붙고**,  
> 반드시 Platform API 어댑터를 거친다는 점입니다.”

### 예상 질문 대응 (짧게)

- “숫자가 0이면?” → “업스트림 down 시 0/배지 down, 에러는 502로 숨기지 않음”  
- “실시간?” → “WebSocket 자리, 현재 React Query 폴링(기본 5초, Settings)”

---

## 2. CI/CD — Actions + (선택) Deployments (0:45–1:55)

### 클릭/시선

| 순서 | UI |
|------|-----|
| 1 | 브라우저 탭: **GitHub → Actions** (초록 CI/CD) |
| 2 | (선택) Dashboard 사이드바 **Deployments** |
| 3 | README 상단 **CI / CD badge** (미리 열어둬도 됨) |

### 말할 내용

> “배포 파이프라인은 GitHub Actions입니다.  
> PR에서는 테스트와 빌드만, main에서는 이미지 빌드·푸시와 선택적 원격 배포, 헬스체크, Discord 알림까지 갈 수 있게 나눴습니다.  
> README의 초록 배지가 최근 파이프라인 성공 증거입니다.  
> Dashboard Deployments 페이지는 GitHub API로 워크플로 런을 보여 주고, 토큰이 없으면 ‘설정 필요’로 정직하게 표시합니다.”

### 백업

Push 라이브가 느리면 **이미 성공한 런**만 스크롤. “데모 시간 때문에 사전 실행분으로 설명”이라고 하면 충분.

---

## 3. Monitoring `/monitoring` (1:55–2:35)

### 클릭/시선

| 순서 | UI |
|------|-----|
| 1 | 사이드바 **Monitoring** |
| 2 | 상단 Stat: CPU % · Memory % · HTTP RPS · JVM Heap |
| 3 | 아래 **CPU (1h)** / **Memory (1h)** 차트 (Recharts) |

### 말할 내용

> “모니터링은 Grafana만 임베드한 게 아니라,  
> Prometheus `query` / `query_range`를 Platform API가 감싸고 Dashboard가 차트로 그립니다.  
> Grafana는 보조 진입점이고, 운영자가 쓰는 1차 UI를 제품 안에 둔 이유입니다.  
> 스크레이프 대상은 Node Exporter, cAdvisor, Spring Actuator입니다.”

### 팁

차트 선이 잠시 비면: “방금 기동 직후라 스크레이프 간격 중일 수 있다” + Prometheus Targets 탭 백업.

---

## 4. 장애 주입 — Docker `/docker` (2:35–3:25)

### 클릭/시선

| 순서 | UI |
|------|-----|
| 1 | 사이드바 **Docker** |
| 2 | 테이블에서 `cloudlab-backend` 행 |
| 3 | **Restart** 클릭 (또는 터미널 inject) |
| 4 | Status가 잠깐 바뀌었다가 running 복귀 관찰 |

### 말할 내용

> “강제로 장애를 내 보겠습니다.  
> Docker 페이지는 Engine API로 컨테이너 목록과 Start/Stop/Restart를 제공합니다.  
> backend를 재시작하면, 잠시 API 배지가 흔들릴 수 있지만 Compose/헬스체크 경로로 다시 살아납니다.  
> k3s가 있는 환경에서는 Pod 삭제로 Deployment 셀프힐을 같은 스토리로 설명할 수 있습니다.”

### 자동화

```bash
./scripts/demo-run.sh --inject-docker-restart
# 또는 큐 중에 DEMO_INJECT=docker
```

**주의:** 재시작 직후 3–10초는 API Offline 가능 → “복구 관찰 구간”이라고 말로 메우기.

---

## 5. Alerts `/alerts` + Discord (3:25–4:05)

### 클릭/시선

| 순서 | UI |
|------|-----|
| 1 | 사이드바 **Alerts** |
| 2 | (있으면) firing 테이블 Severity / Summary |
| 3 | Discord 창 — 사전 `demo-discord-test.sh` 메시지 또는 스크린샷 |

### 말할 내용

> “알림은 Alertmanager API를 Dashboard가 그대로 보여 줍니다.  
> Critical/Warning 라벨과 summary를 운영 채널로 보내는 경로를 열어 두었고,  
> Discord 웹훅으로 실제 메시지 한 번을 검증해 두었습니다.  
> ‘모니터링만 있고 알림이 없다’가 아니라, **관측 → 알림 → 사람** 루프를 닫는 게 목표입니다.”

### 사전 준비

```bash
export DISCORD_WEBHOOK_URL='...'
./scripts/demo-discord-test.sh
# docs/assets/demo/discord-alert.png 캡처
```

---

## 6. Logs `/logs` (4:05–4:40)

### 클릭/시선

| 순서 | UI |
|------|-----|
| 1 | 사이드바 **Logs** |
| 2 | Service = **backend** |
| 3 | (선택) Keyword `ERROR` 또는 비우고 Refresh |
| 4 | 하단 로그 스트림 스크롤 |

### 말할 내용

> “로그는 Loki입니다. Promtail이 Compose 프로젝트 라벨로 컨테이너 로그를 모으고,  
> Dashboard는 LogQL을 Platform API로 조회합니다.  
> 방금 재시작 구간의 로그를 같은 콘솔에서 볼 수 있어,  
> 메트릭·알림·로그를 툴 여러 개 오가지 않아도 됩니다.”

---

## 7. 클로징 `/` (4:40–5:00)

### 클릭

사이드바 **Dashboard**로 복귀 → Integrations / Containers 정상 확인.

### 말할 내용

> “정리하면, CloudLab은 개별 도구 나열이 아니라  
> **제어면 API + 운영 UI + 관측 스택 + CI/CD + 데모 자동화**로 묶은 Self-Hosted 운영 플랫폼입니다.  
> v2로는 Terraform·Ansible IaC 스케치와 무료 검증 파이프라인까지 확장 방향을 열어 두었습니다.  
> 질문 받겠습니다.”

---

## 페이지별 30초 설명 카드 (여분 시간용)

### Database `/database`

> “플랫폼 PostgreSQL 연결 상태, 사이즈, 커넥션, public 테이블 수를 조회합니다.  
> 로컬 프로파일에서는 DataSource를 끄고, Compose 프로파일에서 실DB에 붙습니다.”

### Redis `/redis`

> “Redis INFO 기반 메모리, 키 수, hit ratio, eviction을 보여 줍니다. 캐시/세션 레이어 관측용입니다.”

### Settings `/settings`

> “API Base URL과 폴링 주기를 브라우저 로컬에 저장합니다.  
> GitHub·Discord 시크릿은 프론트에 넣지 않고 서버 환경변수로만 둡니다.”

### Kubernetes `/kubernetes`

> “kubeconfig가 있으면 Pod/Deployment 목록과 삭제·롤아웃 재시작을 제공합니다.  
> 클러스터가 없으면 빈 목록/에러로 정직하게 실패하고, Compose 데모는 Docker 경로로 동일 스토리를 증명합니다.”

---

## 리허설 체크리스트 (소리 내어 3회)

- [ ] 1회차: 시간만 재며 스크립트 읽기  
- [ ] 2회차: 클릭 동선만 보고 말하기 (대본 최소화)  
- [ ] 3회차: 장애 주입 포함, 막히면 백업 플랜  
- [ ] 이력서 한 줄과 오프닝 첫 문장이 **같은 메시지**인지 확인  

```bash
./scripts/demo-reset.sh
./scripts/demo-run.sh --preflight
./scripts/demo-run.sh
```
