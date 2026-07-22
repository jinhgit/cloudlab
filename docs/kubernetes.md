# Kubernetes / k3s (Step 6)

CloudLab 워크로드를 **k3s** 단일 노드에 배포하기 위한 매니페스트·Helm 차트.

## Layout

```text
kubernetes/
├── manifests/           # plain YAML (kustomize 입력)
│   ├── 00-namespace.yaml
│   ├── 01-configmap.yaml
│   ├── 02-secret.example.yaml
│   ├── 10-postgres.yaml
│   ├── 11-redis.yaml
│   ├── 20-backend.yaml
│   ├── 21-frontend.yaml
│   ├── 30-ingress.yaml
│   └── 31-nodeport.yaml
├── kustomization.yaml
├── helm/cloudlab/       # Helm chart (동일 리소스 패키징)
└── README.md
```

## Components

| Workload | Kind | Notes |
|----------|------|-------|
| `cloudlab-postgres` | Deployment + PVC + Service | platform DB |
| `cloudlab-redis` | Deployment + PVC + Service | cache |
| `cloudlab-backend` | Deployment + Service | RollingUpdate, probes on Actuator |
| `cloudlab-frontend` | Deployment + Service | RollingUpdate |
| Ingress | Traefik (k3s default) | host `cloudlab.local` |
| NodePort | 30080 (UI) / 30088 (API) | 로컬 접근 편의 |

Security: backend/frontend `runAsUser: 10001`, non-root, drop ALL caps.

## Prerequisites

1. **k3s** (or any Kubernetes 1.28+)
2. `kubectl`, optional `helm`
3. Local images: `cloudlab-backend:local`, `cloudlab-frontend:local`

### Install k3s (Linux server)

```bash
curl -sfL https://get.k3s.io | sh -
sudo k3s kubectl get nodes
# kubeconfig
mkdir -p ~/.kube
sudo cat /etc/rancher/k3s/k3s.yaml > ~/.kube/config
# fix server address if needed
```

macOS 개발 머신에서는 보통 **원격 Ubuntu에 k3s** 를 두고 `kubectl` 만 연결하거나, `k3d` 로 로컬 클러스터를 띄운다.

## Deploy (manifests / kustomize)

```bash
# 1) images
./scripts/k8s-import-images.sh local

# 2) secret + apply
./scripts/k8s-apply.sh manifests

# 3) status
kubectl -n cloudlab get pods,svc,ing,pvc
kubectl -n cloudlab rollout status deploy/cloudlab-backend
```

## Deploy (Helm)

```bash
./scripts/k8s-import-images.sh local
./scripts/k8s-apply.sh helm
# or
helm upgrade --install cloudlab kubernetes/helm/cloudlab -n cloudlab --create-namespace
```

Override secrets (recommended):

```bash
helm upgrade --install cloudlab kubernetes/helm/cloudlab -n cloudlab --create-namespace \
  --set secrets.postgresPassword='strong' \
  --set secrets.jwtSecret='longer-random-secret'
```

## Access

| Method | URL |
|--------|-----|
| NodePort UI | `http://<node-ip>:30080` |
| NodePort API | `http://<node-ip>:30088/actuator/health` |
| Ingress | `http://cloudlab.local` (`/etc/hosts` → node IP) |

Port-forward without NodePort:

```bash
kubectl -n cloudlab port-forward svc/cloudlab-frontend 3000:3000
kubectl -n cloudlab port-forward svc/cloudlab-backend 8080:8080
```

## Demo: pod delete → self-heal

면접 데모 시나리오와 동일:

```bash
kubectl -n cloudlab delete pod -l app.kubernetes.io/name=cloudlab-backend
kubectl -n cloudlab get pods -w
# Deployment controller recreates pod → Ready
```

## Tear down

```bash
./scripts/k8s-delete.sh manifests
# or
./scripts/k8s-delete.sh helm
```

## Design decisions

| Decision | Choice | Why |
|----------|--------|-----|
| Orchestrator | k3s | single-node self-hosted (PRD) |
| Packaging | plain YAML + Helm | ops flexibility + portfolio depth |
| Local images | `*:local` + import | no registry required for lab |
| Exposure | NodePort + Ingress | demo without Cloudflare first |
| Probes | Actuator liveness/readiness | RollingUpdate safety |
| Secrets | separate from git | security |

## Validation without cluster

This repo machine may not have k3s. Validate templates with:

```bash
kubectl apply -k kubernetes/ --dry-run=client
helm lint kubernetes/helm/cloudlab
helm template cloudlab kubernetes/helm/cloudlab | head
```

## Next

- Step 7: Monitoring (Prometheus scrape k8s workloads)
- Step 9: CI/CD Rolling Update against this Deployment
