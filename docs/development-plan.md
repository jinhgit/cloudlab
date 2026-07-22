# Development Plan

Follow PRD §23. Do not skip steps.

| Step | Status | Deliverable |
|------|--------|-------------|
| 1. Project structure | **done** | folders, PRD, AGENTS, README skeleton |
| 2. Docker | **done** | Dockerfiles, entrypoint, ignore, docs/docker.md |
| 3. Spring Boot | **done** | bootable API + actuator/api health + tests |
| 4. Next.js | **done** | dark shell + sidebar routes + build OK |
| 5. Docker Compose | **done** | postgres/redis/backend/frontend stack |
| 6. Kubernetes | **done** | k3s manifests + Helm chart + apply scripts |
| 7. Monitoring | **done** | Prometheus scrape + Grafana + alert rules |
| 8. Logging | **done** | Loki + Promtail + Grafana Loki DS |
| 9. CI/CD | **done** | GitHub Actions CI + CD + deploy scripts |
| 10. Dashboard wiring | **done** | real adapters + UI pages |
| 11. Tests | pending | unit + integration critical paths |
| 12. README complete | pending | PRD §25 checklist |

## Definition of Done (v1)

- Demo scenario (`docs/demo-scenario.md`) runnable end-to-end
- No permanent mock data in production profile
- README §25 complete
- ADMIN/VIEWER auth working
- Secrets not committed
