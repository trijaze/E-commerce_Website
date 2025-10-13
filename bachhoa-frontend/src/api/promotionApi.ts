import axiosClient from "./axiosClient";

export const promotionApi = {
  // Lấy tất cả promotion
  getAll: () => axiosClient.get("/promotions"),

  // Lọc theo category / product / variant
  getByCategory: (categoryId: number) =>
    axiosClient.get("/promotions", { params: { categoryId } }),

  getByProduct: (productId: number) =>
    axiosClient.get("/promotions", { params: { productId } }),

  getByVariant: (variantId: number) =>
    axiosClient.get("/promotions", { params: { variantId } }),

  /**
   * ✅ Kiểm tra mã giảm giá — trả về 1 object Promotion duy nhất
   * Nếu backend trả về mảng thì ta tự lấy phần tử đầu tiên
   */
  checkCode: async (code: string) => {
    const res = await axiosClient.get(`/promotions`, { params: { code } });

    // ⚙️ Nếu backend trả về mảng, lấy phần tử đầu tiên
    if (Array.isArray(res.data)) {
      if (res.data.length === 0) {
        throw new Error("Promotion not found");
      }
      return { data: res.data[0] };
    }

    // ⚙️ Nếu backend trả về object thì dùng trực tiếp
    return res;
  },

  // CRUD cơ bản cho Admin
  getById: (id: number) => axiosClient.get(`/promotions/${id}`),

  create: (data: any) => axiosClient.post(`/promotions`, data),

  update: (id: number, data: any) => axiosClient.put(`/promotions/${id}`, data),

  delete: (id: number) => axiosClient.delete(`/promotions/${id}`),
};
