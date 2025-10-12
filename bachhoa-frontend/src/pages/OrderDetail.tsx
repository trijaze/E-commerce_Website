import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import orderApi from "../api/orderApi";

export default function OrderDetail() {
  const { id } = useParams<{ id: string }>();
  const [order, setOrder] = useState<any>(null);

  useEffect(() => {
    if (id) {
      orderApi.getById(Number(id)).then((res) => setOrder(res.data));
    }
  }, [id]);

  if (!order) return <p className="text-center mt-10 text-gray-600">Đang tải...</p>;

  return (
    <div className="max-w-4xl mx-auto mt-10 bg-white p-6 rounded-lg shadow">
      <h1 className="text-2xl font-bold mb-4 text-indigo-700">Chi tiết đơn hàng #{order.id}</h1>
      <p className="mb-2 text-gray-700">
        <strong>Trạng thái:</strong> {order.status}
      </p>
      <p className="mb-2 text-gray-700">
        <strong>Tổng tiền:</strong>{" "}
        <span className="text-red-600 font-semibold">
          {order.total.toLocaleString()}₫
        </span>
      </p>
      {order.paymentMethod && (
        <p className="mb-2 text-gray-700">
          <strong>Phương thức:</strong> {order.paymentMethod}
        </p>
      )}
      <h2 className="mt-4 mb-2 text-lg font-semibold text-gray-800">Sản phẩm:</h2>
      <ul className="divide-y divide-gray-200">
        {order.items?.map((item: any) => (
          <li key={item.id} className="py-2 flex justify-between">
            <span>SP #{item.productId} × {item.quantity}</span>
            <span>{(item.price * item.quantity).toLocaleString()}₫</span>
          </li>
        ))}
      </ul>

      <div className="mt-6 text-right">
        <Link to="/orders">
          <button className="bg-gray-100 text-gray-800 px-4 py-2 rounded hover:bg-gray-200">
            ← Quay lại danh sách
          </button>
        </Link>
      </div>
    </div>
  );
}
