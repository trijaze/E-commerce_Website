// src/api/productDetailApi.ts
import axiosClient from './axiosClient';

/** Kiểu dữ liệu trả về từ BE cho trang chi tiết */
export type ProductImage = { imageId?: number; imageUrl: string; isMain?: boolean };
export type ProductVariant = { variantId?: number; attributes: Record<string, string>; price: number };
export type RelatedItem = { productId: number; name: string; imageUrl?: string; minPrice: number };

export type ProductDetail = {
  productId: number;
  name: string;
  description?: string;
  basePrice?: number;
  minPrice?: number;
  images: ProductImage[];
  variants: ProductVariant[];
};

function pickData<T>(res: any): T {
  return (res?.data?.data ?? res?.data) as T;
}

export const getProductDetail = async (id: number): Promise<ProductDetail> => {
  const res = await axiosClient.get(`/products/${id}`);
  return pickData<ProductDetail>(res);
};

export const getRelated = async (id: number, limit = 8): Promise<RelatedItem[]> => {
  const res = await axiosClient.get(`/products/${id}/related`, { params: { limit } });
  return pickData<RelatedItem[]>(res) ?? [];
};

export default { getProductDetail, getRelated };
