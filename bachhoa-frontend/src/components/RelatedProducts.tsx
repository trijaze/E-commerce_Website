// src/components/RelatedProducts.tsx
import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getRelated, type RelatedItem } from '@/api/productDetailApi';

type Props = { productId?: number };

export default function RelatedProducts({ productId }: Props) {
  const params = useParams<{ id: string }>();
  const pid = productId ?? Number(params.id ?? 0);

  const [items, setItems] = useState<RelatedItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let mounted = true;
    setLoading(true);
    getRelated(pid)
      .then((d) => {
        if (!mounted) return;
        setItems(d);
        setError(null);
      })
      .catch((e) => {
        console.error(e);
        if (!mounted) return;
        setError('Không tải được danh sách liên quan.');
      })
      .finally(() => mounted && setLoading(false));
    return () => { mounted = false; };
  }, [pid]);

  if (loading) return <div className="text-sm text-gray-500">Đang tải sản phẩm liên quan…</div>;
  if (error) return <div className="text-sm text-rose-600">{error}</div>;
  if (!items.length) return null;

  return (
    <section>
      <h2 className="text-lg font-semibold mb-3">Sản phẩm liên quan</h2>
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
        {items.map((p, idx) => {
          const id = p.id ?? p.productId!;
          const main =
            p.imageUrl || p.images?.find((i) => i.isMain)?.imageUrl || p.images?.[0]?.imageUrl || '';
          const price = p.minPrice ?? p.basePrice ?? 0;
          return (
            <Link
              key={`${id}-${idx}`}
              to={`/products/${id}`}
              className="border rounded-xl overflow-hidden hover:shadow"
            >
              <div className="aspect-square bg-white flex items-center justify-center">
                {main ? (
                  // eslint-disable-next-line @next/next/no-img-element
                  <img src={main} alt={p.name} className="max-w-full max-h-full object-contain" />
                ) : (
                  <div className="text-xs text-gray-400 p-4">Chưa có ảnh</div>
                )}
              </div>
              <div className="p-3">
                <div className="text-sm line-clamp-2 mb-1">{p.name}</div>
                <div className="font-semibold text-rose-600 text-sm">
                  {price.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })}
                </div>
              </div>
            </Link>
          );
        })}
      </div>
    </section>
  );
}
