import axiosClient from "./axiosClient";
import type { Product } from "../features/products/productTypes";

// URL gốc (không kèm /api)
export const BASE_URL = "http://localhost:8080/bachhoa";

export interface ProductListQuery {
  take?: number;              // FE -> map sang limit của BE
  offset?: number;            // offset
  q?: string;                 // tìm theo tên
  categoryId?: number | string; // lọc theo danh mục
}

// Chuẩn hóa danh sách ảnh của sản phẩm
function normalizeImages(x: any): string[] {
  // Nếu backend trả về imageUrls (mảng string)
  if (Array.isArray(x.imageUrls)) {
    return x.imageUrls.map((path: string) => {
      if (!path) return "";
      if (path.startsWith("http")) return path; // đã đủ URL
      if (path.startsWith("/bachhoa")) return `http://localhost:8080${path}`;
      if (path.startsWith("images/")) return `${BASE_URL}/${path}`; // quan trọng nè
      return `${BASE_URL}/images/${path}`;
    }).filter(Boolean);
  }

  // Cũ: nếu backend trả về images hoặc imageUrl
  if (Array.isArray(x.images)) {
    return x.images.map((i: any) => {
      const path = i?.imageUrl ?? i?.url ?? i?.image ?? "";
      if (!path) return "";
      if (path.startsWith("http")) return path;
      if (path.startsWith("/bachhoa")) return `http://localhost:8080${path}`;
      return `${BASE_URL}/images/${path}`;
    }).filter(Boolean);
  }

  if (x.imageUrl) {
    const path = x.imageUrl;
    if (path.startsWith("http")) return [path];
    if (path.startsWith("/bachhoa")) return [`http://localhost:8080${path}`];
    if (path.startsWith("images/")) return [`${BASE_URL}/${path}`];
    return [`${BASE_URL}/images/${path}`];
  }

  return [];
}


// Chuẩn hóa dữ liệu trả về từ BE sang dạng Product của FE
function toProduct(x: any): Product {
  return {
    productId: Number(x.productId ?? x.id ?? 0),
    sku: x.sku ?? "",
    name: x.name ?? "",
    description: x.description ?? "",
    basePrice: Number(x.basePrice ?? x.price ?? 0),
    categoryName: x.categoryName ?? "",
    supplierName: x.supplierName ?? "",
    imageUrls: normalizeImages(x),
    variantNames: Array.isArray(x.variants)
      ? x.variants.map((v: any) => v.name ?? v.variantName ?? "")
      : [],
  };
}

// Gọi API sản phẩm
export const productApi = {
  // Lấy danh sách sản phẩm (có thể lọc theo danh mục hoặc tìm kiếm)
  async list(q: ProductListQuery = {}): Promise<Product[]> {
    const params: Record<string, any> = {};
    if (q.take != null) params.limit = q.take;
    if (q.offset != null) params.offset = q.offset;
    if (q.q) params.q = q.q;
    if (q.categoryId) params.categoryId = q.categoryId;

    const res = await axiosClient.get("/products", { params });
    const data = res.data?.data ?? res.data;
    const arr = Array.isArray(data) ? data : [];
    return arr.map(toProduct);
  },

  // Lấy chi tiết sản phẩm theo ID
  async getById(id: number): Promise<Product> {
    const res = await axiosClient.get(`/products/${id}`);
    const data = res.data?.data ?? res.data;
    return toProduct(data);
  },

  // Lấy danh sách sản phẩm liên quan
  async related(id: number, limit = 8): Promise<Product[]> {
    const res = await axiosClient.get(`/products/${id}/related`, {
      params: { limit },
    });
    const data = res.data?.data ?? res.data;
    const arr = Array.isArray(data) ? data : [];
    return arr.map(toProduct);
  },
};

export default productApi;
