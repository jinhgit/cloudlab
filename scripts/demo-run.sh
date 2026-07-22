#!/usr/bin/env bash
# CloudLab 5-minute interview demo — detailed UI cues + speaking notes.
# Usage:
#   ./scripts/demo-run.sh
#   ./scripts/demo-run.sh --preflight
#   ./scripts/demo-run.sh --inject-docker-restart
#   ./scripts/demo-run.sh --print   # print full script without pauses
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

MODE="${1:-interactive}"

green() { printf '\033[32m%s\033[0m\n' "$*"; }
yellow() { printf '\033[33m%s\033[0m\n' "$*"; }
blue() { printf '\033[34m%s\033[0m\n' "$*"; }
cyan() { printf '\033[36m%s\033[0m\n' "$*"; }
bold() { printf '\033[1m%s\033[0m\n' "$*"; }
dim() { printf '\033[2m%s\033[0m\n' "$*"; }

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
    green "  ✓ DISCORD_WEBHOOK_URL present"
  else
    yellow "  ○ Discord webhook not set (use screenshot backup)"
  fi

  echo
  if [[ "$ok" -ne 0 ]]; then
    yellow "Fix: ./scripts/demo-reset.sh"
    return 1
  fi
  green "Preflight OK"
}

pause() {
  local sec="${1:-5}"
  if [[ "${PRINT_ONLY:-}" == "1" ]]; then
    dim "  [pause ${sec}s]"
    return
  fi
  yellow "  … ${sec}s — speak / click …"
  sleep "$sec"
}

segment() {
  local t="$1" title="$2" url="$3" ui="$4" say="$5" why="$6"
  echo
  bold "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
  bold "[${t}] ${title}"
  cyan  "OPEN : ${url}"
  green "UI   : ${ui}"
  blue  "SAY  : ${say}"
  dim   "WHY  : ${why}"
  bold "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
}

run_script() {
  preflight || true
  echo
  bold "Tabs to open first:"
  echo "  • http://localhost:3000"
  echo "  • http://localhost:3000/monitoring"
  echo "  • http://localhost:3000/docker"
  echo "  • http://localhost:3000/logs"
  echo "  • http://localhost:3000/alerts"
  echo "  • https://github.com/jinhgit/cloudlab/actions"
  echo "  • Discord (optional)"
  echo
  bold "Full UI script: docs/demo-rehearsal-ui.md"
  bold "Resume lines : docs/resume-one-liners.md"
  echo
  if [[ "${PRINT_ONLY:-}" != "1" ]]; then
    read -r -p "Press Enter to start 5-min cues..." _
  fi

  segment "0:00" "Dashboard 홈" \
    "http://localhost:3000/" \
    "헤더 API 배지 → CPU/Mem/Containers 카드 → Integrations 배지 줄" \
    "브라우저 하나로 서버·컨테이너·관측 상태를 봅니다. CRUD 앱이 아니라 Self-Hosted 운영 플랫폼입니다. 숫자는 Platform API가 Prometheus·Docker에서 가져와 브라우저가 인프라에 직접 붙지 않습니다." \
    "제어면 경계 + 실데이터 요약"
  pause 45

  segment "0:45" "CI/CD" \
    "https://github.com/jinhgit/cloudlab/actions (+ 선택 /deployments)" \
    "초록 CI/CD 런 · README 상단 badge · (선택) Deployments 테이블" \
    "코드 push가 GitHub Actions로 이어집니다. PR은 test/build, main은 이미지 파이프라인입니다. 배지가 최근 성공 증거입니다." \
    "전달 파이프라인 신뢰"
  pause 70

  segment "1:55" "Monitoring" \
    "http://localhost:3000/monitoring" \
    "상단 CPU/Mem/RPS/Heap → 하단 CPU(1h)·Memory(1h) 차트" \
    "Prometheus를 Grafana 임베드만이 아니라 자체 Dashboard 차트로 봅니다. Node Exporter·cAdvisor·Actuator를 스크레이프합니다." \
    "Observability 1차 UI"
  pause 40

  segment "2:35" "장애 주입 (Docker)" \
    "http://localhost:3000/docker" \
    "cloudlab-backend 행 → Restart → status 변화 관찰" \
    "강제로 장애를 냅니다. Restart 후 잠시 API가 흔들려도 헬스 경로로 복구되는 걸 봅니다. k3s 환경이면 Pod 삭제로 셀프힐을 같은 스토리로 말할 수 있습니다." \
    "운영 루프 증명"
  if [[ "${DEMO_INJECT:-}" == "docker" && "${PRINT_ONLY:-}" != "1" ]]; then
    yellow "Inject: restart cloudlab-backend"
    CID=$(curl -fsS http://127.0.0.1:8080/api/docker/containers 2>/dev/null \
      | python3 -c "import sys,json; d=json.load(sys.stdin); print(next((c['id'] for c in (d.get('data') or []) if c.get('name')=='cloudlab-backend'),''))" 2>/dev/null || true)
    if [[ -n "${CID}" ]]; then
      curl -fsS -X POST "http://127.0.0.1:8080/api/docker/containers/${CID}/restart" >/dev/null 2>&1 || true
      green "  API restart requested"
    else
      docker restart cloudlab-backend 2>/dev/null || yellow "  restart skipped"
    fi
  fi
  pause 50

  segment "3:25" "Alerts + Discord" \
    "http://localhost:3000/alerts (+ Discord)" \
    "Alerts 테이블 Severity/Summary · Discord 임베드 메시지" \
    "Alertmanager API를 대시보드에 붙였고, Discord 웹훅으로 운영 채널 알림을 검증합니다. 관측에서 사람까지 루프를 닫습니다." \
    "알림 경로"
  pause 40

  segment "4:05" "Logs" \
    "http://localhost:3000/logs" \
    "Service=backend · Refresh · 로그 스트림 스크롤" \
    "Loki+Promtail로 Compose 로그를 모읍니다. 방금 재시작 구간을 같은 콘솔에서 확인합니다." \
    "로그 상관"
  pause 35

  segment "4:40" "클로징" \
    "http://localhost:3000/" \
    "Integrations up · Containers 정상" \
    "정리하면 제어면 API, 운영 UI, 관측, CI/CD, 데모 자동화가 한 제품입니다. v2는 Terraform·Ansible IaC 스케치와 무료 검증 경로입니다. 질문 받겠습니다." \
    "메시지 고정"
  pause 20

  bold "Cues done."
  green "Resume lines: docs/resume-one-liners.md"
  green "Full UI script: docs/demo-rehearsal-ui.md"
  dim  "Evidence: ./scripts/demo-capture-status.sh"
}

case "$MODE" in
  --preflight|preflight) preflight ;;
  --print|print)
    PRINT_ONLY=1 run_script
    ;;
  --inject-docker-restart)
    DEMO_INJECT=docker run_script
    ;;
  interactive|--interactive|"")
    run_script
    ;;
  *)
    echo "Usage: $0 [--preflight|--print|--inject-docker-restart]"
    exit 1
    ;;
esac
