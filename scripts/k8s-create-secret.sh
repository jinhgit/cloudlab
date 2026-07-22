#!/usr/bin/env bash
# Create / update cloudlab-secret in namespace cloudlab from env or defaults.
set -euo pipefail

NS="${NAMESPACE:-cloudlab}"
POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-change-me}"
REDIS_PASSWORD="${REDIS_PASSWORD:-}"
JWT_SECRET="${JWT_SECRET:-change-me-to-a-long-random-string}"

kubectl get ns "$NS" >/dev/null 2>&1 || kubectl create namespace "$NS"

kubectl -n "$NS" create secret generic cloudlab-secret \
  --from-literal=POSTGRES_PASSWORD="$POSTGRES_PASSWORD" \
  --from-literal=REDIS_PASSWORD="$REDIS_PASSWORD" \
  --from-literal=JWT_SECRET="$JWT_SECRET" \
  --dry-run=client -o yaml | kubectl apply -f -

echo "Secret cloudlab-secret applied in namespace $NS"
