#!/usr/bin/env bash
# Remote deploy helper invoked from GitHub Actions CD.
# Env:
#   DEPLOY_HOST, DEPLOY_USER, DEPLOY_PORT
#   BACKEND_IMAGE, FRONTEND_IMAGE, VERSION
#   DEPLOY_MODE=compose|k8s
#   HEALTH_URL (optional, for logging)
set -euo pipefail

: "${DEPLOY_HOST:?DEPLOY_HOST required}"
: "${DEPLOY_USER:=ubuntu}"
: "${DEPLOY_PORT:=22}"
: "${BACKEND_IMAGE:?BACKEND_IMAGE required}"
: "${FRONTEND_IMAGE:?FRONTEND_IMAGE required}"
: "${VERSION:=unknown}"
: "${DEPLOY_MODE:=compose}"

SSH=(ssh -i "${HOME}/.ssh/id_ed25519" -p "${DEPLOY_PORT}"
  -o StrictHostKeyChecking=accept-new
  -o UserKnownHostsFile="${HOME}/.ssh/known_hosts"
  "${DEPLOY_USER}@${DEPLOY_HOST}")

echo "==> Deploy mode=${DEPLOY_MODE} version=${VERSION}"
echo "    backend=${BACKEND_IMAGE}"
echo "    frontend=${FRONTEND_IMAGE}"

if [[ "${DEPLOY_MODE}" == "k8s" ]]; then
  "${SSH[@]}" bash -s <<EOF
set -euo pipefail
export KUBECONFIG="\${KUBECONFIG:-/etc/rancher/k3s/k3s.yaml}"
kubectl -n cloudlab set image deploy/cloudlab-backend backend=${BACKEND_IMAGE}
kubectl -n cloudlab set image deploy/cloudlab-frontend frontend=${FRONTEND_IMAGE}
kubectl -n cloudlab rollout status deploy/cloudlab-backend --timeout=180s
kubectl -n cloudlab rollout status deploy/cloudlab-frontend --timeout=180s
echo "k8s rolling update complete: ${VERSION}"
EOF
else
  # Default: Docker Compose on host (pull + recreate app services)
  "${SSH[@]}" bash -s <<EOF
set -euo pipefail
cd "\${CLOUDLAB_DIR:-/opt/cloudlab}"
export BACKEND_IMAGE="${BACKEND_IMAGE}"
export FRONTEND_IMAGE="${FRONTEND_IMAGE}"
if [ -f docker-compose.prod.yml ]; then
  docker compose -f docker-compose.prod.yml pull backend frontend || true
  docker compose -f docker-compose.prod.yml up -d backend frontend
else
  docker pull "${BACKEND_IMAGE}"
  docker pull "${FRONTEND_IMAGE}"
  docker compose up -d --no-deps backend frontend || {
    docker tag "${BACKEND_IMAGE}" cloudlab-backend:local
    docker tag "${FRONTEND_IMAGE}" cloudlab-frontend:local
    docker compose up -d --no-deps backend frontend
  }
fi
echo "compose deploy complete: ${VERSION}"
EOF
fi

echo "==> Remote deploy finished"
