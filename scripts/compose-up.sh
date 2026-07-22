#!/usr/bin/env bash
# Start CloudLab Docker Compose stack from repo root.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

if [[ ! -f .env ]]; then
  echo "==> Creating .env from .env.example"
  cp .env.example .env
fi

echo "==> docker compose up -d --build"
docker compose up -d --build

echo
echo "Services:"
docker compose ps
echo
echo "API health:  curl -s http://localhost:8080/actuator/health"
echo "API ping:    curl -s http://localhost:8080/api/health"
echo "Dashboard:   http://localhost:3000"
