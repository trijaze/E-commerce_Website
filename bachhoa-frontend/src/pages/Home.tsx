// \src\pages\Home.tsx
import { useEffect } from 'react';
import { useAppDispatch, useAppSelector } from '../app/hooks';
import { fetchProducts } from '../features/products/productSlice';
import Hero from '../components/Hero';
import ProductCard from '../components/ProductCard';

export default function Home() {
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
  const { loading } = useAppSelector((s) => s.products);
//  const {items, loading } = useAppSelector((s) => s.products);


  useEffect(() => {
    dispatch(fetchProducts({ take: 12 }));
  }, [dispatch]);

  return (
    
    <div className="space-y-12">
      {/* Hero Banner */}
      <Hero />
      {/* Danh sách sản phẩm */}
      <section className="container mx-auto px-4 py-4">
        <h2 className="text-3xl font-bold mb-6 text-center text-gray-800">
          Các sản phẩm nổi bật
        </h2>

        {loading ? (
          <div className="text-center text-lg font-medium text-gray-500">
            Đang tải sản phẩm...
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            {items.map((p) => (
              <ProductCard key={p.id} product={p} />
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
