# CloudLab Architecture

## Overview

CloudLab is a self-hosted internal operations platform. A single control plane (Spring Boot) integrates Docker, Kubernetes (k3s), Prometheus, Loki, Alertmanager, and GitHub Actions, and exposes them through a Next.js operations dashboard.

Related: [PRD](./PRD.md)

## Request Path

```text
Browser
  → Cloudflare Tunnel
  → Nginx (TLS / path routing)
      ├─ /            → Next.js (frontend)
      ├─ /api         → Spring Boot (REST)
      └─ /ws          → Spring Boot (WebSocket)
```

## Control Plane vs Data Plane

| Plane | Components | Responsibility |
|-------|------------|----------------|
| Control | Next.js, Spring Boot, PostgreSQL, Redis | Auth, orchestration of ops actions, UI |
| Data / Workload | App deployments on k3s / Docker | User services under management |
| Observability | Prometheus, Loki, Alertmanager, exporters | Metrics, logs, alerts |
| Edge | Cloudflare Tunnel, Nginx | Ingress without exposing raw node ports |

## Integration Pattern

All external systems are accessed **only through the Platform API** (Spring Boot). The browser never talks directly to the Docker socket, kube-apiserver, or Prometheus with admin credentials.

```text
UI → Platform API → Adapter → External System
                 ↘ Port interface (mock allowed only before system exists)
```

### Adapters (v1)

| Adapter | Target |
|---------|--------|
| `DockerAdapter` | Docker Engine API |
| `KubernetesAdapter` | k3s API |
| `PrometheusAdapter` | Prometheus HTTP API |
| `LokiAdapter` | Loki Query API |
| `AlertmanagerAdapter` | Alertmanager API |
| `GitHubActionsAdapter` | GitHub REST API |
| `DiscordNotifier` | Webhook |

## Deployment Topology (v1 target)

Single Ubuntu host:

1. Docker Engine runs platform services (or mixed with k3s as decided in compose/k8s docs).
2. k3s runs application workloads and optionally platform components.
3. Prometheus scrapes node, cAdvisor, Spring Actuator, and kube components.
4. Promtail ships container/pod logs to Loki.
5. Alertmanager routes to Discord.

Exact compose vs k8s split: see `docs/docker.md` and `docs/kubernetes.md` (filled in Steps 5–6).

## Security Boundaries

- JWT for human users (ADMIN / VIEWER).
- Mutations (restart, delete, deploy) require ADMIN.
- Secrets via environment variables / host secret files — never in git.
- Host-level sockets (docker.sock, kubeconfig) mounted only into the API container, not the frontend.

## Realtime Model

1. Prefer WebSocket push for metrics, logs, deploy progress, pod/container status.
2. On WS failure, UI falls back to React Query polling (default 5s, Settings-configurable).

## Demo-Critical Paths

Interview demo depends on these end-to-end paths remaining healthy:

1. Push → Actions → image → rolling update → health → Discord
2. Pod delete → kube controller recreate → metrics/logs/alerts visible in UI

See [demo-scenario.md](./demo-scenario.md).
