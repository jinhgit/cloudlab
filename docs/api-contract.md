# API Contract (v1 sketch)

Authoritative list evolves with implementation; PRD §16 is the source of required routes.

## Envelope

```json
{
  "success": true,
  "data": {},
  "error": null
}
```

Error:

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "UPSTREAM_UNAVAILABLE",
    "message": "Prometheus is not reachable"
  }
}
```

## Auth

| Method | Path | Role |
|--------|------|------|
| POST | `/api/auth/login` | public |
| POST | `/api/auth/refresh` | authenticated |
| GET | `/api/auth/me` | authenticated |

## Roles

| Role | Read | Mutate (restart/delete/deploy) |
|------|------|--------------------------------|
| VIEWER | yes | no |
| ADMIN | yes | yes |

## Versioning

v1 uses unversioned `/api/*`. Breaking changes in a major portfolio revision may introduce `/api/v2`.
