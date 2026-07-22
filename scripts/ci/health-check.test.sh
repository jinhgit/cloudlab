#!/usr/bin/env bash
# Lightweight smoke test for health-check.sh using a local python HTTP server.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
PORT=18765
BODY='{"status":"UP"}'

python3 - <<PY &
from http.server import BaseHTTPRequestHandler, HTTPServer
import time

class H(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-Type','application/json')
        self.end_headers()
        self.wfile.write(b'${BODY}')
    def log_message(self, *args):
        pass

httpd = HTTPServer(('127.0.0.1', ${PORT}), H)
# Serve a few requests then exit
for _ in range(10):
    httpd.handle_request()
PY
PID=$!

# Wait until port accepts connections
for i in $(seq 1 30); do
  if curl -fsS --max-time 1 "http://127.0.0.1:${PORT}/" >/dev/null 2>&1; then
    break
  fi
  sleep 0.1
done

export HEALTH_RETRIES=10
export HEALTH_SLEEP=1
"$ROOT/scripts/ci/health-check.sh" "http://127.0.0.1:${PORT}/actuator/health"
kill "$PID" 2>/dev/null || true
wait "$PID" 2>/dev/null || true
echo "health-check.test.sh OK"
