import { AppShell } from "@/components/layout/app-shell";
import { PagePlaceholder } from "@/components/layout/page-placeholder";

export default function LogsPage() {
  return (
    <AppShell title="Logs" description="Loki 검색 · 실시간 스트리밍">
      <PagePlaceholder
        title="Logs"
        description="Loki Query API + 스트림 (Step 8·10)."
        upcoming={["Filters: container / namespace / level / keyword", "Auto scroll", "Download"]}
      />
    </AppShell>
  );
}
