"use client";

import { useQuery } from "@tanstack/react-query";
import { AppShell } from "@/components/layout/app-shell";
import { StatCard } from "@/components/ui/stat-card";
import { ErrorBanner } from "@/components/ui/error-banner";
import { Badge } from "@/components/ui/badge";
import { fetchRedisStatus } from "@/services/api";
import { useSettingsStore } from "@/store/settings-store";

export default function RedisPage() {
  const pollingMs = useSettingsStore((s) => s.pollingMs);
  const q = useQuery({
    queryKey: ["redis"],
    queryFn: fetchRedisStatus,
    refetchInterval: pollingMs,
  });
  const d = q.data?.data;
  const hits = Number(d?.keyspaceHits ?? 0);
  const misses = Number(d?.keyspaceMisses ?? 0);
  const ratio = hits + misses > 0 ? ((hits / (hits + misses)) * 100).toFixed(1) : "—";

  return (
    <AppShell title="Redis" description="Redis INFO · 실데이터">
      <div className="space-y-4">
        {q.error instanceof Error ? <ErrorBanner message={q.error.message} /> : null}
        <Badge variant={d?.available ? "success" : "warning"}>
          {d?.available ? `PONG · v${d.redisVersion ?? "?"}` : "Unavailable"}
        </Badge>
        <section className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
          <StatCard title="Memory" value={String(d?.usedMemory ?? "—")} />
          <StatCard title="Keys" value={String(d?.keyCount ?? "—")} />
          <StatCard title="Hit ratio" value={ratio === "—" ? "—" : `${ratio}%`} />
          <StatCard title="Evicted" value={String(d?.evictedKeys ?? "—")} />
        </section>
        {d?.message ? (
          <p className="text-sm text-muted-foreground">{String(d.message)}</p>
        ) : null}
      </div>
    </AppShell>
  );
}
