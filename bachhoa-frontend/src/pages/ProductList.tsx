import React, { useEffect, useState } from "react";
import { useAppDispatch, useAppSelector } from "../app/hooks";
import { fetchProducts } from "../features/products/productSlice";
import ProductCard from "../components/ProductCard";
import CategoryFilter from "../components/CategoryFilter";
import { categoryApi } from "../api/categoryApi"; 

export default function ProductList() {
  const dispatch = useAppDispatch();
  const { items, loading, error } = useAppSelector((s) => s.products);

  // Danh sách danh mục lấy từ backend
  const [categories, setCategories] = useState<{ categoryId: number; name: string }[]>([]);

  // categoryId đang chọn
  const [filter, setFilter] = useState<string>("");

  // Từ khóa tìm kiếm
  const [q, setQ] = useState<string>("");

  // Gọi API sản phẩm mỗi khi q hoặc filter thay đổi
  useEffect(() => {
    dispatch(
      fetchProducts({
        take: 24,
        q: q || undefined,
        categoryId: filter || undefined,
      })
    );
  }, [dispatch, q, filter]);

  // Gọi API danh mục khi load trang
  useEffect(() => {
    categoryApi
      .list()
      .then((res) => {
        if (Array.isArray(res)) setCategories(res);
        else setCategories([]);
      })
      .catch((err) => {
        console.error("Lỗi lấy danh mục:", err);
        setCategories([]);
      });
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

        {/* Bộ lọc danh mục */}
        <CategoryFilter
          categories={categories}
          onChange={(id) => setFilter(id)}
          active={filter}
        />
      </aside>

      {/* Main content */}
      <section className="col-span-4 md:col-span-3">
        <h1 className="text-xl font-semibold mb-4">Sản phẩm</h1>

        {loading && <div>Đang tải…</div>}
        {!loading && error && <div className="text-red-600">Lỗi: {String(error)}</div>}
        {!loading && !error && items?.length === 0 && <div>Chưa có sản phẩm.</div>}

        {!loading && !error && items?.length > 0 && (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
            {items.map((p) => (
              <ProductCard key={p.productId} product={p} />
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
