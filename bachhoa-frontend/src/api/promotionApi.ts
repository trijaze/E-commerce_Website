// src/api/promotionApi.ts
import axios from "axios";

// === Instance chung ===
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/bachhoa",
  headers: { "Content-Type": "application/json" },
});

// === Kiểu dữ liệu tương ứng với backend PromotionDTO ===
export type PromotionDTO = {
  id: number;
  code: string;
  title: string;
  description?: string;
  discountType: "PERCENT" | "AMOUNT";
  discountValue: number;
  minOrderAmount?: number | null;
  active: boolean;
  startAt?: string | null;
  endAt?: string | null;
};

// === API Methods ===
export async function getAllPromotions(): Promise<PromotionDTO[]> {
  const { data } = await api.get("/api/promotions");
  return data.data as PromotionDTO[];
}

export async function getPromotionByCode(code: string): Promise<PromotionDTO | null> {
  const { data } = await api.get("/api/promotions", { params: { code } });
  const result = data.data;
  if (Array.isArray(result)) return result[0] ?? null;
  return result ?? null;
}

export async function createPromotion(promo: Partial<PromotionDTO>) {
  const { data } = await api.post("/api/promotions", promo);
  return data.data;
}

export async function updatePromotion(id: number, promo: Partial<PromotionDTO>) {
  const { data } = await api.put(`/api/promotions/${id}`, promo);
  return data.data;
}

export async function deletePromotion(id: number) {
  const { data } = await api.delete(`/api/promotions/${id}`);
  return data.data;
}
