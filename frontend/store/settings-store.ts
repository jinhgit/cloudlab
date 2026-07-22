"use client";

import { create } from "zustand";
import { persist } from "zustand/middleware";
import type { AppSettings } from "@/types/settings";

type SettingsState = AppSettings & {
  setApiUrl: (url: string) => void;
  setWsUrl: (url: string) => void;
  setPollingMs: (ms: number) => void;
  setDarkMode: (on: boolean) => void;
};

const defaults: AppSettings = {
  apiUrl: process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080",
  wsUrl: process.env.NEXT_PUBLIC_WS_URL || "ws://localhost:8080/ws",
  pollingMs: 5000,
  darkMode: true,
};

export const useSettingsStore = create<SettingsState>()(
  persist(
    (set) => ({
      ...defaults,
      setApiUrl: (apiUrl) => set({ apiUrl }),
      setWsUrl: (wsUrl) => set({ wsUrl }),
      setPollingMs: (pollingMs) => set({ pollingMs }),
      setDarkMode: (darkMode) => set({ darkMode }),
    }),
    {
      name: "cloudlab-settings",
      partialize: (s) => ({
        apiUrl: s.apiUrl,
        wsUrl: s.wsUrl,
        pollingMs: s.pollingMs,
        darkMode: s.darkMode,
      }),
    }
  )
);
