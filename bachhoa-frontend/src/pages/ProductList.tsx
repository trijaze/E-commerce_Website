import React, { useEffect, useState } from "react";
import { useAppDispatch, useAppSelector } from "../app/hooks";
import { fetchProducts } from "../features/products/productSlice";
import ProductCard from "../components/ProductCard";
import CategoryFilter from "../components/CategoryFilter";
import { categoryApi } from "../api/categoryApi";
import { useSearchParams } from "react-router-dom";

export default function ProductList() {
  const dispatch = useAppDispatch();
  const { items, loading, error } = useAppSelector((s) => s.products);

  // Danh sách danh mục lấy từ backend
  const [categories, setCategories] = useState<{ categoryId: number; name: string }[]>([]);

    // Bộ lọc nâng cao
    const [filter, setFilter] = useState({
        q: "",
        categoryId: undefined as string | undefined,
        minPrice: undefined as number | undefined,
        maxPrice: undefined as number | undefined,
        sort: "",
    });

    //Lấy query "q" từ URL (Navbar điều hướng tới /products?q=...)
    const [searchParams] = useSearchParams();
    const keyword = searchParams.get("q") || "";

    //Khi người dùng tìm kiếm từ Navbar, cập nhật filter.q
    useEffect(() => {
        setFilter((prev) => ({ ...prev, q: keyword }));
    }, [keyword]);

    //Gọi API sản phẩm khi filter thay đổi
    useEffect(() => {
        dispatch(
            fetchProducts({
                take: 24,
                q: filter.q || undefined,
                categoryId: filter.categoryId || undefined,
                minPrice: filter.minPrice,
                maxPrice: filter.maxPrice,
                sort: filter.sort || undefined,
            })
        );
    }, [dispatch, filter]);

  // Lấy danh mục sản phẩm
    useEffect(() => {
        categoryApi
            .list()
            .then((res) => (Array.isArray(res) ? setCategories(res) : setCategories([])))
            .catch((err) => {
                console.error("Lỗi lấy danh mục:", err);
                setCategories([]);
            });
    }, []);

    // Handler thay đổi
    const handleCategoryChange = (id: string) => {
        setFilter((prev) => ({ ...prev, categoryId: id || undefined }));
    };

    const handlePriceFilter = (min?: number, max?: number) => {
        setFilter((prev) => ({ ...prev, minPrice: min, maxPrice: max }));
    };

    const handleSortChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setFilter((prev) => ({ ...prev, sort: e.target.value }));
    };

  return (
    <div className="grid grid-cols-4 gap-4">
      {/* Sidebar */}
      <aside className="col-span-4 md:col-span-1 space-y-3">
        {/* Bộ lọc danh mục */}
        <CategoryFilter
          categories={categories}
          onChange={handleCategoryChange}
          active={filter.categoryId ?? ""}
        />

          {/* Bộ lọc giá nhanh */}
          <div className="bg-white p-4 rounded-lg shadow">
              <h2 className="font-medium mb-2 text-sm">Khoảng giá</h2>
              <ul className="space-y-1 text-sm">
                  <li>
                      <button onClick={() => handlePriceFilter(0, 50000)}>Dưới 50k</button>
                  </li>
                  <li>
                      <button onClick={() => handlePriceFilter(50000, 100000)}>50k - 100k</button>
                  </li>
                  <li>
                      <button onClick={() => handlePriceFilter(100000, 300000)}>100k - 300k</button>
                  </li>
                  <li>
                      <button onClick={() => handlePriceFilter(300000, undefined)}>Từ 300k trở lên</button>
                  </li>
              </ul>
          </div>

          {/*Bộ lọc sắp xếp */}
          <div className="bg-white p-4 rounded-lg shadow">
              <h2 className="font-medium mb-2 text-sm">Sắp xếp theo</h2>
              <select
                  value={filter.sort}
                  onChange={handleSortChange}
                  className="border p-1 w-full text-sm"
              >
                  <option value="">Mặc định</option>
                  <option value="price_asc">Giá ↑</option>
                  <option value="price_desc">Giá ↓</option>
                  <option value="name_az">Tên A–Z</option>
                  <option value="name_za">Tên Z–A</option>
              </select>
          </div>
      </aside>

      {/* Main content */}
      <section className="col-span-4 md:col-span-3">
        <h1 className="text-xl font-semibold mb-4">Sản phẩm</h1>

        {loading && <div>Đang tải…</div>}
        {!loading && error && <div className="text-red-600">Lỗi: {String(error)}</div>}
        {!loading && !error && items?.length === 0 && <div>Chưa có sản phẩm.</div>}

        {!loading && !error && items?.length > 0 && (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
            {items
              .filter((p) => (p.totalStock ?? 0) > 0) // Chỉ hiển thị sản phẩm còn hàng
              .map((p) => (
                <ProductCard key={p.productId} product={p} />
              ))}
          </div>
        )}
      </section>
    </div>
  );
}
