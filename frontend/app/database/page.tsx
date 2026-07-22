import { AppShell } from "@/components/layout/app-shell";
import { PagePlaceholder } from "@/components/layout/page-placeholder";

export default function DatabasePage() {
  return (
    <AppShell title="Database" description="PostgreSQL 상태">
      <PagePlaceholder
        title="Database"
        description="플랫폼 DB 메트릭 및 백업 상태."
        upcoming={["DB size · connections · tables", "Slow query", "Backup / Restore status"]}
      />
    </AppShell>
  );
}
