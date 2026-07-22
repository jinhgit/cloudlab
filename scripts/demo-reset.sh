#!/usr/bin/env bash
# CloudLab interview demo — one-shot stack reset to a known-good state.
# Usage (repo root):
#   ./scripts/demo-reset.sh
#   ./scripts/demo-reset.sh --quick   # recreate app only, keep volumes
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

QUICK=false
if [[ "${1:-}" == "--quick" ]]; then
  QUICK=true
fi

COMPOSE=(
  docker compose
  -f docker-compose.yml
  -f docker-compose.monitoring.yml
  -f docker-compose.logging.yml
)

green() { printf '\033[32m%s\033[0m\n' "$*"; }
yellow() { printf '\033[33m%s\033[0m\n' "$*"; }
red() { printf '\033[31m%s\033[0m\n' "$*"; }

echo "=========================================="
echo " CloudLab demo-reset"
echo "=========================================="

if ! command -v docker >/dev/null 2>&1; then
  red "docker not found"
  exit 1
fi

if [[ ! -f .env ]]; then
  yellow "No .env — copying .env.example"
  cp .env.example .env
fi

if [[ "$QUICK" == "true" ]]; then
  yellow "Quick mode: recreate backend/frontend only"
  "${COMPOSE[@]}" up -d --build --force-recreate backend frontend
else
  yellow "Full mode: ensure full stack is up (app + monitoring + logging)"
  "${COMPOSE[@]}" up -d --build
fi

echo
echo "==> Waiting for health endpoints..."
wait_http() {
  local url="$1" name="$2" n="${3:-40}"
  for i in $(seq 1 "$n"); do
    if curl -fsS --max-time 3 "$url" >/dev/null 2>&1; then
      green "  [OK] $name"
      return 0
    fi
    sleep 3
  done
  red "  [FAIL] $name ($url)"
  return 1
}

FAIL=0
wait_http "http://127.0.0.1:8080/actuator/health" "Platform API" || FAIL=1
wait_http "http://127.0.0.1:3000/" "Dashboard" || FAIL=1
wait_http "http://127.0.0.1:9090/-/ready" "Prometheus" || FAIL=1
wait_http "http://127.0.0.1:3100/ready" "Loki" || FAIL=1
wait_http "http://127.0.0.1:9093/-/ready" "Alertmanager" || FAIL=1

# Warm APIs used in demo
curl -fsS "http://127.0.0.1:8080/api/server/status" >/dev/null 2>&1 || true
curl -fsS "http://127.0.0.1:8080/api/docker/containers" >/dev/null 2>&1 || true

echo
if [[ "$FAIL" -eq 0 ]]; then
  green "Demo stack READY"
  echo "  Dashboard : http://localhost:3000"
  echo "  API       : http://localhost:8080/api/server/status"
  echo "  Prometheus: http://localhost:9090/targets"
  echo
  echo "Next: ./scripts/demo-run.sh"
  exit 0
else
  red "Some checks failed — run: ${COMPOSE[*]} ps && ${COMPOSE[*]} logs --tail 50"
  exit 1
fi
