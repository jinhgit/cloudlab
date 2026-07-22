import { describe, expect, it } from "vitest";

/** Mirrors Logs page parser logic for unit coverage. */
function parseLoki(data: Record<string, unknown> | undefined): { ts: string; line: string }[] {
  try {
    const result =
      (data?.data as { result?: { values?: [string, string][] }[] })?.result ?? [];
    const lines: { ts: string; line: string }[] = [];
    for (const stream of result) {
      for (const [ts, line] of stream.values ?? []) {
        const ms = Number(ts) / 1e6;
        lines.push({
          ts: Number.isFinite(ms) ? new Date(ms).toISOString() : ts,
          line,
        });
      }
    }
    return lines.reverse();
  } catch {
    return [];
  }
}

describe("parseLoki", () => {
  it("returns empty for missing data", () => {
    expect(parseLoki(undefined)).toEqual([]);
    expect(parseLoki({})).toEqual([]);
  });

  it("flattens streams and reverses for chronological UI", () => {
    const ns1 = String(1_700_000_000_000_000_000n);
    const ns2 = String(1_700_000_000_100_000_000n);
    const lines = parseLoki({
      data: {
        result: [
          {
            values: [
              [ns2, "second"],
              [ns1, "first"],
            ],
          },
        ],
      },
    });
    expect(lines).toHaveLength(2);
    expect(lines[0].line).toBe("first");
    expect(lines[1].line).toBe("second");
  });
});
