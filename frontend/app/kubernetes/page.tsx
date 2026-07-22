"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { AppShell } from "@/components/layout/app-shell";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { DataTable } from "@/components/ui/data-table";
import { ErrorBanner } from "@/components/ui/error-banner";
import {
  deleteKubernetesPod,
  fetchKubernetesDeployments,
  fetchKubernetesPods,
  restartKubernetesDeployment,
} from "@/services/api";
import { useSettingsStore } from "@/store/settings-store";

export default function KubernetesPage() {
  const pollingMs = useSettingsStore((s) => s.pollingMs);
  const qc = useQueryClient();
  const podsQ = useQuery({
    queryKey: ["k8s", "pods"],
    queryFn: () => fetchKubernetesPods("all"),
    refetchInterval: pollingMs,
    retry: 1,
  });
  const depQ = useQuery({
    queryKey: ["k8s", "deployments"],
    queryFn: () => fetchKubernetesDeployments("all"),
    refetchInterval: pollingMs,
    retry: 1,
  });

  const delPod = useMutation({
    mutationFn: ({ ns, name }: { ns: string; name: string }) => deleteKubernetesPod(ns, name),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["k8s"] }),
  });
  const restartDep = useMutation({
    mutationFn: ({ ns, name }: { ns: string; name: string }) =>
      restartKubernetesDeployment(ns, name),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["k8s"] }),
  });

  const podRows = (podsQ.data?.data ?? []).map((p) => {
    const name = String(p.name ?? "");
    const ns = String(p.namespace ?? "");
    return {
      name,
      namespace: ns,
      phase: <Badge variant={p.phase === "Running" ? "success" : "warning"}>{String(p.phase)}</Badge>,
      restarts: String(p.restarts ?? 0),
      node: String(p.node ?? "—"),
      images: (
        <span className="font-mono text-xs">
          {Array.isArray(p.images) ? p.images.join(", ") : "—"}
        </span>
      ),
      actions: (
        <Button
          size="sm"
          variant="destructive"
          disabled={delPod.isPending}
          onClick={() => {
            if (confirm(`Delete pod ${ns}/${name}? (controller will recreate)`)) {
              delPod.mutate({ ns, name });
            }
          }}
        >
          Delete
        </Button>
      ),
    };
  });

  const depRows = (depQ.data?.data ?? []).map((d) => {
    const name = String(d.name ?? "");
    const ns = String(d.namespace ?? "");
    return {
      name,
      namespace: ns,
      ready: `${d.readyReplicas ?? 0}/${d.replicas ?? 0}`,
      images: (
        <span className="font-mono text-xs">
          {Array.isArray(d.images) ? d.images.join(", ") : "—"}
        </span>
      ),
      actions: (
        <Button
          size="sm"
          variant="secondary"
          disabled={restartDep.isPending}
          onClick={() => restartDep.mutate({ ns, name })}
        >
          Restart
        </Button>
      ),
    };
  });

  const offline =
    podsQ.error instanceof Error
      ? podsQ.error.message
      : depQ.error instanceof Error
        ? depQ.error.message
        : null;

  return (
    <AppShell title="Kubernetes" description="Pods · Deployments (실데이터)">
      <div className="space-y-6">
        {offline ? <ErrorBanner message={offline} /> : null}

        <Card>
          <CardHeader>
            <CardTitle>Pods</CardTitle>
          </CardHeader>
          <CardContent>
            <DataTable
              columns={[
                { key: "name", header: "Name" },
                { key: "namespace", header: "Namespace" },
                { key: "phase", header: "Status" },
                { key: "restarts", header: "Restarts" },
                { key: "node", header: "Node" },
                { key: "images", header: "Image" },
                { key: "actions", header: "Actions" },
              ]}
              rows={podRows}
              empty={podsQ.isLoading ? "Loading…" : "No pods (cluster offline or empty)"}
            />
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Deployments</CardTitle>
          </CardHeader>
          <CardContent>
            <DataTable
              columns={[
                { key: "name", header: "Name" },
                { key: "namespace", header: "Namespace" },
                { key: "ready", header: "Ready" },
                { key: "images", header: "Image" },
                { key: "actions", header: "Actions" },
              ]}
              rows={depRows}
              empty={depQ.isLoading ? "Loading…" : "No deployments"}
            />
          </CardContent>
        </Card>
      </div>
    </AppShell>
  );
}
