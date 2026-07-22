#!/usr/bin/env bash
# CloudLab backend container entrypoint.
# Allows JAVA_OPTS / SPRING_* env injection without rebuilding the image.
set -euo pipefail

# Optional: wait for dependencies when used under Compose (Step 5+)
# CLOUDLAB_WAIT_FOR="postgres:5432 redis:6379"
if [[ -n "${CLOUDLAB_WAIT_FOR:-}" ]]; then
  echo "[entrypoint] waiting for: ${CLOUDLAB_WAIT_FOR}"
  for target in ${CLOUDLAB_WAIT_FOR}; do
    host="${target%%:*}"
    port="${target##*:}"
    for i in $(seq 1 60); do
      if (echo >/dev/tcp/"${host}"/"${port}") >/dev/null 2>&1; then
        echo "[entrypoint] ${target} is up"
        break
      fi
      if [[ "$i" -eq 60 ]]; then
        echo "[entrypoint] timeout waiting for ${target}" >&2
        exit 1
      fi
      sleep 2
    done
  done
fi

if [[ $# -eq 0 ]]; then
  set -- java -jar /app/app.jar
fi

# If first arg is java, inject JAVA_OPTS
if [[ "$1" == "java" ]]; then
  shift
  # shellcheck disable=SC2086
  exec java ${JAVA_OPTS:-} "$@"
fi

exec "$@"
