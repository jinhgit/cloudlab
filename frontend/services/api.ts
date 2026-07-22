import type { ApiResponse, HealthPayload, PlatformInfo } from "@/types/api";

const DEFAULT_API_URL =
  process.env.NEXT_PUBLIC_API_URL?.replace(/\/$/, "") || "http://localhost:8080";

export function getApiBaseUrl(): string {
  if (typeof window !== "undefined") {
    try {
      const stored = localStorage.getItem("cloudlab-settings");
      if (stored) {
        const parsed = JSON.parse(stored) as { state?: { apiUrl?: string } };
        if (parsed.state?.apiUrl) {
          return parsed.state.apiUrl.replace(/\/$/, "");
        }
      }
    } catch {
      // ignore storage parse errors
    }
  }
  return DEFAULT_API_URL;
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const base = getApiBaseUrl();
  const res = await fetch(`${base}${path}`, {
    ...init,
    headers: {
      Accept: "application/json",
      ...(init?.headers ?? {}),
    },
    cache: "no-store",
  });

  if (!res.ok) {
    throw new Error(`API ${res.status}: ${path}`);
  }

  return res.json() as Promise<T>;
}

export async function fetchApiHealth(): Promise<ApiResponse<HealthPayload>> {
  return request<ApiResponse<HealthPayload>>("/api/health");
}

export async function fetchPlatformInfo(): Promise<ApiResponse<PlatformInfo>> {
  return request<ApiResponse<PlatformInfo>>("/api/platform/info");
}

export async function fetchActuatorHealth(): Promise<{ status: string }> {
  return request<{ status: string }>("/actuator/health");
}
