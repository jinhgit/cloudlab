#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

FILES=(
  -f docker-compose.yml
  -f docker-compose.monitoring.yml
  -f docker-compose.logging.yml
)

if [[ "${1:-}" == "-v" || "${1:-}" == "--volumes" ]]; then
  docker compose "${FILES[@]}" down -v
else
  docker compose "${FILES[@]}" down
fi
