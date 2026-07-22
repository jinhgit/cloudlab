# Dashboard Wiring (Step 10)

Platform API adapters feed the Next.js Dashboard with **real** upstream data.

## Architecture

```text
Browser Dashboard
    → Spring Boot /api/*
        → DockerAdapter      (docker.sock)
        → KubernetesAdapter  (kubeconfig / in-cluster)
        → PrometheusAdapter  (HTTP)
        → LokiAdapter        (HTTP)
        → AlertmanagerAdapter
        → GitHubActionsAdapter (optional token)
        → DataSource / Redis  (platform DB/cache)
```

## API map

| UI page | Backend |
|---------|---------|
| Dashboard | `GET /api/server/status` |
| Docker | `GET/POST /api/docker/containers…` |
| Kubernetes | `GET/DELETE/POST /api/kubernetes/…` |
| Monitoring | `GET /api/prometheus/*` |
| Logs | `GET /api/logs` |
| Alerts | `GET /api/alerts` |
| Deployments | `GET /api/deployments` |
| Database | `GET /api/database/status` |
| Redis | `GET /api/redis/status` |

## Compose requirements

Backend needs:

```yaml
volumes:
  - /var/run/docker.sock:/var/run/docker.sock
environment:
  CLOUDLAB_PROMETHEUS_URL: http://prometheus:9090
  CLOUDLAB_LOKI_URL: http://loki:3100
  CLOUDLAB_ALERTMANAGER_URL: http://alertmanager:9093
```

Start full stack:

```bash
docker compose \
  -f docker-compose.yml \
  -f docker-compose.monitoring.yml \
  -f docker-compose.logging.yml \
  up -d --build
```

## Security note

`cloudlab.security.open-api=true` (default) exposes `/api/**` for lab demos.
Set `CLOUDLAB_SECURITY_OPEN_API=false` after JWT lands.

## Graceful degradation

| Upstream down | UI behavior |
|---------------|-------------|
| Docker socket missing | Docker page error banner |
| No kubeconfig | Kubernetes empty / error |
| Prometheus down | metrics zero / 502 |
| No GitHub token | Deployments “not configured” |
