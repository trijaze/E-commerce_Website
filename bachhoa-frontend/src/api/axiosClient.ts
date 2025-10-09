// src/api/axiosClient.ts
import axios from "axios";

// HOA: gọi trực tiếp Tomcat (bỏ qua proxy Vite)
const axiosClient = axios.create({
  baseURL: "http://localhost:8080/ecommerce-hoa/api",
  headers: { "Content-Type": "application/json" },
  withCredentials: false,
});

export default axiosClient;
