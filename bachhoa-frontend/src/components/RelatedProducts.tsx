// src/components/RelatedProducts.tsx
import { useEffect, useState } from 'react';
import ProductCard from './ProductCard';
import { productApi } from '../api/productApi';
import type { Product } from '../features/products/productTypes';

export default function RelatedProducts({
  productId,
  limit = 8,
}: { productId: string; limit?: number }) {
  const [items, setItems] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let alive = true;
    setLoading(true);
    productApi.related(productId, limit)
      .then((arr) => { if (alive) setItems(arr); })
      .finally(() => { if (alive) setLoading(false); });
    return () => { alive = false; };
  }, [productId, limit]);

  return (
    <section className="mt-10">
      <h2 className="text-xl font-semibold mb-4">Sản phẩm liên quan</h2>
      {loading ? (
        <div>Đang tải…</div>
      ) : items.length === 0 ? (
        <div>Chưa có sản phẩm liên quan.</div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4">
          {items.map((p) => <ProductCard key={p.id} product={p} />)}
        </div>
      )}
    </section>
  );
}
