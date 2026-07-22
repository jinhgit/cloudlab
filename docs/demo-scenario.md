# Interview Demo Scenario (5 minutes)

Goal: show CloudLab as an **operations platform**, not a CRUD app.

## Preconditions

- [ ] Cloudflare Tunnel (or local) URL works
- [ ] Dashboard login (ADMIN)
- [ ] k3s workloads running (backend at minimum)
- [ ] Prometheus / Loki / Alertmanager healthy
- [ ] Discord webhook configured
- [ ] GitHub Actions secrets ready (registry, SSH/kube, Discord)

## Script

| # | Action | What to say | What must appear |
|---|--------|-------------|------------------|
| 1 | Open Dashboard | “브라우저 하나로 서버·Pod·컨테이너를 봅니다.” | CPU/Mem, container count, pod count, recent items |
| 2 | Push code to GitHub | “Push가 곧 배포 파이프라인입니다.” | Actions run started (GH UI or Deployments page) |
| 3 | Watch Deployments page | “Rolling update 진행률을 플랫폼에서 추적합니다.” | stages / progress / success |
| 4 | Monitoring page | “Prometheus 메트릭을 자체 대시보드에서 봅니다.” | CPU, memory, JVM live charts |
| 5 | Delete backend pod | “강제 장애를 내고 복구를 증명합니다.” | Pod Terminating → Running |
| 6 | Alerts + Discord | “Alertmanager가 운영 채널로 알림을 보냅니다.” | Firing alert + Discord message |
| 7 | Logs page | “Loki로 장애·복구 구간 로그를 봅니다.” | restart / error / recovery lines |
| 8 | Back to Dashboard | “서비스가 정상 복구된 것을 한 화면에서 확인합니다.” | green status, pod ready |

## Timing Guide

| Segment | Budget |
|---------|--------|
| 1 Dashboard overview | ~45s |
| 2–3 CI/CD | ~90s |
| 4 Metrics | ~45s |
| 5–7 Failure inject + observe | ~90s |
| 8 Close | ~30s |

## Failure Injection Commands (operator)

```bash
# Example: delete backend pod (namespace/name as deployed)
kubectl delete pod -n cloudlab -l app=cloudlab-backend
```

Prefer doing this **from the Dashboard Kubernetes page** if delete action is ready — that better proves the product.

## Backup Plan

If live push is too slow for 5 minutes:

1. Pre-trigger a workflow before the interview, or
2. Use **Deploy Latest** from the Deployments page on an already-built image.

Still explain the full pipeline from the README architecture diagram.
