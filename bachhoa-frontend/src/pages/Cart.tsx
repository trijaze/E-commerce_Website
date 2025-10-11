// src/pages/Cart.tsx
import { useAppSelector, useAppDispatch } from '../app/hooks';
import { remove, updateQty, clear } from '../features/cart/cartSlice';
import { Link, useNavigate } from 'react-router-dom';
import { formatCurrency } from '../utils/format';

export default function Cart() {
  // const items = useAppSelector((s) => s.cart.items);
  const items = [
    {
      id: 1,
      name: 'Nho Mỹ không hạt',
      price: 120000,
      qty: 1,
      image: '/grape.jpg',
    },
    {
      id: 2,
      name: 'Thịt bò Úc 500g',
      price: 150000,
      qty: 2,
      image: '/beef.jpg',
    },
  ];

  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const total = items.reduce((a, b) => a + b.price * b.qty, 0);

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6 text-gray-800">Giỏ hàng</h1>

      {items.length === 0 ? (
        <div className="bg-yellow-50 p-6 rounded-lg text-center">
          <p className="text-lg text-gray-600 mb-4">Giỏ hàng đang trống.</p>
          <Link
            to="/products"
            className="inline-block bg-green-600 text-white px-6 py-3 rounded-lg shadow hover:bg-green-700 transition"
          >
            Tiếp tục mua sắm
          </Link>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Cart items */}
          <div className="lg:col-span-2 space-y-4">
            {items.map((it) => (
              <div
                key={it.id}
                className="bg-white p-4 rounded-lg shadow flex items-center justify-between hover:shadow-md transition"
              >
                <div className="flex items-center gap-4">
                  <img
                    src={it.image ?? '/placeholder.jpg'}
                    alt={it.name}
                    className="w-24 h-24 object-cover rounded-lg border"
                  />
                  <div>
                    <div className="font-semibold text-lg text-gray-800">{it.name}</div>
                    <div className="text-sm text-gray-500">{formatCurrency(it.price)}</div>
                  </div>
                </div>

                <div className="flex items-center gap-4">
                  <input
                    type="number"
                    min={1}
                    value={it.qty}
                    onChange={(e) =>
                      dispatch(updateQty({ id: it.id.toString(), qty: Number(e.target.value) }))
                    }
                    className="w-16 border rounded-lg px-2 py-1 text-center focus:ring focus:ring-blue-300"
                  />
                  <button
                    onClick={() => dispatch(remove(it.id.toString()))}
                    className="text-red-500 hover:text-red-700 font-medium"
                  >
                    Xóa
                  </button>
                </div>
              </div>
            ))}

            <button
              onClick={() => dispatch(clear())}
              className="mt-6 w-full bg-red-600 text-white py-3 rounded-lg font-medium shadow hover:bg-red-700 transition"
            >
              Xóa toàn bộ giỏ hàng
            </button>
          </div>

          {/* Summary */}
          <aside className="bg-white p-6 rounded-lg shadow-lg h-fit sticky top-4">
            <h2 className="text-xl font-semibold mb-4">Đơn hàng</h2>
            <div className="flex justify-between text-gray-700 mb-2">
              <span>Tạm tính:</span>
              <span>{formatCurrency(total)}</span>
            </div>
            <div className="flex justify-between text-gray-700 mb-4">
              <span>Phí vận chuyển:</span>
              <span>Miễn phí</span>
            </div>
            <div className="border-t pt-4 flex justify-between font-bold text-lg text-gray-800">
              <span>Tổng cộng:</span>
              <span>{formatCurrency(total)} VNĐ</span>
            </div>
            <button
              onClick={() => navigate('/checkout')}
              className="mt-6 w-full bg-green-600 text-white py-3 rounded-lg font-medium shadow hover:bg-green-700 transition"
            >
              Thanh toán
            </button>
          </aside>
        </div>
      )}
    </div>
  );
}
