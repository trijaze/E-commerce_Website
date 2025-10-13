import { useEffect, useState } from "react";
import {
  getCartItems,
  updateQuantity,
  deleteCartItem,
  clearCart,
} from "../api/cartApi";
import { formatCurrency } from "../utils/format";
import { toast } from "react-toastify";

type CartItem = {
  id: number;
  quantity: number;
  product: {
    name: string;
    basePrice: number;
    imageUrls?: string[];
  };
};

export default function CartPage() {
  const [items, setItems] = useState<CartItem[]>([]);
  const [loading, setLoading] = useState(true);

  const loadCart = async () => {
    setLoading(true);
    const data = await getCartItems(1); // userId=1
    setItems(data);
    setLoading(false);
  };

  useEffect(() => {
    loadCart();
  }, []);

  const handleQuantityChange = async (id: number, quantity: number) => {
    await updateQuantity(id, quantity);
    toast.info("🔄 Đã cập nhật số lượng");
    loadCart();
  };

  const handleDelete = async (id: number) => {
    await deleteCartItem(id);
    toast.warning("🗑 Đã xóa sản phẩm khỏi giỏ");
    loadCart();
  };

  const handleClear = async () => {
    await clearCart(1);
    toast.warning("🧹 Đã xóa toàn bộ giỏ hàng");
    loadCart();
  };

  const total = items.reduce(
    (sum, i) => sum + i.quantity * i.product.basePrice,
    0
  );

  if (loading) return <div className="p-6">⏳ Đang tải giỏ hàng...</div>;

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold mb-6 text-gray-800">🛒 Giỏ hàng của bạn</h1>

      {items.length === 0 ? (
        <p className="text-gray-500">Giỏ hàng trống.</p>
      ) : (
        <>
          <ul className="divide-y">
            {items.map((item) => (
              <li
                key={item.id}
                className="py-4 flex items-center justify-between gap-4"
              >
                <div className="flex items-center gap-4">
                  <img
                    src={`http://localhost:8080${item.product.imageUrls?.[0] ?? ""}`}
                    alt={item.product.name}
                    className="w-16 h-16 object-cover rounded-md border"
                  />
                  <div>
                    <p className="font-semibold text-gray-800">
                      {item.product.name}
                    </p>
                    <p className="text-gray-600">
                      {formatCurrency(item.product.basePrice)}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-3">
                  <button
                    onClick={() =>
                      handleQuantityChange(item.id, item.quantity - 1)
                    }
                    disabled={item.quantity <= 1}
                    className="px-2 py-1 bg-gray-200 rounded"
                  >
                    -
                  </button>
                  <span>{item.quantity}</span>
                  <button
                    onClick={() =>
                      handleQuantityChange(item.id, item.quantity + 1)
                    }
                    className="px-2 py-1 bg-gray-200 rounded"
                  >
                    +
                  </button>

                  <button
                    onClick={() => handleDelete(item.id)}
                    className="text-red-500 font-medium ml-3"
                  >
                    Xóa
                  </button>
                </div>
              </li>
            ))}
          </ul>

          <div className="mt-6 flex justify-between items-center">
            <button
              onClick={handleClear}
              className="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-300"
            >
              Xóa tất cả
            </button>
            <div className="text-xl font-bold text-green-700">
              Tổng: {formatCurrency(total)}
            </div>
          </div>
        </>
      )}
    </div>
  );
}
