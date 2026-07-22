"use client";

import { useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { AppShell } from "@/components/layout/app-shell";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { ErrorBanner } from "@/components/ui/error-banner";
import { fetchLogs } from "@/services/api";
import { useSettingsStore } from "@/store/settings-store";

type LogLine = { ts: string; line: string };

function parseLoki(data: Record<string, unknown> | undefined): LogLine[] {
  try {
    const result = (data?.data as { result?: { values?: [string, string][] }[] })?.result ?? [];
    const lines: LogLine[] = [];
    for (const stream of result) {
      for (const [ts, line] of stream.values ?? []) {
        const ms = Number(ts) / 1e6;
        lines.push({
          ts: Number.isFinite(ms) ? new Date(ms).toLocaleString() : ts,
          line,
        });
      }
    }
    return lines.reverse();
  } catch {
    return [];
  }
}

export default function LogsPage() {
  const pollingMs = useSettingsStore((s) => s.pollingMs);
  const [service, setService] = useState("backend");
  const [keyword, setKeyword] = useState("");
  const query = useMemo(() => {
    const base =
      service === "all"
        ? '{compose_project="cloudlab"}'
        : `{compose_service="${service}"}`;
    return keyword ? `${base} |= \`${keyword}\`` : base;
  }, [service, keyword]);

  const q = useQuery({
    queryKey: ["logs", query],
    queryFn: () => fetchLogs({ query, limit: 150, rangeSeconds: 3600 }),
    refetchInterval: pollingMs,
  });

  const lines = parseLoki(q.data?.data as Record<string, unknown>);

  return (
    <AppShell title="Logs" description="Loki · 실데이터 검색">
      <div className="space-y-4">
        {q.error instanceof Error ? <ErrorBanner message={q.error.message} /> : null}
        <div className="flex flex-wrap items-end gap-3">
          <label className="space-y-1 text-xs">
            <span className="text-muted-foreground">Service</span>
            <select
              className="block h-9 rounded-md border border-input bg-secondary px-2 text-sm"
              value={service}
              onChange={(e) => setService(e.target.value)}
            >
              <option value="all">all (cloudlab)</option>
              <option value="backend">backend</option>
              <option value="frontend">frontend</option>
              <option value="postgres">postgres</option>
              <option value="prometheus">prometheus</option>
              <option value="loki">loki</option>
            </select>
          </label>
          <label className="space-y-1 text-xs">
            <span className="text-muted-foreground">Keyword</span>
            <input
              className="block h-9 w-56 rounded-md border border-input bg-secondary px-2 text-sm"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              placeholder="ERROR"
            />
          </label>
          <Button variant="secondary" onClick={() => q.refetch()}>
            Refresh
          </Button>
          <code className="text-[11px] text-muted-foreground">{query}</code>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Log stream ({lines.length})</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="max-h-[60vh] overflow-auto rounded-md bg-black/40 p-3 font-mono text-xs leading-relaxed">
              {q.isLoading ? (
                <p className="text-muted-foreground">Loading…</p>
              ) : lines.length === 0 ? (
                <p className="text-muted-foreground">No lines in range</p>
              ) : (
                lines.map((l, i) => (
                  <div key={i} className="border-b border-white/5 py-1">
                    <span className="mr-2 text-muted-foreground">{l.ts}</span>
                    <span className="whitespace-pre-wrap break-all text-zinc-200">{l.line}</span>
                  </div>
                ))
              )}
            </div>
          </CardContent>
        </Card>
      </div>
    </AppShell>
  );
}
