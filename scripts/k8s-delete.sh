#!/usr/bin/env bash
# Tear down CloudLab from the cluster.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

MODE="${1:-manifests}"

case "$MODE" in
  manifests|kustomize)
    kubectl delete -k kubernetes/ --ignore-not-found
    kubectl -n cloudlab delete secret cloudlab-secret --ignore-not-found
    ;;
  helm)
    helm uninstall cloudlab -n cloudlab || true
    kubectl delete ns cloudlab --ignore-not-found
    ;;
  *)
    echo "Usage: $0 [manifests|helm]" >&2
    exit 1
    ;;
esac

echo "Done."
