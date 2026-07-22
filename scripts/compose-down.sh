#!/usr/bin/env bash
# Stop CloudLab Compose stack. Pass -v to remove volumes.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

if [[ "${1:-}" == "-v" || "${1:-}" == "--volumes" ]]; then
  docker compose down -v
else
  docker compose down
fi
