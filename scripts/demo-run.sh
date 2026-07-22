#!/usr/bin/env bash
# CloudLab 5-minute interview demo runner (operator cue card + optional inject).
# Usage:
#   ./scripts/demo-run.sh              # interactive cues
#   ./scripts/demo-run.sh --preflight  # checks only
#   ./scripts/demo-run.sh --inject-docker-restart  # optional failure demo
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

MODE="${1:-interactive}"

green() { printf '\033[32m%s\033[0m\n' "$*"; }
yellow() { printf '\033[33m%s\033[0m\n' "$*"; }
blue() { printf '\033[34m%s\033[0m\n' "$*"; }
bold() { printf '\033[1m%s\033[0m\n' "$*"; }

preflight() {
  bold "== Preflight =="
  local ok=0
  check() {
    local name="$1" url="$2"
    if curl -fsS --max-time 3 "$url" >/dev/null 2>&1; then
      green "  ✓ $name"
    else
      yellow "  ✗ $name ($url)"
      ok=1
    fi
  }
  check "API health" "http://127.0.0.1:8080/actuator/health"
  check "API status" "http://127.0.0.1:8080/api/server/status"
  check "Dashboard" "http://127.0.0.1:3000/"
  check "Prometheus" "http://127.0.0.1:9090/-/ready"
  check "Loki" "http://127.0.0.1:3100/ready"
  check "Alertmanager" "http://127.0.0.1:9093/-/ready"

  if [[ -n "${DISCORD_WEBHOOK_URL:-}" ]] || grep -qE '^DISCORD_WEBHOOK_URL=.+' .env 2>/dev/null; then
    green "  ✓ DISCORD_WEBHOOK_URL present (env or .env)"
  else
    yellow "  ○ DISCORD_WEBHOOK_URL not set — skip live Discord or run demo-discord-test.sh later"
  fi

  if command -v gh >/dev/null 2>&1; then
    if gh run list --workflow=CI --limit 1 --json conclusion -q '.[0].conclusion' 2>/dev/null | grep -q success; then
      green "  ✓ Recent CI run success (gh)"
    else
      yellow "  ○ Could not confirm recent CI success via gh"
    fi
  fi

  echo
  if [[ "$ok" -ne 0 ]]; then
    yellow "Fix failures with: ./scripts/demo-reset.sh"
    return 1
  fi
  green "Preflight OK"
  return 0
}

pause() {
  local sec="${1:-5}"
  yellow "  … ${sec}s (speak / click UI) …"
  sleep "$sec"
}

cue() {
  local t="$1" title="$2" say="$3" show="$4"
  echo
  bold "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
  bold "[${t}] $title"
  blue  "SAY : $say"
  green "SHOW: $show"
  bold "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
}

run_interactive() {
  preflight || true
  echo
  bold "Open these tabs before starting:"
  echo "  1) http://localhost:3000          (Dashboard)"
  echo "  2) http://localhost:3000/monitoring"
  echo "  3) http://localhost:3000/logs"
  echo "  4) https://github.com/jinhgit/cloudlab/actions"
  echo "  5) (optional) Discord channel"
  echo
  read -r -p "Press Enter to start 5-min cues..." _

  cue "0:00" "Dashboard" \
    "브라우저 하나로 서버·컨테이너·관측 상태를 봅니다. 웹앱이 아니라 운영 플랫폼입니다." \
    "CPU/Mem, containers, integrations badges (prometheus/loki/docker)"
  pause 40

  cue "0:45" "CI/CD" \
    "코드 push가 GitHub Actions CI/CD로 이어집니다. PR은 test, main은 이미지 파이프라인입니다." \
    "Actions 초록 런 + README 배지 · Deployments 페이지"
  pause 70

  cue "1:55" "Monitoring" \
    "Prometheus 메트릭을 Grafana만이 아니라 자체 Dashboard 차트로 봅니다." \
    "Monitoring 페이지 CPU/Memory 차트 · JVM heap"
  pause 40

  cue "2:35" "Failure inject" \
    "장애를 인위적으로 내고 자동 복구·로그·알림 루프를 증명합니다." \
    "Docker restart 또는 k8s pod delete (아래 inject 옵션)"
  if [[ "${DEMO_INJECT:-}" == "docker" ]]; then
    yellow "Injecting: docker restart cloudlab-backend (via API if possible)"
    CID=$(curl -fsS http://127.0.0.1:8080/api/docker/containers \
      | python3 -c "import sys,json; d=json.load(sys.stdin); 
print(next((c['id'] for c in d.get('data') or [] if c.get('name')=='cloudlab-backend'),''))" 2>/dev/null || true)
    if [[ -n "${CID}" ]]; then
      curl -fsS -X POST "http://127.0.0.1:8080/api/docker/containers/${CID}/restart" >/dev/null || true
      green "  restart requested for cloudlab-backend"
    else
      docker restart cloudlab-backend 2>/dev/null || yellow "  could not restart backend"
    fi
  fi
  pause 50

  cue "3:25" "Alerts + Discord" \
    "Alertmanager 경로와 Discord 웹훅으로 운영 채널 알림을 닫습니다." \
    "Alerts 페이지 + Discord 메시지 (사전: ./scripts/demo-discord-test.sh)"
  pause 40

  cue "4:05" "Logs" \
    "Loki로 장애·복구 구간 로그를 같은 콘솔에서 확인합니다." \
    "Logs · service=backend · keyword ERROR/restart"
  pause 35

  cue "4:40" "Close" \
    "복구 후 Dashboard 정상 상태로 마무리합니다. v1 운영 플랫폼, v2는 IaC 스케치입니다." \
    "Dashboard integrations 전부 up · container count 정상"
  pause 20

  bold "Demo cues finished. Capture evidence: ./scripts/demo-capture-status.sh"
}

case "$MODE" in
  --preflight|preflight) preflight ;;
  --inject-docker-restart)
    DEMO_INJECT=docker run_interactive
    ;;
  interactive|--interactive|"")
    run_interactive
    ;;
  *)
    echo "Usage: $0 [--preflight|--inject-docker-restart]"
    exit 1
    ;;
esac
