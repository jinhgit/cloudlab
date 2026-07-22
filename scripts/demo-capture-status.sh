#!/usr/bin/env bash
# Capture live demo evidence (API snapshots) for README / interview folder.
# Does not upload secrets. Safe to commit generated markdown.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT="$ROOT/docs/assets/demo"
mkdir -p "$OUT"
TS=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

json_get() {
  local url="$1"
  curl -fsS --max-time 5 "$url" 2>/dev/null || echo '{"error":"unreachable"}'
}

STATUS=$(json_get "http://127.0.0.1:8080/api/server/status")
HEALTH=$(json_get "http://127.0.0.1:8080/actuator/health")
DOCKER_N=$(json_get "http://127.0.0.1:8080/api/docker/containers" | python3 -c "import sys,json; d=json.load(sys.stdin); print(len(d.get('data') or []) if d.get('success') else 'n/a')" 2>/dev/null || echo n/a)
PROM=$(json_get "http://127.0.0.1:9090/-/ready")
LOKI=$(json_get "http://127.0.0.1:3100/ready")

# Pretty status table
python3 - <<PY
import json, pathlib
from datetime import datetime, timezone

out = pathlib.Path("$OUT")
status_raw = '''$STATUS'''
try:
    status = json.loads(status_raw)
except Exception:
    status = {"success": False, "raw": status_raw[:200]}

data = status.get("data") or {}
lines = []
lines.append("# CloudLab demo status capture")
lines.append("")
lines.append(f"- **Captured (UTC):** $TS")
lines.append(f"- **Generator:** \`scripts/demo-capture-status.sh\`")
lines.append("")
lines.append("## Platform API \`/api/server/status\`")
lines.append("")
lines.append("| Field | Value |")
lines.append("|-------|-------|")
for k in ["prometheus","loki","alertmanager","docker","kubernetes","cpuPercent","memoryPercent","containersRunning","containersTotal","podsTotal","alertsFiring"]:
    v = data.get(k, "—")
    lines.append(f"| \`{k}\` | {v} |")
lines.append("")
lines.append(f"- Docker containers (API count): **$DOCKER_N**")
lines.append(f"- Prometheus ready body: \`{repr('''$PROM''')[:80]}\`")
lines.append(f"- Loki ready body: \`{repr('''$LOKI''')[:80]}\`")
lines.append("")
lines.append("## Actuator")
lines.append("")
lines.append("\`\`\`json")
try:
    lines.append(json.dumps(json.loads('''$HEALTH'''), indent=2)[:1500])
except Exception:
    lines.append('''$HEALTH'''[:500])
lines.append("\`\`\`")
lines.append("")
lines.append("## Screenshot checklist (you capture once)")
lines.append("")
lines.append("| File | What to shoot |")
lines.append("|------|----------------|")
lines.append("| \`dashboard.png\` | Home: CPU/Mem + integration badges |")
lines.append("| \`monitoring.png\` | Monitoring charts |")
lines.append("| \`docker.png\` | Docker container list |")
lines.append("| \`logs.png\` | Logs page with backend lines |")
lines.append("| \`actions-ci.png\` | GitHub Actions green CI run |")
lines.append("| \`discord-alert.png\` | Discord message from demo-discord-test.sh |")
lines.append("")
lines.append("Place PNGs in \`docs/assets/demo/\` then they render on README.")
lines.append("")

(out / "latest-status.md").write_text("\n".join(lines) + "\n", encoding="utf-8")
(out / "server-status.json").write_text(json.dumps(status, indent=2)[:8000], encoding="utf-8")
print("Wrote", out / "latest-status.md")
print("Wrote", out / "server-status.json")
PY

# CI badge helper note
cat > "$OUT/README.md" <<'EOF'
# demo/ — Interview evidence assets

## Auto-generated (commit OK)

| File | Source |
|------|--------|
| `latest-status.md` | `./scripts/demo-capture-status.sh` |
| `server-status.json` | same |
| `discord-test-receipt.md` | `./scripts/demo-discord-test.sh` |

## You capture once (PNG)

| File | How |
|------|-----|
| `dashboard.png` | http://localhost:3000 |
| `monitoring.png` | /monitoring |
| `docker.png` | /docker |
| `logs.png` | /logs |
| `actions-ci.png` | GitHub → Actions → green CI |
| `discord-alert.png` | Discord channel after webhook test |

```bash
./scripts/demo-reset.sh
./scripts/demo-capture-status.sh
./scripts/demo-discord-test.sh   # needs DISCORD_WEBHOOK_URL
# take screenshots into this folder
```

Keep images reasonably small (&lt; 1MB each). Redact personal Discord server names if needed.
EOF

echo "Done. Review docs/assets/demo/latest-status.md"
