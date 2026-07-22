import { AppShell } from "@/components/layout/app-shell";
import { PagePlaceholder } from "@/components/layout/page-placeholder";

export default function DockerPage() {
  return (
    <AppShell title="Docker" description="컨테이너 목록 · Start/Stop/Restart/Logs">
      <PagePlaceholder
        title="Docker"
        description="Docker Engine API 어댑터 연동 예정."
        upcoming={["Container list", "Start / Stop / Restart / Remove", "Logs · Inspect"]}
      />
    </AppShell>
  );
}
