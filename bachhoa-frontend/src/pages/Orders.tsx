// src/pages/Orders.tsx
import React, { useEffect, useState } from 'react';

interface Order {
  id: number;
  status: string;
  totalPrice: number;
  createdAt: string;
  items: { name: string; qty: number; price: number }[];
}

export default function Orders() {
  // Demo orders (có thể thay bằng orderApi.list())
  const [orders, setOrders] = useState<Order[]>([]);

  useEffect(() => {
    const demoOrders: Order[] = [
      {
        id: 1001,
        status: 'Đang xử lý',
        totalPrice: 270000,
        createdAt: '2025-09-05',
        items: [
          { name: 'Nho', qty: 1, price: 120000 },
          { name: 'Thịt', qty: 1, price: 150000 },
        ],
      },
      {
        id: 1002,
        status: 'Hoàn tất',
        totalPrice: 450000,
        createdAt: '2025-08-30',
        items: [
          { name: 'Táo', qty: 2, price: 200000 },
          { name: 'Rau cải', qty: 1, price: 50000 },
        ],
      },
    ];

    setOrders(demoOrders);
  }, []);

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6 text-gray-800">Đơn hàng của tôi</h1>

      {orders.length === 0 ? (
        <div className="bg-yellow-50 p-6 rounded-lg text-center text-gray-600">
          Bạn chưa có đơn hàng nào.
        </div>
      ) : (
        <div className="space-y-6">
          {orders.map((o) => (
            <div
              key={o.id}
              className="bg-white p-6 rounded-lg shadow hover:shadow-md transition"
            >
              {/* Header */}
              <div className="flex justify-between items-center border-b pb-3 mb-3">
                <div>
                  <div className="font-semibold text-lg text-gray-800">
                    Đơn hàng #{o.id}
                  </div>
                  <div className="text-sm text-gray-500">Ngày đặt: {o.createdAt}</div>
                </div>
                <div
                  className={`px-3 py-1 rounded-full text-sm font-medium ${
                    o.status === 'Hoàn tất'
                      ? 'bg-green-100 text-green-700'
                      : 'bg-yellow-100 text-yellow-700'
                  }`}
                >
                  {o.status}
                </div>
              </div>

              {/* Items */}
              <div className="space-y-2">
                {o.items.map((it, idx) => (
                  <div
                    key={idx}
                    className="flex justify-between text-gray-700 text-sm"
                  >
                    <span>
                      {it.name} × {it.qty}
                    </span>
                    <span>{it.price.toLocaleString()}₫</span>
                  </div>
                ))}
              </div>

              {/* Footer */}
              <div className="mt-4 flex justify-between items-center border-t pt-3">
                <span className="font-semibold text-gray-800">Tổng cộng:</span>
                <span className="text-lg font-bold text-green-600">
                  {o.totalPrice.toLocaleString()}₫
                </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
