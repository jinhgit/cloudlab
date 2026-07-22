"use client";

import { useQuery } from "@tanstack/react-query";
import { fetchApiHealth, fetchPlatformInfo } from "@/services/api";
import { useSettingsStore } from "@/store/settings-store";

export function usePlatformHealth() {
  const pollingMs = useSettingsStore((s) => s.pollingMs);

  return useQuery({
    queryKey: ["platform", "health"],
    queryFn: fetchApiHealth,
    refetchInterval: pollingMs,
    retry: 1,
  });
}

export function usePlatformInfo() {
  return useQuery({
    queryKey: ["platform", "info"],
    queryFn: fetchPlatformInfo,
    staleTime: 60_000,
    retry: 1,
  });
}
