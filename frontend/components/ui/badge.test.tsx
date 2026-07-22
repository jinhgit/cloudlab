import { describe, expect, it } from "vitest";
import { render, screen } from "@testing-library/react";
import { Badge } from "@/components/ui/badge";

describe("Badge", () => {
  it("renders children", () => {
    render(<Badge>API UP</Badge>);
    expect(screen.getByText("API UP")).toBeInTheDocument();
  });

  it("applies success variant class", () => {
    const { container } = render(<Badge variant="success">ok</Badge>);
    expect(container.firstChild).toHaveClass("bg-success/15");
  });
});
