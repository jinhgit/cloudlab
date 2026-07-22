"use client";

import { useQuery } from "@tanstack/react-query";
import { AppShell } from "@/components/layout/app-shell";
import { Badge } from "@/components/ui/badge";
import { DataTable } from "@/components/ui/data-table";
import { ErrorBanner } from "@/components/ui/error-banner";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { fetchDeployments } from "@/services/api";
import { useSettingsStore } from "@/store/settings-store";

export default function DeploymentsPage() {
  const pollingMs = useSettingsStore((s) => s.pollingMs);
  const q = useQuery({
    queryKey: ["deployments"],
    queryFn: () => fetchDeployments(20),
    refetchInterval: pollingMs * 2,
  });

  const configured = q.data?.data?.configured;
  const runs = q.data?.data?.runs ?? [];

  const rows = runs.map((r) => ({
    name: String(r.name ?? r.display_title ?? "—"),
    status: (
      <Badge
        variant={
          r.conclusion === "success"
            ? "success"
            : r.conclusion === "failure"
              ? "destructive"
              : "warning"
        }
      >
        {String(r.status ?? "—")}
        {r.conclusion ? ` / ${r.conclusion}` : ""}
      </Badge>
    ),
    branch: String(r.head_branch ?? "—"),
    actor: String((r.actor as { login?: string })?.login ?? "—"),
    event: String(r.event ?? "—"),
    created: String(r.created_at ?? "—"),
    url: r.html_url ? (
      <a
        className="text-primary underline-offset-2 hover:underline"
        href={String(r.html_url)}
        target="_blank"
        rel="noreferrer"
      >
        Open
      </a>
    ) : (
      "—"
    ),
  }));

  return (
    <AppShell title="Deployments" description="GitHub Actions · 실데이터">
      <div className="space-y-4">
        {q.error instanceof Error ? <ErrorBanner message={q.error.message} /> : null}
        {!configured && !q.isLoading ? (
          <Card>
            <CardHeader>
              <CardTitle>GitHub not configured</CardTitle>
              <CardDescription>
                Set <code>GITHUB_TOKEN</code>, <code>GITHUB_OWNER</code>,{" "}
                <code>GITHUB_REPO</code> on the Platform API to list workflow runs.
              </CardDescription>
            </CardHeader>
            <CardContent className="text-sm text-muted-foreground">
              CD 파이프라인 자체는 GitHub Actions에서 동작합니다. 이 화면은 API 연동 뷰입니다.
            </CardContent>
          </Card>
        ) : null}
        <DataTable
          columns={[
            { key: "name", header: "Workflow" },
            { key: "status", header: "Status" },
            { key: "branch", header: "Branch" },
            { key: "actor", header: "Actor" },
            { key: "event", header: "Event" },
            { key: "created", header: "Created" },
            { key: "url", header: "Link" },
          ]}
          rows={rows}
          empty={q.isLoading ? "Loading…" : "No workflow runs"}
        />
      </div>
    </AppShell>
  );
}
