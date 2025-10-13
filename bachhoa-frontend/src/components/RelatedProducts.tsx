// src/components/RelatedProducts.tsx
import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getRelated, type RelatedItem } from "@/api/productDetailApi";

type Props = { productId?: number };

// Chuẩn hóa URL ảnh từ BE (vd: "/images/xxx.jpg" -> http://localhost:8080/bachhoa/images/xxx.jpg)
const API_BASE =
  import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/bachhoa";
const resolveUrl = (u?: string | null) => {
  if (!u) return "/placeholder.png";
  if (/^https?:\/\//i.test(u)) return u;
  return `${API_BASE}${u.startsWith("/") ? "" : "/"}${u}`;
};

export default function RelatedProducts({ productId }: Props) {
  const params = useParams<{ id: string }>();
  const pid = productId ?? Number(params.id ?? 0);

  const [items, setItems] = useState<RelatedItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let mounted = true;
    setLoading(true);
    setError(null);
    setItems([]);

    getRelated(pid, 8)
      .then((d) => {
        if (!mounted) return;
        setItems(d ?? []);
      })
      .catch(() => {
        if (!mounted) return;
        setError("Không tải được danh sách liên quan.");
      })
      .finally(() => mounted && setLoading(false));

    return () => {
      mounted = false;
    };
  }, [pid]);

  // Nếu lỗi hoặc danh sách rỗng sau khi load xong thì ẩn block
  if (error || (!loading && items.length === 0)) return null;

  return (
    <section>
      <h2 className="text-lg font-semibold mb-3">Sản phẩm liên quan</h2>

      {loading ? (
        // Skeleton khi đang tải
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
          {Array.from({ length: 4 }).map((_, i) => (
            <div key={i} className="border rounded-xl p-3 animate-pulse">
              <div className="aspect-square rounded-xl bg-gray-100" />
              <div className="h-4 mt-2 bg-gray-100 rounded" />
              <div className="h-4 mt-2 w-1/2 bg-gray-100 rounded" />
            </div>
          ))}
        </div>
      ) : (
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
          {items.map((p, idx) => {
            const id = p.productId;
            const price = p.basePrice ?? 0;
            const img = resolveUrl(p.imageUrl ?? null);

            return (
              <Link
                key={`${id}-${idx}`}
                to={`/products/${id}`}
                className="border rounded-xl overflow-hidden hover:shadow"
              >
                <div className="aspect-square bg-white flex items-center justify-center">
                  <img
                    src={img}
                    alt={p.name}
                    className="max-w-full max-h-full object-contain"
                    loading="lazy"
                    onError={(e) => {
                      (e.currentTarget as HTMLImageElement).src =
                        "/placeholder.png";
                    }}
                  />
                </div>
                <div className="p-3">
                  <div className="text-sm line-clamp-2 mb-1">{p.name}</div>
                  <div className="font-semibold text-emerald-600 text-sm">
                    {price.toLocaleString("vi-VN", {
                      style: "currency",
                      currency: "VND",
                    })}
                  </div>
                </div>
              </Link>
            );
          })}
        </div>
      )}
    </section>
  );
}
