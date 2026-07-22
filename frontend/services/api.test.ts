import { afterEach, describe, expect, it, vi } from "vitest";
import { getApiBaseUrl } from "@/services/api";

describe("getApiBaseUrl", () => {
  afterEach(() => {
    localStorage.clear();
    vi.unstubAllEnvs();
  });

  it("falls back to default when no stored settings", () => {
    const url = getApiBaseUrl();
    expect(url).toMatch(/localhost:8080|http/);
  });

  it("reads apiUrl from zustand-persisted localStorage", () => {
    localStorage.setItem(
      "cloudlab-settings",
      JSON.stringify({ state: { apiUrl: "http://api.example:9090/" } })
    );
    expect(getApiBaseUrl()).toBe("http://api.example:9090");
  });
});
