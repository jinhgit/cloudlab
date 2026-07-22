"use client";

import { usePlatformHealth } from "@/hooks/use-platform-health";
import { Badge } from "@/components/ui/badge";
import { useSettingsStore } from "@/store/settings-store";

type HeaderProps = {
  title: string;
  description?: string;
};

export function Header({ title, description }: HeaderProps) {
  const apiUrl = useSettingsStore((s) => s.apiUrl);
  const { data, isError, isFetching, isLoading } = usePlatformHealth();

  const status = data?.data?.status;
  const connected = !isError && status === "UP";

  return (
    <header className="flex h-14 shrink-0 items-center justify-between border-b border-border px-6">
      <div>
        <h1 className="text-sm font-semibold tracking-tight">{title}</h1>
        {description ? (
          <p className="text-xs text-muted-foreground">{description}</p>
        ) : null}
      </div>

      <div className="flex items-center gap-3">
        <span className="hidden font-mono text-[11px] text-muted-foreground sm:inline">
          {apiUrl}
        </span>
        {isLoading || isFetching ? (
          <Badge variant="secondary">Checking…</Badge>
        ) : connected ? (
          <Badge variant="success">API UP</Badge>
        ) : (
          <Badge variant="warning">API Offline</Badge>
        )}
      </div>
    </header>
  );
}
