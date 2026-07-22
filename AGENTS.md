# AGENTS.md — AI Development Contract for CloudLab

This repository is a **portfolio-grade self-hosted DevOps / Platform Engineering project**, not a CRUD demo.

Authoritative requirements: [`docs/PRD.md`](docs/PRD.md).

## Identity

- Product name: **CloudLab**
- Type: Internal operations platform (AWS Console–like for a self-hosted lab)
- Audience: Platform / DevOps / Cloud engineer hiring managers

## Hard Rules

1. **Follow Development Rules order** (PRD §23). Do not jump to UI polish before foundations exist unless the current step explicitly includes it.
2. **Do not invent architecture.** Decisions are locked in PRD §28. Changing stack requires updating `docs/PRD.md` first.
3. **No permanent mock data.** Temporary mocks only behind ports/adapters when the real system is not up yet.
4. **Browser never holds infra credentials.** Docker socket, kubeconfig, Prometheus admin access → Platform API only.
5. **Dark theme + shadcn/ui + Tailwind** for frontend. Do not add alternate UI kits.
6. **Small commits** with conventional prefixes: `feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `ci:`, `infra:`, `monitoring:`, `logging:`.
7. **New tech ⇒ new doc** under `docs/`.
8. Prioritize **Observability, Security, Maintainability** over feature breadth.
9. Java base package: `com.cloudlab`.
10. API response envelope and roles: see PRD §16 and §21.
11. **Git: 큼지막한 진행(Step 완료, 주요 기능 묶음, 문서/구조 전환)마다 commit + `git push` 자동 수행.** 사용자에게 매번 push 허가를 다시 묻지 않는다(이 문서가 사전 승인). force-push / `--force` / main 강제 덮어쓰기는 금지. remote가 없으면 생성·연결 후 push.
12. **커밋 메시지 언어: 한국어 메인.** prefix(`feat:` 등)는 유지. 제목·본문은 한국어로 작성하고, 기술 용어·경로·명령은 영어가 자연스러우면 영어 그대로 사용.
13. **프로젝트 README 위치는 `.github/README.md` 고정.** 루트 `README.md`를 다시 만들지 않는다. 이미지/문서 링크는 `.github/` 기준 상대경로(`../docs/...`)를 쓴다.

## Git Commit & Push

### When to commit + push

| 시점 | 예 |
|------|----|
| Development Step 완료 | Step 2 Docker, Step 3 Spring Boot … |
| 의미 있는 기능 단위 완료 | API 어댑터, 사이드바 골격, CI 파이프라인 |
| 문서/정책 전환 | PRD 개정, AGENTS 규칙 추가 |

자잘한 중간 수정만 있을 때는 묶어서 한 번에 커밋해도 된다. **Step exit criteria를 만족하면 반드시 push.**

### Message format

```text
<prefix>: <한국어 요약 제목>

<무엇을 왜 바꿨는지 2~5문장, 한국어>
- 세부 bullet (경로, 명령, 기술명은 영어 OK)
```

예시:

```text
docs: CloudLab PRD·구조 부트스트랩 및 AI 개발 계약 추가

포트폴리오용 운영 플랫폼 기준으로 PRD를 고정하고 Step 1 폴더 구조를 생성했다.
- docs/PRD.md, architecture, demo-scenario
- AGENTS.md에 커밋/푸시 자동화 규칙 명시
```

### Push procedure

1. `git status` / `git diff` 확인
2. 관련 파일만 stage (secret 제외)
3. HEREDOC으로 commit message 작성
4. `git pull --rebase` (remote에 새 커밋 있을 때)
5. `git push -u origin HEAD` (또는 현재 브랜치)
6. 사용자에게 커밋 제목·원격 URL·push 결과 요약 보고

## Current Step

See [`docs/development-plan.md`](docs/development-plan.md). Implement only the active step and its exit criteria.

## Out of Scope (v1)

- Terraform / Ansible (v2)
- Multi-cluster, multi-tenant SaaS
- Paid cloud control planes as dependencies

## When Uncertain

1. Read `docs/PRD.md` and `docs/architecture.md`.
2. Prefer the option that improves the **5-minute interview demo**.
3. Ask the user only if a decision is not covered by the PRD decision log.
