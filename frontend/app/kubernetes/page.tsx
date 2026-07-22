import { AppShell } from "@/components/layout/app-shell";
import { PagePlaceholder } from "@/components/layout/page-placeholder";

export default function KubernetesPage() {
  return (
    <AppShell title="Kubernetes" description="Pod · Deployment 조회 및 조작">
      <PagePlaceholder
        title="Kubernetes"
        description="k3s API를 Platform API를 통해 연동합니다."
        upcoming={[
          "Pod 목록 · 상세 · Logs / Restart / Delete / Events",
          "Deployment Restart · Rolling Update · Scale",
        ]}
      />
    </AppShell>
  );
}
