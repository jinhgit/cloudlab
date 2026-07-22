import { test, expect } from "@playwright/test";

/**
 * Golden path smoke against Compose UI + Platform API data.
 * Scope: home → docker → monitoring (visibility only).
 */
test.describe("CloudLab dashboard smoke", () => {
  test("home shows ops dashboard chrome and live status widgets", async ({
    page,
  }) => {
    await page.goto("/");

    // Shell
    await expect(page.getByText("CloudLab", { exact: false }).first()).toBeVisible();
    await expect(page.getByRole("link", { name: "Dashboard" })).toBeVisible();

    // Home title / description
    await expect(page.getByRole("heading", { name: "Dashboard" })).toBeVisible();

    // Stat cards from live /api/server/status (labels always rendered)
    await expect(page.getByText("CPU", { exact: true }).first()).toBeVisible();
    await expect(page.getByText("Memory", { exact: true }).first()).toBeVisible();
    await expect(page.getByText("Containers", { exact: true }).first()).toBeVisible();

    // Integration badges area
    await expect(page.getByText("Integrations", { exact: false })).toBeVisible({
      timeout: 30_000,
    });

    // Header API badge eventually resolves (UP or Offline — must not stay blank forever)
    const headerBadge = page.locator("header").getByText(/API (UP|Offline)|Checking/i);
    await expect(headerBadge.first()).toBeVisible({ timeout: 30_000 });
  });

  test("docker page lists containers or shows structured empty/error", async ({
    page,
  }) => {
    await page.goto("/docker");
    await expect(page.getByRole("heading", { name: "Docker" })).toBeVisible();

    // Either a data table with Name column or empty/error banner
    const nameHeader = page.getByText("Name", { exact: true });
    const empty = page.getByText(/No containers|Loading|not reachable|socket/i);
    await expect(nameHeader.or(empty).first()).toBeVisible({ timeout: 30_000 });
  });

  test("monitoring page renders chart section", async ({ page }) => {
    await page.goto("/monitoring");
    await expect(page.getByRole("heading", { name: "Monitoring" })).toBeVisible();
    await expect(page.getByText("CPU %", { exact: false }).or(page.getByText("CPU (1h)")).first()).toBeVisible({
      timeout: 30_000,
    });
  });
});
