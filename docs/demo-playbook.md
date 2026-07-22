# 면접 데모 플레이북 (5분) — 끊김 없이

목표: **운영 플랫폼**임을 한 흐름으로 증명한다.

| 자료 | 링크 |
|------|------|
| **UI 동선 + 상세 대본** | [demo-rehearsal-ui.md](./demo-rehearsal-ui.md) |
| **이력서 한 줄 / 불릿** | [resume-one-liners.md](./resume-one-liners.md) |
| 터미널 큐 카드 | `./scripts/demo-run.sh` · `--print` 로 전체 출력 |

---

## 0. 면접 전날 / 당일 30분 전

```bash
cd /path/to/cloudlab
./scripts/demo-reset.sh          # 스택 정상화
./scripts/demo-capture-status.sh # 증거 스냅샷
# (선택) Discord
export DISCORD_WEBHOOK_URL='...' # or .env
./scripts/demo-discord-test.sh
# 스크린샷 6장 → docs/assets/demo/*.png
```

### 탭 미리 열기

1. http://localhost:3000  
2. http://localhost:3000/monitoring  
3. http://localhost:3000/docker  
4. http://localhost:3000/logs  
5. https://github.com/jinhgit/cloudlab/actions  
6. Discord (알림 채널)

### 백업 플랜

| 리스크 | 대비 |
|--------|------|
| Push가 느림 | Actions 초록 런 **미리** 열어두고 배지/스크린샷으로 설명 |
| k8s 없음 | Docker restart inject (`demo-run.sh --inject-docker-restart`) |
| Discord 실패 | `discord-alert.png` 사전 캡처 + receipt md |

---

## 1. 5분 스크립트 (말 + 화면)

| 시간 | 화면 | 말할 내용 (요약) |
|------|------|------------------|
| 0:00 | Dashboard | 브라우저 하나 = 운영 콘솔. CPU/Mem, 컨테이너, prometheus/loki/docker 배지 |
| 0:45 | Actions | Push → CI test/build → CD 이미지. README 초록 배지가 증거 |
| 1:55 | Monitoring | Prometheus를 자체 UI 차트로. Grafana는 보조 |
| 2:35 | Docker/K8s | 장애 주입: restart 또는 pod delete → 복구 |
| 3:25 | Alerts+Discord | Alertmanager + 실제 Discord 메시지 |
| 4:05 | Logs | Loki로 같은 구간 로그 |
| 4:40 | Dashboard | 정상 복귀로 클로징. v2는 IaC 스케치 |

타이밍 자동화 큐: `./scripts/demo-run.sh`

---

## 2. 증거 파일 맵

| 파일 | 의미 |
|------|------|
| [docs/assets/demo/latest-status.md](../docs/assets/demo/latest-status.md) | API 실측 스냅샷 |
| `docs/assets/demo/*.png` | UI/Actions/Discord 캡처 |
| CI badge | README 상단 shields |

---

## 3. 원커맨드 치트시트

```bash
# 리셋
./scripts/demo-reset.sh

# 큐 카드
./scripts/demo-run.sh

# Discord 1회
./scripts/demo-discord-test.sh

# 상태 증거 갱신
./scripts/demo-capture-status.sh

# Docker 장애 주입 포함 큐
./scripts/demo-run.sh --inject-docker-restart
```

---

## 4. 클로징 한 문장

> “CRUD 앱이 아니라, 관측·배포·장애 복구가 한 콘솔에서 닫히는 **내부 운영 플랫폼**이고,  
> 인프라 자동화는 v2 IaC 스케치로 이어집니다.”
