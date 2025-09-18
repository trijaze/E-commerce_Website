// src/pages/Orders.tsx
import React, { useEffect, useState } from 'react';
import { orderApi } from '../api/orderApi';

export default function Orders() {
  const [orders, setOrders] = useState<any[]>([]);
  useEffect(() => {
    orderApi.list().then((d) => setOrders(d)).catch(() => setOrders([]));
  }, []);
  return (
    <div>
      <h1 className="text-2xl font-semibold mb-4">My Orders</h1>
      <div className="space-y-3">
        {orders.length === 0 && <div>No orders yet</div>}
        {orders.map((o) => (
          <div key={o.id} className="bg-white p-3 rounded shadow">
            <div className="flex justify-between">
              <div>Order #{o.id}</div>
              <div>{o.status}</div>
            </div>
            <div className="text-sm text-gray-500">Total: ${Number(o.totalPrice).toFixed(2)}</div>
          </div>
        ))}
      </div>
    </div>
  );
}
