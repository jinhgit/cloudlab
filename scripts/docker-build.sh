#!/usr/bin/env bash
# CloudLab image build helper (repo root)
# Usage:
#   ./scripts/docker-build.sh              # both
#   ./scripts/docker-build.sh backend
#   ./scripts/docker-build.sh frontend
#   ./scripts/docker-build.sh backend v0.1.0
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

TARGET="${1:-all}"
TAG="${2:-local}"
API_URL="${NEXT_PUBLIC_API_URL:-http://localhost:8080}"
WS_URL="${NEXT_PUBLIC_WS_URL:-ws://localhost:8080/ws}"

build_backend() {
  echo "==> Building cloudlab-backend:${TAG}"
  docker build \
    -f docker/Dockerfile.backend \
    -t "cloudlab-backend:${TAG}" \
    .
}

build_frontend() {
  echo "==> Building cloudlab-frontend:${TAG}"
  docker build \
    -f docker/Dockerfile.frontend \
    -t "cloudlab-frontend:${TAG}" \
    --build-arg "NEXT_PUBLIC_API_URL=${API_URL}" \
    --build-arg "NEXT_PUBLIC_WS_URL=${WS_URL}" \
    .
}

case "${TARGET}" in
  backend)  build_backend ;;
  frontend) build_frontend ;;
  all)
    build_backend
    build_frontend
    ;;
  *)
    echo "Usage: $0 [all|backend|frontend] [tag]" >&2
    exit 1
    ;;
esac

echo "Done."
