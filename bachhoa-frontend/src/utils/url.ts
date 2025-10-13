// src/utils/url.ts
export const API_BASE =
  import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/bachhoa";

export function resolveUrl(u?: string | null) {
  if (!u) return "/placeholder.png";
  if (/^https?:\/\//i.test(u)) return u;
  return `${API_BASE}${u.startsWith("/") ? "" : "/"}${u}`;
}
