// src/pages/ProductList.tsx
import React, { useEffect, useState } from 'react';
import { useAppDispatch, useAppSelector } from '../app/hooks';
import { fetchProducts } from '../features/products/productSlice';
import ProductCard from '../components/ProductCard';
import CategoryFilter from '../components/CategoryFilter';
// HOA: BE chưa có /api/categories, tạm không import/không gọi
// import { categoryApi } from '../api/categoryApi';

export default function ProductList() {
  const dispatch = useAppDispatch();
  const { items, loading, error } = useAppSelector((s) => s.products);

  const [categories, setCategories] = useState<{ id: string; name: string }[]>([]);
  const [filter, setFilter] = useState<string>(''); // HOA: sẽ dùng khi BE có categoryId
  const [q, setQ] = useState<string>('');          // tìm kiếm theo tên

  // HOA: gọi list sản phẩm thật từ BE (map take -> limit ở productApi)
  useEffect(() => {
    // Khi BE có filter theo categoryId, bật categoryId: filter || undefined
    dispatch(fetchProducts({ take: 24, q: q || undefined /*, categoryId: filter || undefined*/ }));
  }, [dispatch, q /*, filter*/]);

  // HOA: tạm thời không gọi /api/categories vì BE chưa có
  useEffect(() => {
    setCategories([]);
    // categoryApi.list().then(setCategories).catch(() => setCategories([]));
  }, []);

  return (
    <div className="grid grid-cols-4 gap-4">
      {/* Sidebar */}
      <aside className="col-span-4 md:col-span-1 space-y-3">
        {/* Tìm kiếm */}
        <div className="p-3 border rounded-xl">
          <div className="text-sm font-semibold mb-2">Tìm kiếm</div>
          <input
            value={q}
            onChange={(e) => setQ(e.target.value)}
            placeholder="Nhập tên sản phẩm…"
            className="w-full border rounded-lg px-3 py-2 text-sm"
          />
        </div>

        {/* Lọc danh mục (hiện tạm rỗng) */}
        <CategoryFilter categories={categories} onChange={(id) => setFilter(id)} />
      </aside>

      {/* Main */}
      <section className="col-span-4 md:col-span-3">
        <h1 className="text-xl font-semibold mb-4">Sản phẩm</h1>

        {loading && <div>Đang tải…</div>}
        {!loading && error && <div className="text-red-600">Lỗi: {String(error)}</div>}
        {!loading && !error && (!items || items.length === 0) && <div>Chưa có sản phẩm.</div>}

        {!loading && !error && items?.length > 0 && (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
            {items.map((p) => (
              <ProductCard key={p.id} product={p} />
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
