// src/components/RelatedProducts.tsx
import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import axiosClient from '@/api/axiosClient';

type RelatedProduct = {
  id: number;
  name: string;
  images?: { imageUrl: string; isMain?: boolean }[];
  minPrice?: number;
  basePrice?: number;
};

type Props = {
  /** ID sản phẩm hiện tại để lấy danh sách liên quan */
  productId?: number; // cho phép optional, sẽ fallback sang useParams
};

const formatPrice = (v: number) =>
  (v ?? 0).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });

export default function RelatedProducts({ productId }: Props) {
  const params = useParams<{ id: string }>();
  const id = productId ?? Number(params.id ?? 0);

  const [items, setItems] = useState<RelatedProduct[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;
    let on = true;
    setLoading(true);
    axiosClient
      .get<RelatedProduct[]>(`/products/${id}/related`)
      .then((res) => {
        if (!on) return;
        setItems(res.data || []);
      })
      .catch((e: unknown) => setError((e as Error).message || 'Tải thất bại'))
      .finally(() => on && setLoading(false));
    return () => {
      on = false;
    };
  }, [id]);

  if (loading) return <div className="text-sm text-gray-500">Đang tải sản phẩm liên quan…</div>;
  if (error) return <div className="text-sm text-rose-600">Lỗi: {error}</div>;
  if (!items.length) return null;

  return (
    <section>
      <h2 className="text-lg font-semibold mb-3">Sản phẩm liên quan</h2>
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
        {items.map((p) => {
          const main =
            p.images?.find((i) => i.isMain)?.imageUrl || p.images?.[0]?.imageUrl || '';
          const price = p.minPrice ?? p.basePrice ?? 0;
          return (
            <Link
              key={p.id}
              to={`/products/${p.id}`}
              className="border rounded-2xl overflow-hidden hover:shadow transition bg-white"
              title={p.name}
            >
              <div className="aspect-square bg-gray-50 flex items-center justify-center">
                {main ? (
                  // eslint-disable-next-line jsx-a11y/alt-text
                  <img src={main} className="object-contain max-h-full max-w-full" />
                ) : (
                  <span className="text-xs text-gray-400">No image</span>
                )}
              </div>
              <div className="p-3">
                <div className="text-sm line-clamp-2 mb-1">{p.name}</div>
                <div className="font-semibold text-rose-600 text-sm">{formatPrice(price)}</div>
              </div>
            </Link>
          );
        })}
      </div>
    </section>
  );
}
