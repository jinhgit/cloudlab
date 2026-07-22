# scripts/

Operational scripts. Keep them idempotent and documented.

## Interview demo (5 min)

| Script | Purpose |
|--------|---------|
| `demo-reset.sh` | Bring Compose stack to known-good state |
| `demo-run.sh` | Preflight + timed speaking cues (+ optional docker inject) |
| `demo-discord-test.sh` | Real Discord webhook message for evidence |
| `demo-capture-status.sh` | Write `docs/assets/demo/latest-status.md` + SVG-friendly JSON |

See [docs/demo-playbook.md](../docs/demo-playbook.md).

## Other

| Area | Scripts |
|------|---------|
| Compose | `compose-up.sh`, `monitoring-up.sh`, `logging-up.sh` |
| k8s | `k8s-apply.sh`, `k8s-import-images.sh` |
| CI | `ci/health-check.sh` |
| IaC (v2) | `iac-free-check.sh` ($0), `iac-bootstrap.sh` |
