# e2e/ — Playwright golden-path smoke

## Prerequisites

```bash
# repo root — Dashboard on :3000
./scripts/demo-reset.sh
```

## Run

```bash
# from frontend (wrapper)
cd frontend && npm run test:e2e

# or directly
cd e2e && npm install && npx playwright install chromium && npm test

# custom URL
BASE_URL=http://127.0.0.1:3000 npm test
```

## Scope

| Spec | Coverage |
|------|----------|
| `dashboard-smoke.spec.ts` | `/` widgets · `/docker` · `/monitoring` |

Thin smoke only — not full regression.

## CI

Optional / manual. Prefer local run after `demo-reset` (avoid flaky PR gates).
