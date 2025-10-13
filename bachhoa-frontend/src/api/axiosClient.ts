import axios, { AxiosRequestConfig } from "axios";

import {
  getAccessToken,
  getRefreshToken,
  setAccessToken,
  setRefreshToken,
  clearTokens,
} from "../utils/token";

const baseURL = "http://localhost:8080/bachhoa/api";

const axiosClient = axios.create({
  baseURL,
  headers: { "Content-Type": "application/json" },
  withCredentials: false,
});

let isRefreshing = false;
let failedQueue: Array<{ resolve: (token: string) => void; reject: (err: any) => void }> = [];

const processQueue = (error: any, token: string | null = null) => {
  failedQueue.forEach((p) => {
    if (error) p.reject(error);
    else if (token) p.resolve(token);
  });
  failedQueue = [];
};

interface RetryConfig extends AxiosRequestConfig {
  _retry?: boolean;
}

axiosClient.interceptors.request.use((config) => {
  const token = getAccessToken();
  if (token) {
    config.headers = config.headers ?? {};
    config.headers["Authorization"] = `Bearer ${token}`;
  }
  return config;
});

axiosClient.interceptors.response.use(
  (res) => res,
  async (err) => {
    const originalConfig = err.config as RetryConfig;
    if (!originalConfig) return Promise.reject(err);

    // Chỉ xử lý lỗi 401 và đây là lần thử lại đầu tiên
    if (err.response?.status === 401 && !originalConfig._retry) {
      originalConfig._retry = true;

      // Nếu đang có một quá trình refresh khác đang chạy, hãy đưa request này vào hàng đợi
      if (isRefreshing) {
        return new Promise<string>((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            originalConfig.headers = originalConfig.headers ?? {};
            originalConfig.headers["Authorization"] = "Bearer " + token;
            // Gửi lại request đã được tạm dừng với token mới
            return axiosClient(originalConfig);
          })
          .catch((e) => Promise.reject(e));
      }

      isRefreshing = true;
      const refreshToken = getRefreshToken();

      // Nếu không có refresh token, không thể làm gì hơn -> đăng xuất
      if (!refreshToken) {
        clearTokens();
        isRefreshing = false;
        // Chuyển hướng về trang đăng nhập
        window.location.href = '/login';
        return Promise.reject(err);
      }

      try {
        const { data } = await axiosClient.post(`${baseURL}/auth/refresh`, { refreshToken });

        setAccessToken(data.accessToken);
        if (data?.refreshToken) setRefreshToken(data.refreshToken); // Xử lý nếu backend trả về refresh token mới

        // Xử lý hàng đợi: thông báo cho các request khác rằng đã có token mới
        processQueue(null, data.accessToken);

        // Gửi lại request ban đầu với token mới
        originalConfig.headers = originalConfig.headers ?? {};
        originalConfig.headers["Authorization"] = "Bearer " + data.accessToken;
        return axiosClient(originalConfig);

      } catch (e) {
        // Nếu ngay cả việc refresh cũng thất bại
        processQueue(e, null);
        clearTokens();
        // Chuyển hướng về trang đăng nhập
        window.location.href = '/login';
        return Promise.reject(e);

      } finally {
        isRefreshing = false;
      }
    }

    // ✅ SỬA LỖI LOGIC QUAN TRỌNG:
    // Đối với các lỗi khác không phải 401, hoặc nếu đây là lần thử lại thất bại,
    // hãy trả về reject ngay lập tức.
    // Việc xóa bỏ `return Promise.reject(err)` ở cuối khối `if (401)` cũ là chìa khóa
    // để ngăn chặn "lá thư ban đầu" báo cáo thất bại quá sớm.
    return Promise.reject(err);
  }
);

export default axiosClient;

