import axiosClient from "./axiosClient";

const orderApi = {
  // 🟢 Lấy tất cả đơn hàng
  getAll: () => axiosClient.get("/bachhoa/orders"),

  // 🟢 Lấy 1 đơn hàng theo ID
  getById: (id: number) => axiosClient.get(`/bachhoa/orders?id=${id}`),

  // 🟢 Tạo đơn hàng mới
  create: (data: any) => axiosClient.post("/bachhoa/orders", data),

  // 🟢 Cập nhật đơn hàng (status, paymentMethod, ...)
  update: (id: number, data: any) => axiosClient.put(`/bachhoa/orders?id=${id}`, data),

  // 🔴 Xoá đơn hàng (nếu cần)
  delete: (id: number) => axiosClient.delete(`/bachhoa/orders?id=${id}`),
};

export default orderApi;
export { orderApi };
