import { AppShell } from "@/components/layout/app-shell";
import { PagePlaceholder } from "@/components/layout/page-placeholder";

export default function AlertsPage() {
  return (
    <AppShell title="Alerts" description="Alertmanager · Critical / Warning / Info">
      <PagePlaceholder
        title="Alerts"
        description="Alertmanager API + ACK 상태 (Step 7·10)."
        upcoming={["Firing / resolved history", "Recovery time", "Acknowledge"]}
      />
    </AppShell>
  );
}
