import {
  Activity,
  AlertTriangle,
  Container,
  Database,
  HardDrive,
  LayoutDashboard,
  Rocket,
  ScrollText,
  Settings,
  ShipWheel,
} from "lucide-react";
import type { NavItem } from "@/types/navigation";

/** PRD §5 sidebar order — do not reorder without updating PRD. */
export const NAV_ITEMS: NavItem[] = [
  {
    title: "Dashboard",
    href: "/",
    icon: LayoutDashboard,
    description: "서버 · 리소스 · 최근 이벤트 요약",
  },
  {
    title: "Kubernetes",
    href: "/kubernetes",
    icon: ShipWheel,
    description: "Pod · Deployment 관리",
  },
  {
    title: "Docker",
    href: "/docker",
    icon: Container,
    description: "컨테이너 조회 · 조작",
  },
  {
    title: "Deployments",
    href: "/deployments",
    icon: Rocket,
    description: "CI/CD · 롤백 · 진행률",
  },
  {
    title: "Monitoring",
    href: "/monitoring",
    icon: Activity,
    description: "Prometheus 메트릭",
  },
  {
    title: "Logs",
    href: "/logs",
    icon: ScrollText,
    description: "Loki 로그 스트리밍",
  },
  {
    title: "Database",
    href: "/database",
    icon: Database,
    description: "PostgreSQL 상태",
  },
  {
    title: "Redis",
    href: "/redis",
    icon: HardDrive,
    description: "Redis 메트릭",
  },
  {
    title: "Alerts",
    href: "/alerts",
    icon: AlertTriangle,
    description: "Alertmanager",
  },
  {
    title: "Settings",
    href: "/settings",
    icon: Settings,
    description: "환경 · 토큰 · Polling",
  },
];
