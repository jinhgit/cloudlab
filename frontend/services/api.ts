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
      // ignore
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
      "Content-Type": "application/json",
      ...(init?.headers ?? {}),
    },
    cache: "no-store",
  });

  if (!res.ok) {
    let message = `API ${res.status}: ${path}`;
    try {
      const body = (await res.json()) as ApiResponse<unknown>;
      if (body?.error?.message) message = body.error.message;
    } catch {
      // ignore
    }
    throw new Error(message);
  }

  return res.json() as Promise<T>;
}

export async function fetchApiHealth(): Promise<ApiResponse<HealthPayload>> {
  return request("/api/health");
}

export async function fetchPlatformInfo(): Promise<ApiResponse<PlatformInfo>> {
  return request("/api/platform/info");
}

export async function fetchServerStatus(): Promise<ApiResponse<Record<string, unknown>>> {
  return request("/api/server/status");
}

export async function fetchDockerContainers(
  all = true
): Promise<ApiResponse<Record<string, unknown>[]>> {
  return request(`/api/docker/containers?all=${all}`);
}

export async function dockerAction(
  id: string,
  action: "start" | "stop" | "restart"
): Promise<ApiResponse<Record<string, string>>> {
  return request(`/api/docker/containers/${id}/${action}`, { method: "POST" });
}

export async function fetchDockerLogs(
  id: string,
  tail = 200
): Promise<ApiResponse<{ logs: string }>> {
  return request(`/api/docker/containers/${id}/logs?tail=${tail}`);
}

export async function fetchKubernetesPods(
  namespace = "all"
): Promise<ApiResponse<Record<string, unknown>[]>> {
  return request(`/api/kubernetes/pods?namespace=${encodeURIComponent(namespace)}`);
}

export async function fetchKubernetesDeployments(
  namespace = "all"
): Promise<ApiResponse<Record<string, unknown>[]>> {
  return request(`/api/kubernetes/deployments?namespace=${encodeURIComponent(namespace)}`);
}

export async function deleteKubernetesPod(
  namespace: string,
  name: string
): Promise<ApiResponse<Record<string, string>>> {
  return request(
    `/api/kubernetes/pods/${encodeURIComponent(namespace)}/${encodeURIComponent(name)}`,
    { method: "DELETE" }
  );
}

export async function restartKubernetesDeployment(
  namespace: string,
  name: string
): Promise<ApiResponse<Record<string, string>>> {
  return request(
    `/api/kubernetes/deployments/${encodeURIComponent(namespace)}/${encodeURIComponent(name)}/restart`,
    { method: "POST" }
  );
}

export async function fetchPrometheusSummary(): Promise<
  ApiResponse<Record<string, unknown>>
> {
  return request("/api/prometheus/summary");
}

export async function fetchPrometheusRange(
  query: string,
  rangeSeconds = 3600
): Promise<ApiResponse<Record<string, unknown>>> {
  const end = Math.floor(Date.now() / 1000);
  const start = end - rangeSeconds;
  const q = new URLSearchParams({
    query,
    start: String(start),
    end: String(end),
    step: "30s",
  });
  return request(`/api/prometheus/query_range?${q}`);
}

export async function fetchLogs(params: {
  query?: string;
  limit?: number;
  rangeSeconds?: number;
}): Promise<ApiResponse<Record<string, unknown>>> {
  const q = new URLSearchParams({
    query: params.query ?? '{compose_project="cloudlab"}',
    limit: String(params.limit ?? 100),
    rangeSeconds: String(params.rangeSeconds ?? 3600),
  });
  return request(`/api/logs?${q}`);
}

export async function fetchAlerts(): Promise<ApiResponse<Record<string, unknown>[]>> {
  return request("/api/alerts");
}

export async function fetchDeployments(
  perPage = 20
): Promise<ApiResponse<{ configured: boolean; runs: Record<string, unknown>[] }>> {
  return request(`/api/deployments?perPage=${perPage}`);
}

export async function fetchDatabaseStatus(): Promise<ApiResponse<Record<string, unknown>>> {
  return request("/api/database/status");
}

export async function fetchRedisStatus(): Promise<ApiResponse<Record<string, unknown>>> {
  return request("/api/redis/status");
}
