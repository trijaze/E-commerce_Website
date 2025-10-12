// src/api/axiosClient.ts
import axios, {
  AxiosResponse,
  AxiosError,
  InternalAxiosRequestConfig,
} from "axios";
import {
  getAccessToken,
  getRefreshToken,
  setAccessToken,
  setRefreshToken,
  clearTokens,
} from "../utils/token";

// Dev: để ENV trống -> dùng "/api" (proxy trong vite.config.ts)
// Prod: set VITE_API_BASE_URL = "https://your-domain/bachhoa-backend/api" (hoặc ".../bachhoa/api")
const API_BASE: string = (import.meta as any).env?.VITE_API_BASE_URL ?? "/api";

const axiosClient = axios.create({
  baseURL: API_BASE,
  headers: { "Content-Type": "application/json" },
});

// ====== Helper set header an toàn cho axios v1 (AxiosHeaders hoặc object) ======
function setAuthHeader(headers: any, token: string) {
  if (!headers) return;
  if (typeof headers.set === "function") {
    // AxiosHeaders instance
    headers.set("Authorization", `Bearer ${token}`);
  } else {
    // Plain object
    headers["Authorization"] = `Bearer ${token}`;
  }
}

// ====== Request interceptor (gắn bearer nếu có) ======
axiosClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig): InternalAxiosRequestConfig => {
    const token = getAccessToken();
    if (token) setAuthHeader(config.headers, token);
    return config;
  },
  (error: AxiosError) => Promise.reject(error)
);

// Đánh dấu đã retry để tránh vòng lặp
type RetryConfig = InternalAxiosRequestConfig & { _retry?: boolean };

// Hàng đợi request chờ refresh
let isRefreshing = false;
let failedQueue: Array<{ resolve: (t: string) => void; reject: (e: any) => void }> = [];
const processQueue = (error: any, token: string | null) => {
  failedQueue.forEach(p => (token ? p.resolve(token) : p.reject(error)));
  failedQueue = [];
};

// ====== Response interceptor (tự refresh khi 401) ======
axiosClient.interceptors.response.use(
  (response: AxiosResponse) => response,
  async (error: AxiosError) => {
    const originalConfig = (error.config || {}) as RetryConfig;

    if (error.response?.status === 401 && !originalConfig._retry) {
      originalConfig._retry = true;

      // Nếu đã có refresh đang chạy -> xếp hàng chờ token mới
      if (isRefreshing) {
        return new Promise<string>((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        }).then((newAccess) => {
          setAuthHeader(originalConfig.headers, newAccess);
          return axiosClient(originalConfig as any);
        });
      }

      isRefreshing = true;
      try {
        const refreshToken = getRefreshToken();
        if (!refreshToken) throw new Error("No refresh token");

        // Dùng axios gốc + API_BASE tuyệt đối
        const r = await axios.post(`${API_BASE}/auth/refresh`, { refreshToken });
        const newAccess = (r.data as any)?.accessToken;
        const newRefresh = (r.data as any)?.refreshToken;
        if (!newAccess) throw new Error("No new access token");

        setAccessToken(newAccess);
        if (newRefresh) setRefreshToken(newRefresh);

        processQueue(null, newAccess);
        setAuthHeader(originalConfig.headers, newAccess);
        return axiosClient(originalConfig as any);
      } catch (e) {
        processQueue(e, null);
        clearTokens();
        return Promise.reject(e);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

export default axiosClient;
