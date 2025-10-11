// src/api/axiosClient.ts
import axios from "axios";

// Ưu tiên đọc từ env; mặc định dùng '/api' để chạy qua proxy Vite (dev)
const API_BASE = import.meta.env.VITE_API_BASE ?? "/api";

const axiosClient = axios.create({
  baseURL: API_BASE,
  headers: { "Content-Type": "application/json" },
  withCredentials: false,
});

export default axiosClient;
