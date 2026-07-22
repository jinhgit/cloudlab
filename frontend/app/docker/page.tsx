"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { AppShell } from "@/components/layout/app-shell";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { DataTable } from "@/components/ui/data-table";
import { ErrorBanner } from "@/components/ui/error-banner";
import { dockerAction, fetchDockerContainers } from "@/services/api";
import { useSettingsStore } from "@/store/settings-store";

export default function DockerPage() {
  const pollingMs = useSettingsStore((s) => s.pollingMs);
  const qc = useQueryClient();
  const q = useQuery({
    queryKey: ["docker", "containers"],
    queryFn: () => fetchDockerContainers(true),
    refetchInterval: pollingMs,
  });

  const action = useMutation({
    mutationFn: ({ id, op }: { id: string; op: "start" | "stop" | "restart" }) =>
      dockerAction(id, op),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["docker"] }),
  });

  const rows = (q.data?.data ?? []).map((c) => {
    const id = String(c.id ?? "");
    const short = id.slice(0, 12);
    const state = String(c.state ?? "");
    return {
      name: String(c.name ?? short),
      state: (
        <Badge
          variant={
            state === "running" ? "success" : state === "exited" ? "secondary" : "warning"
          }
        >
          {state}
        </Badge>
      ),
      image: <span className="font-mono text-xs">{String(c.image ?? "")}</span>,
      status: String(c.status ?? ""),
      actions: (
        <div className="flex flex-wrap gap-1">
          <Button
            size="sm"
            variant="outline"
            disabled={action.isPending}
            onClick={() => action.mutate({ id, op: "start" })}
          >
            Start
          </Button>
          <Button
            size="sm"
            variant="outline"
            disabled={action.isPending}
            onClick={() => action.mutate({ id, op: "stop" })}
          >
            Stop
          </Button>
          <Button
            size="sm"
            variant="secondary"
            disabled={action.isPending}
            onClick={() => action.mutate({ id, op: "restart" })}
          >
            Restart
          </Button>
        </div>
      ),
    };
  });

  return (
    <AppShell title="Docker" description="Docker Engine · 실데이터">
      <div className="space-y-4">
        {q.error instanceof Error ? <ErrorBanner message={q.error.message} /> : null}
        {action.error instanceof Error ? (
          <ErrorBanner message={action.error.message} />
        ) : null}
        <DataTable
          columns={[
            { key: "name", header: "Name" },
            { key: "state", header: "State" },
            { key: "image", header: "Image" },
            { key: "status", header: "Status" },
            { key: "actions", header: "Actions" },
          ]}
          rows={rows}
          empty={q.isLoading ? "Loading…" : "No containers (is docker.sock mounted?)"}
        />
      </div>
    </AppShell>
  );
}
