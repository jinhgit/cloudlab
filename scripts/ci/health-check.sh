#!/usr/bin/env bash
# Poll health URL until UP or timeout.
set -euo pipefail

URL="${1:-http://localhost:8080/actuator/health}"
RETRIES="${HEALTH_RETRIES:-30}"
SLEEP_SEC="${HEALTH_SLEEP:-5}"

echo "==> Health check: ${URL} (retries=${RETRIES})"

for i in $(seq 1 "${RETRIES}"); do
  if BODY=$(curl -fsS --max-time 5 "${URL}" 2>/dev/null); then
    if echo "${BODY}" | grep -q '"status"[[:space:]]*:[[:space:]]*"UP"'; then
      echo "Healthy on attempt ${i}: ${BODY}"
      exit 0
    fi
    echo "Attempt ${i}: unexpected body: ${BODY}"
  else
    echo "Attempt ${i}: not ready"
  fi
  sleep "${SLEEP_SEC}"
done

echo "Health check failed after ${RETRIES} attempts: ${URL}" >&2
exit 1
