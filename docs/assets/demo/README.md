# demo/ — Interview evidence assets

## Auto-generated (commit OK)

| File | Source |
|------|--------|
| `latest-status.md` | `./scripts/demo-capture-status.sh` |
| `server-status.json` | same |
| `discord-test-receipt.md` | `./scripts/demo-discord-test.sh` |

## You capture once (PNG)

| File | How |
|------|-----|
| `dashboard.png` | http://localhost:3000 |
| `monitoring.png` | /monitoring |
| `docker.png` | /docker |
| `logs.png` | /logs |
| `actions-ci.png` | GitHub → Actions → green CI |
| `discord-alert.png` | Discord channel after webhook test |

```bash
./scripts/demo-reset.sh
./scripts/demo-capture-status.sh
./scripts/demo-discord-test.sh   # needs DISCORD_WEBHOOK_URL
# take screenshots into this folder
```

Keep images reasonably small (&lt; 1MB each). Redact personal Discord server names if needed.
