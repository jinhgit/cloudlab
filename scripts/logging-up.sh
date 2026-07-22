#!/usr/bin/env bash
# Start app stack + monitoring (optional) + Loki/Promtail.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

if [[ ! -f .env ]]; then
  cp .env.example .env
fi

chmod +x monitoring/alertmanager/entrypoint.sh 2>/dev/null || true

FILES=(
  -f docker-compose.yml
  -f docker-compose.monitoring.yml
  -f docker-compose.logging.yml
)

echo "==> docker compose (app + monitoring + logging)"
docker compose "${FILES[@]}" up -d --build

echo
docker compose "${FILES[@]}" ps
echo
echo "Loki:     http://localhost:3100/ready"
echo "Promtail: http://localhost:9080/ready"
echo "Grafana:  http://localhost:3001  (Explore → Loki)"
echo
echo "Query labels:"
echo "  curl -sG http://localhost:3100/loki/api/v1/labels"
echo "Query backend logs (last 5m):"
echo "  curl -sG http://localhost:3100/loki/api/v1/query_range --data-urlencode 'query={compose_service=\"backend\"}' --data-urlencode 'limit=20'"
