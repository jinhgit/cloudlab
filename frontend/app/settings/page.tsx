"use client";

import { useState } from "react";
import { AppShell } from "@/components/layout/app-shell";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useSettingsStore } from "@/store/settings-store";

export default function SettingsPage() {
  const apiUrl = useSettingsStore((s) => s.apiUrl);
  const wsUrl = useSettingsStore((s) => s.wsUrl);
  const pollingMs = useSettingsStore((s) => s.pollingMs);
  const darkMode = useSettingsStore((s) => s.darkMode);
  const setApiUrl = useSettingsStore((s) => s.setApiUrl);
  const setWsUrl = useSettingsStore((s) => s.setWsUrl);
  const setPollingMs = useSettingsStore((s) => s.setPollingMs);

  const [draftApi, setDraftApi] = useState(apiUrl);
  const [draftWs, setDraftWs] = useState(wsUrl);
  const [draftPoll, setDraftPoll] = useState(String(pollingMs));
  const [saved, setSaved] = useState(false);

  function onSave(e: React.FormEvent) {
    e.preventDefault();
    setApiUrl(draftApi.trim());
    setWsUrl(draftWs.trim());
    const n = Number(draftPoll);
    if (!Number.isNaN(n) && n >= 1000) {
      setPollingMs(n);
    }
    setSaved(true);
    setTimeout(() => setSaved(false), 2000);
  }

  return (
    <AppShell title="Settings" description="환경설정 · API URL · Polling">
      <div className="mx-auto max-w-2xl space-y-4">
        <Card>
          <CardHeader>
            <CardTitle>Connection</CardTitle>
            <CardDescription>
              브라우저 로컬에 저장됩니다. GitHub Token / Discord Webhook 등 시크릿은
              서버 측에만 둡니다 (PRD §15).
            </CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={onSave} className="space-y-4">
              <label className="block space-y-1.5">
                <span className="text-xs font-medium text-muted-foreground">API URL</span>
                <input
                  className="h-9 w-full rounded-md border border-input bg-secondary px-3 text-sm outline-none ring-offset-background focus-visible:ring-2 focus-visible:ring-ring"
                  value={draftApi}
                  onChange={(e) => setDraftApi(e.target.value)}
                  placeholder="http://localhost:8080"
                />
              </label>
              <label className="block space-y-1.5">
                <span className="text-xs font-medium text-muted-foreground">WebSocket URL</span>
                <input
                  className="h-9 w-full rounded-md border border-input bg-secondary px-3 text-sm outline-none focus-visible:ring-2 focus-visible:ring-ring"
                  value={draftWs}
                  onChange={(e) => setDraftWs(e.target.value)}
                  placeholder="ws://localhost:8080/ws"
                />
              </label>
              <label className="block space-y-1.5">
                <span className="text-xs font-medium text-muted-foreground">
                  Polling interval (ms)
                </span>
                <input
                  type="number"
                  min={1000}
                  step={500}
                  className="h-9 w-full rounded-md border border-input bg-secondary px-3 text-sm outline-none focus-visible:ring-2 focus-visible:ring-ring"
                  value={draftPoll}
                  onChange={(e) => setDraftPoll(e.target.value)}
                />
              </label>
              <div className="flex items-center justify-between rounded-md border border-border bg-secondary/40 px-3 py-2">
                <div>
                  <div className="text-sm">Dark mode</div>
                  <div className="text-xs text-muted-foreground">
                    기본값 ON (forced). 토글 UI는 유지·확장 예정.
                  </div>
                </div>
                <span className="text-xs font-medium text-success">
                  {darkMode ? "Enabled" : "Disabled"}
                </span>
              </div>
              <div className="flex items-center gap-3">
                <Button type="submit">Save</Button>
                {saved ? (
                  <span className="text-xs text-success">Saved</span>
                ) : null}
              </div>
            </form>
          </CardContent>
        </Card>
      </div>
    </AppShell>
  );
}
