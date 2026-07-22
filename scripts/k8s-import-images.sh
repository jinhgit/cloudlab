#!/usr/bin/env bash
# Build local images and import into k3s containerd.
# Run on the k3s node (or machine where docker + k3s share host).
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

TAG="${1:-local}"

echo "==> Building images (tag=${TAG})"
./scripts/docker-build.sh all "$TAG"

if command -v k3s >/dev/null 2>&1; then
  echo "==> Importing into k3s (ctr)"
  docker save "cloudlab-backend:${TAG}" | sudo k3s ctr images import -
  docker save "cloudlab-frontend:${TAG}" | sudo k3s ctr images import -
  echo "Imported cloudlab-backend:${TAG} cloudlab-frontend:${TAG}"
elif command -v k3d >/dev/null 2>&1; then
  CLUSTER="${K3D_CLUSTER:-k3s-default}"
  echo "==> Importing into k3d cluster ${CLUSTER}"
  k3d image import "cloudlab-backend:${TAG}" "cloudlab-frontend:${TAG}" -c "$CLUSTER"
else
  echo "k3s/k3d not found. Images built only. Load them into your cluster runtime manually."
  echo "  docker images | grep cloudlab"
fi
