# Logging (Step 8)

Loki + Promtail collect container logs for CloudLab. Grafana Explore and (later) Dashboard Logs page query Loki.

## Components

| Service | Port | Role |
|---------|------|------|
| **Loki** | 3100 | log store · query API |
| **Promtail** | 9080 | Docker SD scrape → push to Loki |
| **Grafana** | 3001 | Explore / dashboards (Loki DS) |

## Layout

```text
logging/
├── loki/loki-config.yml
└── promtail/promtail-config.yml
```

## Quick start

```bash
./scripts/logging-up.sh
# starts app + monitoring + logging overlays
```

Or manually:

```bash
docker compose \
  -f docker-compose.yml \
  -f docker-compose.monitoring.yml \
  -f docker-compose.logging.yml \
  up -d
```

## Verify

```bash
# Loki ready
curl -s http://localhost:3100/ready

# Labels (expect container, compose_service, job, ...)
curl -sG http://localhost:3100/loki/api/v1/labels

# Label values for compose_service
curl -sG http://localhost:3100/loki/api/v1/label/compose_service/values

# Backend logs (LogQL) — start/end in nanoseconds
python3 - <<'PY'
import time, urllib.parse, urllib.request, json
start=int((time.time()-3600)*1e9); end=int(time.time()*1e9)
q=urllib.parse.urlencode({"query":'{compose_service="backend"}',"limit":"10","start":start,"end":end})
d=json.load(urllib.request.urlopen(f"http://localhost:3100/loki/api/v1/query_range?{q}"))
for s in d["data"]["result"]:
    for _, line in s["values"][-3:]:
        print(line[:160])
PY
```

Grafana: http://localhost:3001 → **Explore** → datasource **Loki**

Example LogQL:

```logql
{compose_service="backend"}
{compose_project="cloudlab"} |= "ERROR"
{container="cloudlab-backend"} |~ "(?i)error|exception"
```

## Labels (Promtail)

| Label | Source |
|-------|--------|
| `container` | Docker container name |
| `compose_service` | compose service name |
| `compose_project` | compose project (`cloudlab`) |
| `stream` | stdout / stderr |
| `job` | `cloudlab-docker` |
| `level` | best-effort regex from message |

Only containers with `compose_project=cloudlab` are kept (noise reduction).

## Retention

Loki config: **7 days** (`retention_period: 168h`). Adjust in `logging/loki/loki-config.yml`.

## Env

| Key | Default |
|-----|---------|
| `LOKI_PORT` | 3100 |
| `PROMTAIL_PORT` | 9080 |
| `LOKI_URL` | http://localhost:3100 (Platform API later) |

## Notes

- Promtail needs **docker.sock** + container log paths (read-only).
- On Docker Desktop macOS, paths map into the Linux VM; Docker SD still works.
- Step 10: Platform API `GET /api/logs` → Loki Query API → Dashboard Logs page.

## Next

- Step 9: CI/CD
- Step 10: Dashboard log streaming UI
