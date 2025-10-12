// src/api/productDetailApi.ts
import axiosClient from './axiosClient';

/** Kiểu dữ liệu trả về từ BE cho trang chi tiết */
export type ProductImage = { imageId?: number; imageUrl: string; isMain?: boolean };
export type ProductVariant = { variantId?: number; attributes: Record<string, string>; price: number };

export type ProductDetail = {
  productId: number;
  name: string;
  description?: string;
  basePrice?: number;
  minPrice?: number;
  images: ProductImage[];
  variants: ProductVariant[];
};

type RawResponse<T> = { data: T } | T;

// Chuẩn hóa đọc data cho BE trả {data:{...}} hoặc {data:...}
function unwrap<T>(res: any): T {
  const d = res?.data;
  if (d && typeof d === 'object' && 'data' in d) return d.data as T;
  return (d ?? res) as T;
}

export async function getProductDetail(id: number): Promise<ProductDetail> {
  const res: RawResponse<ProductDetail> = await axiosClient.get(`/products/${id}`);
  return unwrap<ProductDetail>(res);
}

// Linh hoạt cho 2 kiểu id: {id} hoặc {productId}
export type RelatedItem = {
  id?: number;
  productId?: number;
  name: string;
  imageUrl?: string;
  minPrice?: number;
  basePrice?: number;
  images?: ProductImage[];
};

export async function getRelated(id: number, limit = 8): Promise<RelatedItem[]> {
  const res: RawResponse<RelatedItem[]> = await axiosClient.get(`/products/${id}/related`, { params: { limit } });
  return unwrap<RelatedItem[]>(res) ?? [];
}

export default { getProductDetail, getRelated };
