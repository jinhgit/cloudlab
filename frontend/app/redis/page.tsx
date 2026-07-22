import { AppShell } from "@/components/layout/app-shell";
import { PagePlaceholder } from "@/components/layout/page-placeholder";

export default function RedisPage() {
  return (
    <AppShell title="Redis" description="Memory · Hit ratio · Eviction">
      <PagePlaceholder
        title="Redis"
        description="Redis INFO 기반 요약 카드 예정."
        upcoming={["Memory", "Key count", "Hit ratio", "TTL / Eviction"]}
      />
    </AppShell>
  );
}
