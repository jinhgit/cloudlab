# CloudLab demo status capture

- **Captured (UTC):** 2026-07-22T18:13:58Z
- **Generator:** `scripts/demo-capture-status.sh`

## Platform API `/api/server/status`

| Field | Value |
|-------|-------|
| `prometheus` | True |
| `loki` | True |
| `alertmanager` | True |
| `docker` | True |
| `kubernetes` | False |
| `cpuPercent` | 6.5 |
| `memoryPercent` | 40.6 |
| `containersRunning` | 11 |
| `containersTotal` | 11 |
| `podsTotal` | 0 |
| `alertsFiring` | 0 |

- Docker containers (API count): **11**
- Prometheus ready body: `'Prometheus Server is Ready.'`
- Loki ready body: `'ready'`

## Actuator

```json
{
  "status": "UP",
  "groups": [
    "liveness",
    "readiness"
  ],
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 134681202688,
        "free": 115794706432,
        "threshold": 10485760,
        "path": "/app/.",
        "exists": true
      }
    },
    "livenessState": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    },
    "readinessState": {
      "status": "UP"
    },
    "redis": {
      "status": "UP",
      "details": {
        "version": "7.4.9"
      }
    },
    "ssl": {
      "status": "UP",
      "details": {
        "validChains": [],
        "invalidChains": []
      }
    }
  }
}
```

## Screenshot checklist (you capture once)

| File | What to shoot |
|------|----------------|
| `dashboard.png` | Home: CPU/Mem + integration badges |
| `monitoring.png` | Monitoring charts |
| `docker.png` | Docker container list |
| `logs.png` | Logs page with backend lines |
| `actions-ci.png` | GitHub Actions green CI run |
| `discord-alert.png` | Discord message from demo-discord-test.sh |

Place PNGs in `docs/assets/demo/` then they render on README.

