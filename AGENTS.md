# AGENTS.md — AI Development Contract for CloudLab

This repository is a **portfolio-grade self-hosted DevOps / Platform Engineering project**, not a CRUD demo.

Authoritative requirements: [`docs/PRD.md`](docs/PRD.md).

## Identity

- Product name: **CloudLab**
- Type: Internal operations platform (AWS Console–like for a self-hosted lab)
- Audience: Platform / DevOps / Cloud engineer hiring managers

## Hard Rules

1. **Follow Development Rules order** (PRD §23). Do not jump to UI polish before foundations exist unless the current step explicitly includes it.
2. **Do not invent architecture.** Decisions are locked in PRD §28. Changing stack requires updating `docs/PRD.md` first.
3. **No permanent mock data.** Temporary mocks only behind ports/adapters when the real system is not up yet.
4. **Browser never holds infra credentials.** Docker socket, kubeconfig, Prometheus admin access → Platform API only.
5. **Dark theme + shadcn/ui + Tailwind** for frontend. Do not add alternate UI kits.
6. **Small commits** with conventional prefixes: `feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `ci:`, `infra:`, `monitoring:`, `logging:`.
7. **New tech ⇒ new doc** under `docs/`.
8. Prioritize **Observability, Security, Maintainability** over feature breadth.
9. Java base package: `com.cloudlab`.
10. API response envelope and roles: see PRD §16 and §21.

## Current Step

See [`docs/development-plan.md`](docs/development-plan.md). Implement only the active step and its exit criteria.

## Out of Scope (v1)

- Terraform / Ansible (v2)
- Multi-cluster, multi-tenant SaaS
- Paid cloud control planes as dependencies

## When Uncertain

1. Read `docs/PRD.md` and `docs/architecture.md`.
2. Prefer the option that improves the **5-minute interview demo**.
3. Ask the user only if a decision is not covered by the PRD decision log.
