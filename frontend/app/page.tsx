"use client";

import { useQuery } from "@tanstack/react-query";
import { AppShell } from "@/components/layout/app-shell";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { StatCard } from "@/components/ui/stat-card";
import { ErrorBanner } from "@/components/ui/error-banner";
import { fetchServerStatus, fetchPlatformInfo } from "@/services/api";
import { useSettingsStore } from "@/store/settings-store";

function fmt(n: unknown, suffix = "") {
  if (typeof n !== "number" || Number.isNaN(n)) return "—";
  return `${n.toFixed?.(1) ?? n}${suffix}`;
}

export default function DashboardPage() {
  const pollingMs = useSettingsStore((s) => s.pollingMs);
  const statusQ = useQuery({
    queryKey: ["server", "status"],
    queryFn: fetchServerStatus,
    refetchInterval: pollingMs,
  });
  const infoQ = useQuery({
    queryKey: ["platform", "info"],
    queryFn: fetchPlatformInfo,
    staleTime: 60_000,
  });

  const d = statusQ.data?.data;
  const err = statusQ.error instanceof Error ? statusQ.error.message : null;

  return (
    <AppShell title="Dashboard" description="서버 · 리소스 · 통합 상태 요약 (실데이터)">
      <div className="space-y-6">
        {err ? <ErrorBanner message={err} /> : null}

        <section className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
          <StatCard
            title="CPU"
            description="Node Exporter"
            value={fmt(d?.cpuPercent, "%")}
            hint="Prometheus · 5m avg"
          />
          <StatCard
            title="Memory"
            description="Node Exporter"
            value={fmt(d?.memoryPercent, "%")}
          />
          <StatCard
            title="Disk"
            description="mount /"
            value={fmt(d?.diskPercent, "%")}
          />
          <StatCard
            title="JVM Heap"
            description="cloudlab-backend"
            value={
              typeof d?.jvmHeapUsed === "number"
                ? `${(Number(d.jvmHeapUsed) / 1024 / 1024).toFixed(0)} MB`
                : "—"
            }
          />
        </section>

        <section className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
          <StatCard
            title="Containers"
            value={`${String(d?.containersRunning ?? "—")} / ${String(d?.containersTotal ?? "—")}`}
            hint="running / total (Docker)"
          />
          <StatCard title="Pods" value={String(d?.podsTotal ?? "—")} hint="Kubernetes (if connected)" />
          <StatCard title="Alerts firing" value={String(d?.alertsFiring ?? "—")} hint="Alertmanager" />
          <Card>
            <CardHeader>
              <CardTitle>Integrations</CardTitle>
              <CardDescription>Upstream reachability</CardDescription>
            </CardHeader>
            <CardContent className="flex flex-wrap gap-2">
              {(
                [
                  ["prometheus", d?.prometheus],
                  ["loki", d?.loki],
                  ["alertmanager", d?.alertmanager],
                  ["docker", d?.docker],
                  ["kubernetes", d?.kubernetes],
                ] as const
              ).map(([name, ok]) => (
                <Badge key={name} variant={ok ? "success" : "warning"}>
                  {name}: {ok ? "up" : "down"}
                </Badge>
              ))}
            </CardContent>
          </Card>
        </section>

        <Card>
          <CardHeader>
            <CardTitle>Platform API</CardTitle>
            <CardDescription>
              {infoQ.data?.data?.name ?? "CloudLab"} · v{infoQ.data?.data?.version ?? "—"}
            </CardDescription>
          </CardHeader>
          <CardContent className="text-sm text-muted-foreground">
            Polling every {pollingMs}ms · Step 10 real adapters (Docker / Prometheus / Loki / …)
          </CardContent>
        </Card>
      </div>
    </AppShell>
  );
}
