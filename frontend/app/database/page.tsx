"use client";

import { useQuery } from "@tanstack/react-query";
import { AppShell } from "@/components/layout/app-shell";
import { StatCard } from "@/components/ui/stat-card";
import { ErrorBanner } from "@/components/ui/error-banner";
import { Badge } from "@/components/ui/badge";
import { fetchDatabaseStatus } from "@/services/api";
import { useSettingsStore } from "@/store/settings-store";

export default function DatabasePage() {
  const pollingMs = useSettingsStore((s) => s.pollingMs);
  const q = useQuery({
    queryKey: ["database"],
    queryFn: fetchDatabaseStatus,
    refetchInterval: pollingMs,
  });
  const d = q.data?.data;

  return (
    <AppShell title="Database" description="PostgreSQL · 실데이터">
      <div className="space-y-4">
        {q.error instanceof Error ? <ErrorBanner message={q.error.message} /> : null}
        <div className="flex items-center gap-2">
          <Badge variant={d?.available ? "success" : "warning"}>
            {d?.available ? "Connected" : "Unavailable"}
          </Badge>
          <span className="text-sm text-muted-foreground">
            {String(d?.product ?? "")} {String(d?.version ?? "")}
          </span>
        </div>
        <section className="grid gap-3 sm:grid-cols-3">
          <StatCard
            title="DB Size"
            value={
              typeof d?.sizeBytes === "number"
                ? `${(Number(d.sizeBytes) / 1024 / 1024).toFixed(2)} MB`
                : "—"
            }
          />
          <StatCard title="Connections" value={String(d?.connections ?? "—")} />
          <StatCard title="Public tables" value={String(d?.tables ?? "—")} />
        </section>
        {d?.message ? (
          <p className="text-sm text-muted-foreground">{String(d.message)}</p>
        ) : null}
        {d?.url ? (
          <p className="font-mono text-xs text-muted-foreground">{String(d.url)}</p>
        ) : null}
      </div>
    </AppShell>
  );
}
