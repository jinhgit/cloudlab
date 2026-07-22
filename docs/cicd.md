# CI/CD (Step 9)

GitHub Actions pipeline for CloudLab.

```text
Push / PR
   │
   ▼
 CI: Gradle test · Next build · Docker build (no push)
   │
   ▼  (main only)
 CD: Quality gate → Registry push → SSH deploy (optional)
       → Health check → Discord notify
```

## Workflows

| File | Trigger | Purpose |
|------|---------|---------|
| [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) | PR + push `main` | test / lint / image build |
| [`.github/workflows/cd.yml`](../.github/workflows/cd.yml) | push `main` + manual | push images · deploy · notify |

## Jobs

### CI

1. **backend-test** — `./gradlew test` + `bootJar`
2. **frontend-build** — `npm ci` · lint · `next build`
3. **docker-build** — multi-stage images (push=false)

### CD

1. **gate** — re-test on main
2. **build-and-push** — tag `SHA` + `latest` → Docker Hub
3. **deploy** — SSH rolling update (`compose` or `k8s`) when secrets present
4. **notify** — Discord embed (success/fail)

Deploy is **skipped** (not failed) when `DEPLOY_HOST` / `DEPLOY_SSH_KEY` are unset — so portfolio repos can run image CI without a server.

## Repository secrets

| Secret | Required for | Description |
|--------|----------------|-------------|
| `DOCKERHUB_USERNAME` | push | Docker Hub user |
| `DOCKERHUB_TOKEN` | push | Access token |
| `DEPLOY_HOST` | deploy | Server hostname/IP |
| `DEPLOY_USER` | deploy | SSH user (default ubuntu) |
| `DEPLOY_PORT` | deploy | SSH port (default 22) |
| `DEPLOY_SSH_KEY` | deploy | Private key (PEM) |
| `DEPLOY_MODE` | deploy | `compose` (default) or `k8s` |
| `HEALTH_URL` | deploy | e.g. `http://host:8080/actuator/health` |
| `DISCORD_WEBHOOK_URL` | notify | Discord channel webhook |

## Repository variables (optional)

| Variable | Default | Description |
|----------|---------|-------------|
| `NEXT_PUBLIC_API_URL` | `http://localhost:8080` | baked into frontend image |
| `NEXT_PUBLIC_WS_URL` | `ws://localhost:8080/ws` | baked into frontend image |

Set in GitHub → Settings → Secrets and variables → Actions.

```bash
# CLI examples
gh secret set DOCKERHUB_USERNAME -b"youruser"
gh secret set DOCKERHUB_TOKEN -b"dckr_pat_..."
gh variable set NEXT_PUBLIC_API_URL -b"https://api.example.com"
```

## Image tags

| Tag | When |
|-----|------|
| `<git-sha7>` | every CD run |
| `latest` | main only |

Examples: `jinhgit/cloudlab-backend:a1b2c3d`

## Remote layout (compose mode)

On the server:

```bash
# /opt/cloudlab (or CLOUDLAB_DIR)
git clone https://github.com/jinhgit/cloudlab.git /opt/cloudlab
# copy .env with secrets
# first boot:
docker compose -f docker-compose.prod.yml up -d
```

CD then pulls new tags and recreates `backend` / `frontend`.

### k8s mode

```bash
# secrets.DEPLOY_MODE=k8s
kubectl -n cloudlab set image deploy/cloudlab-backend backend=$BACKEND_IMAGE
kubectl -n cloudlab set image deploy/cloudlab-frontend frontend=$FRONTEND_IMAGE
```

Images must be pullable by the node (public Hub or private pull secret).

## Local scripts

| Script | Role |
|--------|------|
| `scripts/ci/remote-deploy.sh` | SSH deploy body |
| `scripts/ci/health-check.sh` | poll Actuator UP |

```bash
./scripts/ci/health-check.sh http://localhost:8080/actuator/health
```

## Permissions note

Creating/updating `.github/workflows/*` via `git push` requires a token with the **`workflow`** scope:

```bash
gh auth refresh -h github.com -s workflow
```

## Interview talking points

1. PR = CI only (no prod risk)
2. main = immutable image tags (SHA) + latest
3. Deploy is optional/idempotent SSH or k8s rollout
4. Health gate before success
5. Discord closes the ops loop (PRD demo)

## Next

- Step 10: Dashboard Deployments page → GitHub Actions API
