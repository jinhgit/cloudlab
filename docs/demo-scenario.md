# Interview Demo Scenario (5 minutes)

> **운영 플레이북(최신):** [demo-playbook.md](./demo-playbook.md)  
> **원커맨드:** `./scripts/demo-reset.sh` → `./scripts/demo-run.sh`

Goal: show CloudLab as an **operations platform**, not a CRUD app.

## One-shot prep

```bash
./scripts/demo-reset.sh
./scripts/demo-capture-status.sh
# optional Discord evidence
# export DISCORD_WEBHOOK_URL=...
# ./scripts/demo-discord-test.sh
./scripts/demo-run.sh --preflight
```

## Preconditions

- [ ] `./scripts/demo-reset.sh` green
- [ ] Dashboard http://localhost:3000
- [ ] Prometheus / Loki / Alertmanager healthy
- [ ] (Optional) Discord webhook for live alert
- [ ] GitHub Actions recent CI success (README badge)

## Script

| # | Action | What to say | What must appear |
|---|--------|-------------|------------------|
| 1 | Open Dashboard | “브라우저 하나로 서버·컨테이너·관측을 봅니다.” | CPU/Mem, containers, integration badges |
| 2 | GitHub Actions | “Push가 CI/CD 파이프라인입니다.” | 초록 런 + README badge |
| 3 | Monitoring | “Prometheus를 자체 Dashboard 차트로.” | CPU/Memory charts |
| 4 | Failure inject | “장애를 내고 복구를 증명합니다.” | Docker restart or pod delete |
| 5 | Alerts + Discord | “운영 채널 알림까지 닫습니다.” | Alerts page + Discord screenshot |
| 6 | Logs | “Loki로 같은 구간 로그.” | backend lines |
| 7 | Dashboard close | “정상 복귀로 마무리.” | all up |

## Inject helpers

```bash
# guided cues + optional restart
./scripts/demo-run.sh --inject-docker-restart

# k8s (if cluster available)
kubectl -n cloudlab delete pod -l app.kubernetes.io/name=cloudlab-backend
```

## Evidence folder

`docs/assets/demo/` — status capture, snapshot SVG, PNG checklist.

## Backup Plan

1. Pre-open a successful Actions run tab.  
2. Use committed `discord-alert.png` if live webhook unavailable.  
3. Explain architecture diagram if a service is briefly down; `demo-reset.sh` recovers.
