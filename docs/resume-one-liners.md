# 이력서 · 포트폴리오 문구 (CloudLab)

복사해서 바로 쓰세요. **한 줄 요약 → 불릿 3~5개**가 가장 무난합니다.

---

## 1) 이력서 한 줄 (추천)

### 한국어 — 메인 추천

> **Self-Hosted DevOps 운영 플랫폼(CloudLab) 설계·구현** — Docker/k3s·Prometheus/Loki·GitHub Actions를 Spring Boot·Next.js 대시보드로 통합하고, 장애 주입·복구·알림 데모 및 CI/CD·E2E 스모크까지 구축

### 한국어 — 짧은 버전

> Docker·Prometheus·Loki·GitHub Actions를 통합한 **Self-Hosted 운영 콘솔** 구축 (Next.js + Spring Boot)

### 한국어 — Platform 강조

> 브라우저 기반 내부 운영 플랫폼으로 컨테이너/메트릭/로그/배포 현황을 단일 API·대시보드에서 관측·조작하도록 설계·구현

### English — main

> Designed and built **CloudLab**, a self-hosted DevOps ops platform integrating Docker, k3s, Prometheus, Loki, and GitHub Actions behind a Spring Boot control plane and Next.js dashboard, with CI/CD, demo automation, and Playwright UI smoke tests

### English — short

> Built a self-hosted ops console (Docker / Prometheus / Loki / Actions) with Spring Boot + Next.js

---

## 2) 이력서 불릿 (3~5개 선택)

아래 중 **역할에 맞게 3~5개만** 고르세요. 숫자·결과는 본인 환경에 맞게 조정 가능합니다.

1. **통합 제어면(Control Plane):** Spring Boot Platform API로 Docker Engine, Kubernetes, Prometheus, Loki, Alertmanager를 어댑터 패턴으로 연동하고, 브라우저는 인프라 시크릿에 직접 접근하지 않도록 설계  
2. **운영 대시보드:** Next.js(App Router)·React Query·다크 UI로 서버 현황, 컨테이너, 메트릭 차트, 로그 검색, 알림, DB/Redis 상태를 실시간 폴링으로 제공  
3. **Observability:** Prometheus(Node Exporter, cAdvisor, Actuator)·Loki/Promtail·Alertmanager 스택을 Compose로 구성하고, Dashboard에서 PromQL/LogQL 결과를 직접 조회  
4. **CI/CD:** GitHub Actions로 Gradle/Vitest 테스트·이미지 빌드/푸시·선택적 SSH 배포·헬스체크·Discord 알림 파이프라인 구축 (PR=CI, main=CD)  
5. **인프라 as Code 스케치:** Terraform(VPC/SG/EC2)·Ansible(Docker/k3s/앱) 골격과 **$0 검증 스크립트**(fmt/validate/Free Tier 가드레일)로 비용 인식 기반 IaC 경로 제시  
6. **품질·데모:** JUnit/MockMvc·Vitest·Playwright 골든 패스 E2E, `demo-reset`/`demo-run`으로 5분 면접 시연 자동화  

### 신입/주니어용 (성과 톤 완화)

- Self-Hosted 환경에서 모니터링·로깅·컨테이너 관리를 하나의 웹 콘솔로 묶어 구현  
- Docker Compose 기반 로컬 풀스택과 GitHub Actions CI/CD를 연동  
- 장애 시나리오(컨테이너 재시작)와 로그·메트릭 확인 흐름을 데모 스크립트로 정리  

### 경력/전환용 (임팩트 톤)

- 분산 도구(Docker, Prom, Loki, Actions)를 단일 운영 UX로 통합하여 관측·조치 경로 단축  
- 어댑터 기반 API로 upstream 장애 시 502/원인 메시지를 노출하는 운영 친화적 에러 처리  
- Free Tier 리스크를 명시한 IaC 검증 파이프라인으로 “무조건 apply”가 아닌 **안전한 기본 경로** 정립  

---

## 3) 스킬 키워드 (이력서 Skills 칸)

```
Linux, Docker, Docker Compose, Kubernetes(k3s), Helm,
GitHub Actions, CI/CD, Prometheus, Grafana, Loki, Alertmanager,
Spring Boot, Java 21, Next.js, TypeScript, React Query,
Terraform, Ansible (IaC sketch), Observability, Platform Engineering
```

---

## 4) 자기소개서 / 프로젝트 소개 문단 (150~200자)

Self-Hosted 환경에서 서버·컨테이너·메트릭·로그·배포를 한 화면에서 다루는 운영 플랫폼 CloudLab을 설계·구현했습니다. Spring Boot가 Docker/Prometheus/Loki 등을 어댑트하고 Next.js 대시보드로 실데이터를 제공하며, Compose·GitHub Actions·면접용 데모 자동화까지 포함해 재현 가능한 포트폴리오로 정리했습니다.

---

## 5) 면접 예상 질문 30초 답

| 질문 | 한 줄 축 |
|------|----------|
| 왜 만들었나? | CRUD가 아니라 **운영 콘솔** 역량을 증명하려고 |
| 왜 모놀리식 API? | 브라우저 보안 경계 + 단일 제어면 |
| 왜 Mock 안 쓰나? | 어댑터 뒤 실연동, 장애도 실제로 보여주려고 |
| 보안은? | lab open-api, prod는 JWT/권한·socket 최소화가 방향 |
| 비용/IaC? | v2는 스케치, 기본은 **$0 validate**, Free Tier apply는 옵트인 |
| 테스트? | 단위+MockMvc+Vitest+Playwright 골든 패스 |

상세 데모 대본: [demo-rehearsal-ui.md](./demo-rehearsal-ui.md)
