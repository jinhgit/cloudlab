# Testing (Step 11)

CloudLab 테스트 전략: **핵심 경로 단위/통합** 우선, 실인프라 E2E는 Compose 스모크로 보조.

## Backend (JUnit 5 + MockMvc + Mockito)

```bash
cd backend
./gradlew test
```

| Suite | 내용 |
|-------|------|
| `ApiResponseTest` | 공통 envelope |
| `PlatformOverviewServiceTest` | 집계 로직 (mock adapters) |
| `ServerControllerTest` | `/api/server/status` |
| `DockerControllerTest` | list/restart + 502 upstream |
| `LogsControllerTest` | Loki 프록시 envelope |
| `PrometheusControllerTest` | summary |
| `DatabaseAndRedisControllerTest` | local 프로파일 우아한 저하 |
| `GlobalExceptionHandlerTest` | UpstreamException → 502 |
| `CloudLabApplicationTests` | context + health |

## Frontend (Vitest + Testing Library)

```bash
cd frontend
npm test
```

| Suite | 내용 |
|-------|------|
| `lib/utils.test.ts` | `cn()` |
| `lib/loki.test.ts` | LogQL 결과 파서 |
| `services/api.test.ts` | API base URL / localStorage |
| `components/ui/badge.test.tsx` | UI primitive |

## Scripts

```bash
./scripts/ci/health-check.test.sh
```

## CI

`.github/workflows/ci.yml`:

1. Backend `./gradlew test`
2. Frontend `npm test` + lint + build
3. Scripts smoke

## Manual / stack smoke (optional)

```bash
curl -s http://localhost:8080/api/server/status | jq .success
curl -s http://localhost:8080/api/docker/containers | jq '.data|length'
```

## E2E (Playwright · golden path)

Compose UI smoke (not full regression).

```bash
# stack up first
./scripts/demo-reset.sh

cd frontend
npm run test:e2e
# first time browsers: cd ../e2e && npx playwright install chromium
```

| File | Coverage |
|------|----------|
| `e2e/dashboard-smoke.spec.ts` | `/` widgets · `/docker` · `/monitoring` |
| `e2e/playwright.config.ts` | `BASE_URL` default `http://127.0.0.1:3000` |

CI: **optional / manual** (avoid flaky PR gates). See `e2e/README.md`.

## Gaps (intentionally later)

- Full multi-page E2E matrix
- Contract tests against live Prometheus/Loki
- JWT role matrix once auth is locked down
