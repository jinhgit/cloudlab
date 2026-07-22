# Frontend — Next.js Dashboard (Step 4)

## Overview

| Item | Value |
|------|--------|
| Module | `frontend/` |
| Framework | Next.js 15 (App Router) |
| Language | TypeScript |
| Styling | TailwindCSS v4 + shadcn-style primitives |
| State | React Query (server) · Zustand (settings) |
| Theme | **Dark default** (`forcedTheme=dark`) |
| Docker | `output: "standalone"` |

## Exit criteria (Step 4)

- [x] App boots (`npm run dev` / `npm run build`)
- [x] Dark shell layout
- [x] Sidebar with PRD menu order
- [x] Route stubs for all sidebar pages
- [x] Settings store (API URL, polling)
- [x] Optional live probe to Platform API `/api/health`

## Routes

| Path | Page |
|------|------|
| `/` | Dashboard home widgets (placeholders + API status) |
| `/kubernetes` | shell |
| `/docker` | shell |
| `/deployments` | shell |
| `/monitoring` | shell |
| `/logs` | shell |
| `/database` | shell |
| `/redis` | shell |
| `/alerts` | shell |
| `/settings` | local settings form |

## Layout

```text
Sidebar (fixed) | Header (title + API badge)
                | Main content
```

## Packages (PRD stack)

- next, react, typescript
- tailwindcss, class-variance-authority, clsx, tailwind-merge
- @tanstack/react-query, zustand
- lucide-react, recharts, socket.io-client (wired later)
- next-themes (dark forced)

## Run

```bash
cd frontend
cp .env.example .env.local
npm install
npm run dev
```

## Next steps

- Step 5: Compose with backend
- Step 10: real adapters, charts, tables, WebSocket
