import React, { useState } from "react";
import { useAppSelector, useAppDispatch } from "../app/hooks";
import { orderApi } from "../api/orderApi";
import { getPromotionByCode } from "@/api/promotionApi";
import { clear } from "../features/cart/cartSlice";
import { useNavigate, useLocation } from "react-router-dom";

export default function Checkout() {
  const location = useLocation();
  const singleProduct = location.state?.product; // 👈 nhận sản phẩm từ nút MUA (nếu có)

  const cartItems = useAppSelector((s) => s.cart.items);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  // ✅ Nếu có sản phẩm truyền qua => chỉ mua 1 sản phẩm đó, ngược lại dùng cart
  const items = singleProduct
    ? [
        {
          id: singleProduct.productId,
          name: singleProduct.name,
          qty: 1,
          price: singleProduct.basePrice,
        },
      ]
    : cartItems;

  const [paymentMethod, setPaymentMethod] = useState<string>("cod");

  // 👇 Thêm các state liên quan đến mã giảm giá
  const [promoCode, setPromoCode] = useState<string>("");
  const [promotion, setPromotion] = useState<any>(null);
  const [checkingPromo, setCheckingPromo] = useState<boolean>(false);


  // 🧮 Tính tổng tiền
  const subtotal = items.reduce((acc, i) => acc + i.price * i.qty, 0);
  
  const discountAmount = (() => {
    if (!promotion) return 0;
    // Kiểm tra active và minOrderAmount
    if (!promotion.active) return 0;
    if (promotion.minOrderAmount && subtotal < promotion.minOrderAmount) return 0;

    if (promotion.discountType === "PERCENT") {
      return Math.min((subtotal * promotion.discountValue) / 100, subtotal);
    } else if (promotion.discountType === "AMOUNT") {
      return Math.min(promotion.discountValue, subtotal);
    }
    return 0;
  })();

  const total = subtotal - discountAmount;
  

  // ⚙️ Hàm áp dụng mã giảm giá

  const applyPromotion = async () => {
    if (!promoCode.trim()) {
      alert("⚠️ Vui lòng nhập mã khuyến mãi!");
      return;
    }

    try {
      setCheckingPromo(true);
      const promo = await getPromotionByCode(promoCode.trim());
      console.log("🧩 Promotion API:", promo);
      console.log("📂 promo.active =", promo?.active);


      if (!promo || !promo.active) {
        alert("❌ Mã không hợp lệ hoặc đã hết hạn!");
        setPromotion(null);
        return;
      }

      if (promo.minOrderAmount && subtotal < promo.minOrderAmount) {
        alert(`❌ Mã này chỉ áp dụng cho đơn từ ${promo.minOrderAmount.toLocaleString()}₫ trở lên!`);
        setPromotion(null);
        return;
      }

      setPromotion(promo);
      alert(
        `✅ Mã ${promo.code} áp dụng thành công: ${
          promo.discountType === "PERCENT"
            ? `${promo.discountValue}%`
            : `${promo.discountValue.toLocaleString()}₫`
        }`
      );
    } catch (err) {
      console.error(err);
      alert("❌ Mã khuyến mãi không tồn tại hoặc lỗi server!");
      setPromotion(null);
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
        userId: 1, // tạm hardcode user
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

      // Nếu là mua qua cart thì clear, còn mua ngay thì không cần
      if (!singleProduct) dispatch(clear());

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

            {promotion && discountAmount > 0 && (
              <p className="text-green-600 mt-2">
                ✅ Mã {promotion.code} áp dụng thành công! Giảm{" "}
                {promotion.discountType === "PERCENT"
                  ? `${promotion.discountValue}%`
                  : `${promotion.discountValue.toLocaleString()}₫`}
                .
              </p>
            )}

          </div>

          
          {/* Tổng cộng */}
          <div className="flex justify-between text-lg mb-2">
            <span>Tạm tính:</span>
            <span>{subtotal.toLocaleString()}₫</span>
          </div>

          {discountAmount > 0 && promotion && (
            <div className="flex justify-between text-green-600 mb-2">
              <span>
                Giảm giá (
                {promotion.discountType === "PERCENT"
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
