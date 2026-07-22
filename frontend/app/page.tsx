"use client";

import { AppShell } from "@/components/layout/app-shell";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { usePlatformHealth, usePlatformInfo } from "@/hooks/use-platform-health";
import { useSettingsStore } from "@/store/settings-store";

const WIDGETS = [
  { key: "cpu", label: "CPU", hint: "Prometheus · Step 7+" },
  { key: "memory", label: "Memory", hint: "Prometheus · Step 7+" },
  { key: "disk", label: "Disk", hint: "Node Exporter · Step 7+" },
  { key: "network", label: "Network", hint: "RX / TX · Step 7+" },
  { key: "containers", label: "Containers", hint: "Docker API · Step 10" },
  { key: "pods", label: "Pods", hint: "k3s API · Step 10" },
] as const;

export default function DashboardPage() {
  const pollingMs = useSettingsStore((s) => s.pollingMs);
  const { data: health, isError, isLoading } = usePlatformHealth();
  const { data: info } = usePlatformInfo();

  const apiStatus = isLoading ? "checking" : isError ? "offline" : health?.data?.status ?? "unknown";

  return (
    <AppShell
      title="Dashboard"
      description="서버 · 리소스 · 최근 배포/알림/로그 요약"
    >
      <div className="space-y-6">
        <section className="grid gap-3 sm:grid-cols-2 xl:grid-cols-3">
          <Card className="sm:col-span-2 xl:col-span-1">
            <CardHeader>
              <CardTitle>Platform API</CardTitle>
              <CardDescription>Spring Boot control plane</CardDescription>
            </CardHeader>
            <CardContent className="space-y-2">
              <div className="flex items-center gap-2">
                <span className="text-sm text-muted-foreground">Status</span>
                {apiStatus === "UP" ? (
                  <Badge variant="success">UP</Badge>
                ) : apiStatus === "checking" ? (
                  <Badge variant="secondary">Checking</Badge>
                ) : (
                  <Badge variant="warning">Offline</Badge>
                )}
              </div>
              <p className="font-mono text-xs text-muted-foreground">
                {info?.data?.name ?? "CloudLab Platform API"}
                {info?.data?.version ? ` · v${info.data.version}` : ""}
              </p>
              <p className="text-xs text-muted-foreground">
                Polling every {pollingMs}ms (Settings). WebSocket fallback later.
              </p>
            </CardContent>
          </Card>

          {WIDGETS.map((w) => (
            <Card key={w.key}>
              <CardHeader>
                <CardTitle>{w.label}</CardTitle>
                <CardDescription>{w.hint}</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-semibold tabular-nums text-muted-foreground">
                  —
                </div>
                <p className="mt-1 text-xs text-muted-foreground">
                  실데이터 연동 전 placeholder
                </p>
              </CardContent>
            </Card>
          ))}
        </section>

        <section className="grid gap-3 lg:grid-cols-3">
          <Card>
            <CardHeader>
              <CardTitle>Recent Deploys</CardTitle>
              <CardDescription>GitHub Actions · Step 9–10</CardDescription>
            </CardHeader>
            <CardContent className="text-sm text-muted-foreground">
              아직 배포 이력이 없습니다.
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <CardTitle>Recent Alerts</CardTitle>
              <CardDescription>Alertmanager · Step 7–10</CardDescription>
            </CardHeader>
            <CardContent className="text-sm text-muted-foreground">
              활성 알림이 없습니다.
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <CardTitle>Recent Logs</CardTitle>
              <CardDescription>Loki · Step 8–10</CardDescription>
            </CardHeader>
            <CardContent className="text-sm text-muted-foreground">
              로그 스트림 대기 중.
            </CardContent>
          </Card>
        </section>
      </div>
    </AppShell>
  );
}
