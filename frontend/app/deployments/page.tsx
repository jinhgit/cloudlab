import { AppShell } from "@/components/layout/app-shell";
import { PagePlaceholder } from "@/components/layout/page-placeholder";

export default function DeploymentsPage() {
  return (
    <AppShell title="Deployments" description="GitHub Actions · Deploy · Rollback">
      <PagePlaceholder
        title="Deployments"
        description="CI/CD 진행률을 Dashboard에서 추적합니다."
        upcoming={["Recent workflows", "Deploy Latest / Rollback", "WebSocket progress"]}
      />
    </AppShell>
  );
}
