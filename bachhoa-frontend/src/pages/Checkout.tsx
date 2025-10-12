import React, { useState } from "react";
import { useAppSelector, useAppDispatch } from "../app/hooks";
import { orderApi } from "../api/orderApi";
import { clear } from "../features/cart/cartSlice";
import { useNavigate } from "react-router-dom";

export default function Checkout() {
  const items = useAppSelector((s) => s.cart.items);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const [paymentMethod, setPaymentMethod] = useState<string>("cod");

  const createOrder = async () => {
    if (items.length === 0) {
      alert("❌ Giỏ hàng trống!");
      return;
    }

    try {
      const payload = {
        userId: 1, // giả định user tạm
        paymentMethod,
        items: items.map((i) => ({
          productId: i.id,
          quantity: i.qty,
          price: i.price,
        })),
      };

      console.log("🚀 Gửi đơn hàng:", payload);

      const response = await orderApi.create(payload);
      console.log("✅ Đặt hàng thành công:", response.data);

      alert("🎉 Đặt hàng thành công!");
      dispatch(clear());
      navigate("/orders");
    } catch (error) {
      console.error("❌ Lỗi khi tạo đơn hàng:", error);
      alert("Không thể đặt hàng, kiểm tra console!");
    }
  };

  return (
    <div className="max-w-xl mx-auto mt-10 bg-white p-6 rounded-xl shadow-md">
      <h1 className="text-2xl font-bold mb-4 text-indigo-700 text-center">
        🧾 Thanh toán đơn hàng
      </h1>

      {items.length === 0 ? (
        <p className="text-gray-600 text-center">
          Giỏ hàng của bạn đang trống.
        </p>
      ) : (
        <>
          {/* Danh sách sản phẩm */}
          <ul className="divide-y divide-gray-100 mb-4">
            {items.map((item) => (
              <li
                key={item.id}
                className="flex justify-between py-2 text-gray-700"
              >
                <span>
                  {item.name} × {item.qty}
                </span>
                <span className="font-semibold">
                  {(item.price * item.qty).toLocaleString()}₫
                </span>
              </li>
            ))}
          </ul>

          {/* Tổng cộng */}
          <div className="flex justify-between font-semibold text-lg mb-6">
            <span>Tổng tiền:</span>
            <span className="text-red-600">
              {items
                .reduce((acc, i) => acc + i.price * i.qty, 0)
                .toLocaleString()}₫
            </span>
          </div>

          {/* Chọn phương thức thanh toán */}
          <div className="mb-4">
            <label className="block mb-2 font-medium text-gray-700">
              Phương thức thanh toán:
            </label>
            <select
              value={paymentMethod}
              onChange={(e) => setPaymentMethod(e.target.value)}
              className="border rounded px-3 py-2 w-full"
            >
              <option value="cod">💵 Thanh toán khi nhận hàng (COD)</option>
              <option value="credit_card">💳 Thẻ tín dụng</option>
              <option value="momo">📱 Ví MoMo</option>
            </select>
          </div>

          {/* Nút đặt hàng */}
          <button
            onClick={createOrder}
            className="bg-indigo-600 text-white w-full py-3 rounded-lg font-semibold hover:bg-indigo-700 transition"
          >
            Xác nhận đặt hàng
          </button>
        </>
      )}
    </div>
  );
}
