#!/usr/bin/env bash
# Apply CloudLab Kubernetes manifests (kustomize) to the current context.
# Prerequisites: kubectl, cluster (k3s), local images imported (see docs/kubernetes.md)
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

MODE="${1:-manifests}" # manifests | helm

echo "==> context: $(kubectl config current-context 2>/dev/null || echo 'none')"

case "$MODE" in
  manifests|kustomize)
    ./scripts/k8s-create-secret.sh
    echo "==> kubectl apply -k kubernetes/"
    kubectl apply -k kubernetes/
    ;;
  helm)
    echo "==> helm upgrade --install cloudlab kubernetes/helm/cloudlab -n cloudlab --create-namespace"
    helm upgrade --install cloudlab kubernetes/helm/cloudlab \
      -n cloudlab \
      --create-namespace \
      --wait \
      --timeout 5m
    ;;
  *)
    echo "Usage: $0 [manifests|helm]" >&2
    exit 1
    ;;
esac

echo
echo "==> resources"
kubectl -n cloudlab get all,ing,pvc 2>/dev/null || true
echo
echo "NodePort UI:  http://<node-ip>:30080"
echo "NodePort API: http://<node-ip>:30088/actuator/health"
echo "Ingress host: http://cloudlab.local (add to /etc/hosts)"
