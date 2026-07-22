# Monitoring (Step 7)

Prometheus-first observability for CloudLab. Grafana is auxiliary; CloudLab Dashboard Monitoring page becomes primary UI in Step 10.

## Components

| Service | Port (host) | Role |
|---------|-------------|------|
| **Prometheus** | 9090 | scrape · store · rules |
| **Alertmanager** | 9093 | route alerts (Discord optional) |
| **Grafana** | 3001 | provisioned dashboards |
| **Node Exporter** | 9100 | host metrics |
| **cAdvisor** | 8081 | container metrics |
| **Spring Actuator** | 8080 `/actuator/prometheus` | JVM / HTTP metrics |

## Layout

```text
monitoring/
├── prometheus/
│   ├── prometheus.yml
│   └── rules/cloudlab-alerts.yml
├── alertmanager/
│   ├── alertmanager.yml
│   ├── alertmanager.template.yml
│   └── entrypoint.sh
└── grafana/provisioning/
    ├── datasources/
    └── dashboards/
```

## Quick start

```bash
# requires app stack images (backend healthy)
./scripts/monitoring-up.sh

# or
docker compose -f docker-compose.yml -f docker-compose.monitoring.yml up -d
```

Stop:

```bash
./scripts/monitoring-down.sh
```

## Verify scrape path

```bash
# Prometheus ready
curl -s http://localhost:9090/-/ready

# Targets API — expect cloudlab-backend, node-exporter, cadvisor, prometheus up
curl -s http://localhost:9090/api/v1/targets | python3 -c "
import sys,json
d=json.load(sys.stdin)
for t in d['data']['activeTargets']:
    print(t['labels'].get('job'), t['health'], t.get('lastError',''))
"

# Sample JVM metric
curl -s 'http://localhost:9090/api/v1/query?query=jvm_memory_used_bytes' | head -c 400; echo

# Actuator direct
curl -s http://localhost:8080/actuator/prometheus | head
```

Browser:

- Targets: http://localhost:9090/targets  
- Grafana: http://localhost:3001 (default `admin` / `admin` from `.env`)  
- Dashboard folder **CloudLab → CloudLab Overview**

## Scrape jobs

| Job | Target | Path |
|-----|--------|------|
| `cloudlab-backend` | `backend:8080` | `/actuator/prometheus` |
| `node-exporter` | `node-exporter:9100` | `/metrics` |
| `cadvisor` | `cadvisor:8080` | `/metrics` |
| `prometheus` | `localhost:9090` | `/metrics` |
| `alertmanager` | `alertmanager:9093` | `/metrics` |

## Alert rules (initial)

| Alert | Severity | Meaning |
|-------|----------|---------|
| `CloudLabBackendDown` | critical | backend scrape down 1m |
| `CloudLabBackendHighErrorRate` | warning | 5xx ratio high |
| `NodeHighCpu` / `NodeHighMemory` | warning | host pressure |
| `TargetDown` | warning | any scrape target down |

Discord: set `DISCORD_WEBHOOK_URL` in `.env` and recreate alertmanager.

## Env keys

| Key | Default | Notes |
|-----|---------|-------|
| `PROMETHEUS_PORT` | 9090 | |
| `ALERTMANAGER_PORT` | 9093 | |
| `GRAFANA_PORT` | 3001 | avoids clash with Next.js :3000 |
| `GRAFANA_ADMIN_USER` / `PASSWORD` | admin / admin | change in real deploys |
| `DISCORD_WEBHOOK_URL` | empty | optional |
| `CADVISOR_PORT` | 8081 | host map (internal still 8080) |

## Notes / platform caveats

- **Docker Desktop (macOS):** Node Exporter / cAdvisor see the **Linux VM**, not full macOS host metrics. Still valid for container lab demos.
- Backend metrics require Micrometer Prometheus registry (already on classpath).
- Actuator `/actuator/prometheus` is public in Step 3 security config for scrape; lock down later if needed behind network policy.

## k8s

Compose is the Step 7 primary path. Helm/Prometheus Operator for k3s can follow once the scrape model is stable (optional later PR).

## Next

- Step 8: Loki + Promtail (logs)
- Step 10: Dashboard Monitoring page → Prometheus `query_range` API
