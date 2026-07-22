import { AppShell } from "@/components/layout/app-shell";
import { PagePlaceholder } from "@/components/layout/page-placeholder";

export default function MonitoringPage() {
  return (
    <AppShell title="Monitoring" description="Prometheus 메트릭 차트">
      <PagePlaceholder
        title="Monitoring"
        description="Recharts + Prometheus query_range (Step 7·10)."
        upcoming={[
          "CPU / Memory / Disk / Network",
          "JVM Heap · GC · HTTP TPS",
          "Docker · Pod stats",
        ]}
      />
    </AppShell>
  );
}
