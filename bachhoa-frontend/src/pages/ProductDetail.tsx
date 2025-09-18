// src/pages/ProductDetail.tsx
import React, { useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../app/hooks';
import { fetchProductById } from '../features/products/productSlice';
import { add } from '../features/cart/cartSlice';
import { formatCurrency } from '../utils/format';

export default function ProductDetail() {
  const { id } = useParams();
  const dispatch = useAppDispatch();
  const product = useAppSelector((s) => s.products.items.find((p) => p.id === id));
  useEffect(() => {
    if (id) dispatch(fetchProductById(id));
  }, [id, dispatch]);

  if (!product) return <div>Product not found</div>;

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div className="md:col-span-2 bg-white p-4 rounded shadow">
        <img src={product.images?.[0] ?? '/placeholder.jpg'} alt={product.name} className="w-full h-80 object-cover rounded" />
        <h2 className="text-2xl font-semibold mt-3">{product.name}</h2>
        <p className="text-gray-600 mt-2">{product.description}</p>
      </div>
      <aside className="bg-white p-4 rounded shadow">
        <div className="text-xl font-bold">{formatCurrency(Number(product.price))}</div>
        <div className="text-sm text-gray-500 mt-2">Brand: {product.brand}</div>
        <div className="text-sm text-gray-500 mt-2">Stock: {product.stock}</div>
        <button
          className="mt-4 w-full bg-blue-600 text-white py-2 rounded"
          onClick={() => dispatch(add({ id: product.id, name: product.name, price: Number(product.price), qty: 1, image: product.images?.[0] ?? null }))}
        >
          Add to cart
        </button>
      </aside>
    </div>
  );
}
