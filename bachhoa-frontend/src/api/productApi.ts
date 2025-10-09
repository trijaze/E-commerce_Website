// src/api/productApi.ts
import axiosClient from './axiosClient';
import type { Product } from '../features/products/productTypes';

export type ProductListQuery = {
  take?: number;     // FE -> map sang limit của BE
  offset?: number;   // offset
  q?: string;        // tìm theo tên
  categoryId?: string;
};

function normalizeImages(x: any): string[] {
  if (Array.isArray(x.images)) {
    return x.images.map((i: any) => i?.imageUrl ?? i?.url ?? String(i)).filter(Boolean);
  }
  if (x.imageUrl) return [String(x.imageUrl)];
  return [];
}

function toProduct(x: any): Product {
  return {
    id: String(x.productId ?? x.id ?? ''),
    name: x.name ?? '',
    description: x.description ?? '',
    price: Number(x.minPrice ?? x.price ?? 0),
    stock: Number(x.stock ?? 0),
    brand: x.brand ?? '',
    images: normalizeImages(x),
    categoryId: x.categoryId ? String(x.categoryId) : undefined,
  };
}

export const productApi = {
  list(q: ProductListQuery = {}): Promise<Product[]> {
    const params: any = {};
    if (q.take != null) params.limit = q.take;
    if (q.offset != null) params.offset = q.offset;
    if (q.q) params.q = q.q;
    if (q.categoryId) params.categoryId = q.categoryId;

    return axiosClient.get('/products', { params }).then((r) => {
      const data = r.data?.data ?? r.data;
      const arr = Array.isArray(data) ? data : [];
      return arr.map(toProduct);
    });
  },

  getById(id: string): Promise<Product> {
    return axiosClient.get(`/products/${id}`).then((r) => {
      const data = r.data?.data ?? r.data;
      return toProduct(data);
    });
  },

  related(id: string, limit = 8): Promise<Product[]> {
    return axiosClient.get(`/products/${id}/related`, { params: { limit } }).then((r) => {
      const data = r.data?.data ?? r.data;
      const arr = Array.isArray(data) ? data : [];
      return arr.map(toProduct);
    });
  },
};

export default productApi;
