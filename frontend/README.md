# frontend/ — CloudLab Dashboard

Next.js 15 · TypeScript · TailwindCSS · shadcn-style UI · React Query · Zustand

상세: [docs/frontend.md](../docs/frontend.md)

```bash
cp .env.example .env.local
npm install
npm run dev
# http://localhost:3000
```

Backend health (optional):

```bash
# terminal 2
cd ../backend && ./gradlew bootRun
```

Production build (Docker-compatible standalone):

```bash
npm run build
npm start
```
