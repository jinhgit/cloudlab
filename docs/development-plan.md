# Development Plan

Follow PRD §23. Do not skip steps.

| Step | Status | Deliverable |
|------|--------|-------------|
| 1. Project structure | **done** | folders, PRD, AGENTS, README skeleton |
| 2. Docker | **done** | Dockerfiles, entrypoint, ignore, docs/docker.md |
| 3. Spring Boot | **done** | bootable API + actuator/api health + tests |
| 4. Next.js | **done** | dark shell + sidebar routes + build OK |
| 5. Docker Compose | pending | local stack up |
| 6. Kubernetes | pending | k3s manifests / Helm |
| 7. Monitoring | pending | Prometheus scrape path |
| 8. Logging | pending | Loki + Promtail |
| 9. CI/CD | pending | GitHub Actions |
| 10. Dashboard wiring | pending | real adapters |
| 11. Tests | pending | unit + integration critical paths |
| 12. README complete | pending | PRD §25 checklist |

## Definition of Done (v1)

- Demo scenario (`docs/demo-scenario.md`) runnable end-to-end
- No permanent mock data in production profile
- README §25 complete
- ADMIN/VIEWER auth working
- Secrets not committed
