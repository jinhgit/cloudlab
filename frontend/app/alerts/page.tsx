"use client";

import { useQuery } from "@tanstack/react-query";
import { AppShell } from "@/components/layout/app-shell";
import { Badge } from "@/components/ui/badge";
import { DataTable } from "@/components/ui/data-table";
import { ErrorBanner } from "@/components/ui/error-banner";
import { fetchAlerts } from "@/services/api";
import { useSettingsStore } from "@/store/settings-store";

export default function AlertsPage() {
  const pollingMs = useSettingsStore((s) => s.pollingMs);
  const q = useQuery({
    queryKey: ["alerts"],
    queryFn: fetchAlerts,
    refetchInterval: pollingMs,
  });

  const rows = (q.data?.data ?? []).map((a) => {
    const labels = (a.labels as Record<string, string>) ?? {};
    const status = (a.status as Record<string, string>) ?? {};
    const annotations = (a.annotations as Record<string, string>) ?? {};
    const severity = labels.severity ?? "info";
    return {
      alertname: labels.alertname ?? "—",
      severity: (
        <Badge
          variant={
            severity === "critical"
              ? "destructive"
              : severity === "warning"
                ? "warning"
                : "secondary"
          }
        >
          {severity}
        </Badge>
      ),
      state: status.state ?? "—",
      summary: annotations.summary ?? annotations.description ?? "—",
      startsAt: a.startsAt ? String(a.startsAt) : "—",
    };
  });

  return (
    <AppShell title="Alerts" description="Alertmanager · 실데이터">
      <div className="space-y-4">
        {q.error instanceof Error ? <ErrorBanner message={q.error.message} /> : null}
        <DataTable
          columns={[
            { key: "alertname", header: "Alert" },
            { key: "severity", header: "Severity" },
            { key: "state", header: "State" },
            { key: "summary", header: "Summary" },
            { key: "startsAt", header: "Starts" },
          ]}
          rows={rows}
          empty={q.isLoading ? "Loading…" : "No alerts (good!)"}
        />
      </div>
    </AppShell>
  );
}
