import type { NextConfig } from "next";
import path from "path";

const nextConfig: NextConfig = {
  // Required by docker/Dockerfile.frontend (standalone output)
  output: "standalone",
  // Avoid picking parent monorepo/home lockfiles as tracing root
  outputFileTracingRoot: path.join(__dirname),
  poweredByHeader: false,
  reactStrictMode: true,
};

export default nextConfig;
