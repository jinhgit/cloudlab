#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

if [[ "${1:-}" == "-v" || "${1:-}" == "--volumes" ]]; then
  docker compose -f docker-compose.yml -f docker-compose.monitoring.yml down -v
else
  docker compose -f docker-compose.yml -f docker-compose.monitoring.yml down
fi
