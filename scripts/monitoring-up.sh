#!/usr/bin/env bash
# Start app stack + monitoring overlay.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

if [[ ! -f .env ]]; then
  cp .env.example .env
fi

# ensure entrypoint is executable in bind mount
chmod +x monitoring/alertmanager/entrypoint.sh

echo "==> docker compose (app + monitoring)"
docker compose \
  -f docker-compose.yml \
  -f docker-compose.monitoring.yml \
  up -d --build

echo
docker compose -f docker-compose.yml -f docker-compose.monitoring.yml ps
echo
echo "Prometheus:   http://localhost:9090"
echo "Alertmanager: http://localhost:9093"
echo "Grafana:      http://localhost:3001  (admin / from .env)"
echo "Targets:      http://localhost:9090/targets"
echo
echo "Quick check:"
echo "  curl -s http://localhost:9090/api/v1/targets | head"
