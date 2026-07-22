/** Common API envelope — matches backend ApiResponse (PRD §16). */
export type ApiResponse<T> = {
  success: boolean;
  data: T | null;
  error: { code: string; message: string } | null;
};

export type PlatformInfo = {
  name: string;
  version: string;
  description: string;
};

export type HealthPayload = {
  status: string;
  service: string;
  timestamp: string;
};
