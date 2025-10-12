// src/api/productDetailApi.ts
import axiosClient from "./axiosClient";

<<<<<<< HEAD
// === Types KHỚP 100% BE DTO ===
export type ImageDTO = {
  imageId: number;
  imageUrl: string;
  isMain: boolean;
  variantId: number | null;
};

export type VariantDTO = {
  variantId: number;
  sku?: string | null;
  name?: string | null;   // BE: variantName
  price?: number | null;
};
=======
/** Kiểu dữ liệu trả về từ BE cho trang chi tiết */
export type ProductImage = { imageId?: number; imageUrl: string; isMain?: boolean };
export type ProductVariant = { variantId?: number; attributes: Record<string, string>; price: number };
>>>>>>> 5fdfb7099475079ea94830212a58e14902a70441

export type ProductDetail = {
  productId: number;
  sku?: string | null;
  name: string;
  description?: string | null;
  basePrice?: number | null;
  categoryId?: number | null;
  categoryName?: string | null;
  supplierId?: number | null;
  supplierName?: string | null;
  images: ImageDTO[];
  variants: VariantDTO[];
};

<<<<<<< HEAD
export type RelatedItem = {
  productId: number;
  name: string;
  basePrice?: number | null;
  imageUrl?: string | null;
};

// BE bọc { data: ... } nên cần unwrap
function unwrap<T>(res: any): T {
  const d = res?.data;
  return (d && typeof d === "object" && "data" in d ? d.data : d) as T;
}

export async function getProductDetail(id: number): Promise<ProductDetail> {
  const res = await axiosClient.get(`/products/${id}`);
  return unwrap<ProductDetail>(res);
}

export async function getRelated(id: number, limit = 8): Promise<RelatedItem[]> {
  const res = await axiosClient.get(`/products/${id}/related`, { params: { limit } });
  return (unwrap<RelatedItem[]>(res) ?? []);
}
=======
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
>>>>>>> 5fdfb7099475079ea94830212a58e14902a70441
