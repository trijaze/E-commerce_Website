// src/api/productDetailApi.ts
import axiosClient from "./axiosClient";

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
