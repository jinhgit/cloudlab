"use client";

import { useQuery } from "@tanstack/react-query";
import {
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { AppShell } from "@/components/layout/app-shell";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { StatCard } from "@/components/ui/stat-card";
import { ErrorBanner } from "@/components/ui/error-banner";
import { fetchPrometheusRange, fetchPrometheusSummary } from "@/services/api";
import { useSettingsStore } from "@/store/settings-store";

function seriesFromRange(payload: Record<string, unknown> | undefined): { t: string; v: number }[] {
  try {
    const data = payload?.data as { result?: { values?: [number, string][] }[] } | undefined;
    const values = data?.result?.[0]?.values ?? [];
    return values.map(([ts, val]) => ({
      t: new Date(ts * 1000).toLocaleTimeString(),
      v: Number(val),
    }));
  } catch {
    return [];
  }
}

export default function MonitoringPage() {
  const pollingMs = useSettingsStore((s) => s.pollingMs);
  const summaryQ = useQuery({
    queryKey: ["prom", "summary"],
    queryFn: fetchPrometheusSummary,
    refetchInterval: pollingMs,
  });
  const cpuQ = useQuery({
    queryKey: ["prom", "cpu"],
    queryFn: () =>
      fetchPrometheusRange(
        '100 - (avg(rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100)',
        3600
      ),
    refetchInterval: pollingMs * 2,
  });
  const memQ = useQuery({
    queryKey: ["prom", "mem"],
    queryFn: () =>
      fetchPrometheusRange(
        "(1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes)) * 100",
        3600
      ),
    refetchInterval: pollingMs * 2,
  });

  const s = summaryQ.data?.data;
  const cpuSeries = seriesFromRange(cpuQ.data?.data as Record<string, unknown>);
  const memSeries = seriesFromRange(memQ.data?.data as Record<string, unknown>);
  const err = summaryQ.error instanceof Error ? summaryQ.error.message : null;

  return (
    <AppShell title="Monitoring" description="Prometheus · 실데이터 차트">
      <div className="space-y-6">
        {err ? <ErrorBanner message={err} /> : null}
        <section className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
          <StatCard title="CPU %" value={typeof s?.cpu === "number" ? s.cpu.toFixed(1) : "—"} />
          <StatCard title="Memory %" value={typeof s?.memory === "number" ? s.memory.toFixed(1) : "—"} />
          <StatCard
            title="HTTP RPS"
            value={typeof s?.httpRps === "number" ? s.httpRps.toFixed(2) : "—"}
          />
          <StatCard
            title="JVM Heap"
            value={
              typeof s?.jvmHeapUsed === "number"
                ? `${(Number(s.jvmHeapUsed) / 1024 / 1024).toFixed(0)} MB`
                : "—"
            }
          />
        </section>

        <div className="grid gap-4 lg:grid-cols-2">
          <Card>
            <CardHeader>
              <CardTitle>CPU (1h)</CardTitle>
            </CardHeader>
            <CardContent className="h-64">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={cpuSeries}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#27272a" />
                  <XAxis dataKey="t" tick={{ fontSize: 10 }} stroke="#71717a" />
                  <YAxis tick={{ fontSize: 10 }} stroke="#71717a" domain={[0, 100]} />
                  <Tooltip />
                  <Legend />
                  <Line type="monotone" dataKey="v" name="CPU %" stroke="#3b82f6" dot={false} />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <CardTitle>Memory (1h)</CardTitle>
            </CardHeader>
            <CardContent className="h-64">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={memSeries}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#27272a" />
                  <XAxis dataKey="t" tick={{ fontSize: 10 }} stroke="#71717a" />
                  <YAxis tick={{ fontSize: 10 }} stroke="#71717a" domain={[0, 100]} />
                  <Tooltip />
                  <Legend />
                  <Line type="monotone" dataKey="v" name="Mem %" stroke="#22c55e" dot={false} />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </div>
      </div>
    </AppShell>
  );
}
