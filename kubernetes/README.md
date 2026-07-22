# kubernetes/

k3s / Kubernetes manifests and Helm chart for CloudLab.

| Path | Description |
|------|-------------|
| `manifests/` | Ordered YAML (namespace → data → apps → ingress) |
| `kustomization.yaml` | `kubectl apply -k` entrypoint |
| `helm/cloudlab/` | Helm chart (same topology) |

**Docs:** [docs/kubernetes.md](../docs/kubernetes.md)

```bash
# secret + apply
../scripts/k8s-apply.sh manifests
# or
../scripts/k8s-apply.sh helm
```

Secrets are **not** committed. Use `../scripts/k8s-create-secret.sh` or Helm `--set secrets.*`.
