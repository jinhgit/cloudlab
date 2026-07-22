# logging/

Loki + Promtail configuration for CloudLab container logs.

**Docs:** [docs/logging.md](../docs/logging.md)

```bash
../scripts/logging-up.sh
curl -s http://localhost:3100/ready
curl -sG http://localhost:3100/loki/api/v1/labels
```
