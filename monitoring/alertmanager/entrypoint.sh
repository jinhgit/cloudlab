#!/bin/sh
set -eu
CONFIG_DIR="/etc/alertmanager"
OUT="${CONFIG_DIR}/alertmanager.generated.yml"

if [ -n "${DISCORD_WEBHOOK_URL:-}" ]; then
  echo "Configuring Discord webhook receiver..."
  sed "s|\${DISCORD_WEBHOOK_URL}|${DISCORD_WEBHOOK_URL}|g" \
    "${CONFIG_DIR}/alertmanager.template.yml" > "${OUT}"
else
  echo "DISCORD_WEBHOOK_URL empty — using local placeholder receiver"
  cp "${CONFIG_DIR}/alertmanager.yml" "${OUT}"
fi

exec /bin/alertmanager \
  --config.file="${OUT}" \
  --storage.path=/alertmanager \
  --web.listen-address=:9093
