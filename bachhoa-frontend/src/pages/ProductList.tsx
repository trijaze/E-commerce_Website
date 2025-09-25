// src/pages/ProductList.tsx
import React, { useEffect, useState } from 'react';
import { useAppDispatch, useAppSelector } from '../app/hooks';
import { fetchProducts } from '../features/products/productSlice';
import ProductCard from '../components/ProductCard';
import CategoryFilter from '../components/CategoryFilter';
import { categoryApi } from '../api/categoryApi';

export default function ProductList() {
  const dispatch = useAppDispatch();

  const items = [
    {
      id: 1,
      name: 'Nho',
      price: 120000,
      qty: 1,
      image: '/grape.jpg',
    },
    {
      id: 2,
      name: 'Thịt bò',
      price: 150000,
      qty: 2,
      image: '/beef.jpg',
    },
  ];

  const {loading } = useAppSelector((s) => s.products);
  const [categories, setCategories] = useState<{ id: string; name: string }[]>([]);
  const [filter, setFilter] = useState<string>('');

  useEffect(() => {
    dispatch(fetchProducts({ take: 24, categoryId: filter || undefined }));
  }, [dispatch, filter]);

  useEffect(() => {
    categoryApi.list().then((data) => setCategories(data)).catch(() => setCategories([]));
  }, []);

  return (
    <div className="grid grid-cols-4 gap-4">
      <aside className="col-span-1">
        <CategoryFilter categories={categories} onChange={(id) => setFilter(id)} />
      </aside>
      <section className="col-span-3">
        <h1 className="text-xl font-semibold mb-4">Products</h1>
        {loading ? <div>Loading...</div> : (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
            {items.map((p) => <ProductCard key={p.id} product={p} />)}
          </div>
        )}
      </section>
    </div>
  );
}
