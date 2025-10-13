import React, { useState } from "react";
import { useAppSelector, useAppDispatch } from "../app/hooks";
import { orderApi } from "../api/orderApi";
import { promotionApi } from "../api/promotionApi"; // 👈 thêm dòng này
import { clear } from "../features/cart/cartSlice";
import { useNavigate } from "react-router-dom";

export default function Checkout() {
  const items = useAppSelector((s) => s.cart.items);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const [paymentMethod, setPaymentMethod] = useState<string>("cod");

  // 👇 Thêm các state liên quan đến mã giảm giá
  const [promoCode, setPromoCode] = useState<string>("");
  const [promotion, setPromotion] = useState<any>(null);
  const [discountPercent, setDiscountPercent] = useState<number>(0);
  const [checkingPromo, setCheckingPromo] = useState<boolean>(false);

  // 🧮 Tính tổng tiền
  const subtotal = items.reduce((acc, i) => acc + i.price * i.qty, 0);
  let discountAmount = 0;

  if (promotion) {
    if (promotion.discountType === "PERCENT") {
      discountAmount = (subtotal * promotion.discountValue) / 100;
    } else if (promotion.discountType === "AMOUNT") {
      discountAmount = promotion.discountValue;
    }

    // Không cho giảm quá subtotal
    if (discountAmount > subtotal) discountAmount = subtotal;
  }

  const total = subtotal - discountAmount;
  

  // ⚙️ Hàm áp dụng mã giảm giá
  const applyPromotion = async () => {
    if (!promoCode) {
      alert("⚠️ Vui lòng nhập mã khuyến mãi!");
      return;
    }
    try {
      setCheckingPromo(true);
      const res = await promotionApi.checkCode(promoCode);
      const promo = res.data;

      // ⚠️ Sửa đoạn này:
      if (!promo || promo.active === false) {
        alert("❌ Mã không hợp lệ hoặc đã hết hạn!");
        return;
      }

      // ⚠️ Ở đây dùng discountValue thay vì discountPercent
      setPromotion(promo);
      setDiscountPercent(promo.discountValue || 0);

      alert(`✅ Áp dụng mã ${promo.code}: giảm ${promo.discountValue}%`);
    } catch (error) {
      console.error(error);
      alert("❌ Mã khuyến mãi không tồn tại!");
    } finally {
      setCheckingPromo(false);
    }
  };

  // 🧾 Tạo đơn hàng
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
        promotionCode: promoCode || null, // 👈 gửi mã giảm giá kèm theo
        totalPrice: total, // 👈 tổng sau khi giảm
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
        <p className="text-gray-600 text-center">Giỏ hàng của bạn đang trống.</p>
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

          {/* Mã khuyến mãi */}
          <div className="mb-4">
            <label className="block mb-2 font-medium text-gray-700">
              Mã khuyến mãi:
            </label>
            <div className="flex gap-2">
              <input
                type="text"
                value={promoCode}
                onChange={(e) => setPromoCode(e.target.value)}
                className="border rounded px-3 py-2 flex-1"
                placeholder="Nhập mã khuyến mãi..."
              />
              <button
                onClick={applyPromotion}
                disabled={checkingPromo}
                className={`${
                  checkingPromo ? "bg-gray-400" : "bg-green-600 hover:bg-green-700"
                } text-white px-4 py-2 rounded transition`}
              >
                {checkingPromo ? "Đang kiểm tra..." : "Áp dụng"}
              </button>
            </div>

            {promotion && (
              <p className="text-green-600 mt-2">
                ✅ Mã {promotion.code} áp dụng thành công! Giảm{" "}
                {promotion.discountPercent}%.
              </p>
            )}
          </div>

          {/* Tổng cộng */}
          <div className="flex justify-between text-lg mb-2">
            <span>Tạm tính:</span>
            <span>{subtotal.toLocaleString()}₫</span>
          </div>
          {discountPercent > 0 && (
            <div className="flex justify-between text-green-600 mb-2">
              <span>
                Giảm giá (
                {promotion?.discountType === "PERCENT"
                  ? `${promotion.discountValue}%`
                  : `${promotion.discountValue.toLocaleString()}₫`}
                ):
              </span>
              <span>-{discountAmount.toLocaleString()}₫</span>
            </div>
          )}
          <div className="flex justify-between font-semibold text-xl mb-6">
            <span>Tổng tiền:</span>
            <span className="text-red-600">{total.toLocaleString()}₫</span>
          </div>

          {/* Phương thức thanh toán */}
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
