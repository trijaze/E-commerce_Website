// 🛒 API giỏ hàng kết nối tới BE Tomcat
const BASE_URL = "http://localhost:8080/bachhoa/api/cart";

// 🔹 Lấy danh sách sản phẩm trong giỏ hàng
export async function getCartItems() {
  try {
    const res = await fetch(BASE_URL);
    if (!res.ok) throw new Error("Không thể tải giỏ hàng");
    return await res.json();
  } catch (err) {
    console.error("❌ Lỗi khi lấy giỏ hàng:", err);
    return [];
  }
}

// 🔹 Thêm sản phẩm vào giỏ
export async function addToCart(productId: number, quantity: number = 1) {
  try {
    const form = new URLSearchParams();
    form.append("productId", String(productId));
    form.append("quantity", String(quantity));

    const res = await fetch(BASE_URL, {
      method: "POST",
      body: form,
    });

    if (!res.ok) throw new Error("Không thể thêm sản phẩm vào giỏ");
    return await res.json();
  } catch (err) {
    console.error("❌ Lỗi thêm vào giỏ hàng:", err);
    return null;
  }
}

// 🔹 Cập nhật số lượng sản phẩm
export async function updateQuantity(id: number, quantity: number) {
  try {
    const res = await fetch(BASE_URL, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ id, quantity }),
    });

    if (!res.ok) throw new Error("Không thể cập nhật số lượng");
    return await res.json();
  } catch (err) {
    console.error("❌ Lỗi cập nhật số lượng:", err);
    return null;
  }
}

// 🔹 Xóa 1 sản phẩm khỏi giỏ hàng
export async function deleteCartItem(id: number) {
  try {
    const res = await fetch(`${BASE_URL}?id=${id}`, { method: "DELETE" });
    if (!res.ok) throw new Error("Không thể xóa sản phẩm");
    return await res.json();
  } catch (err) {
    console.error("❌ Lỗi xóa sản phẩm:", err);
    return null;
  }
}

// 🔹 Xóa toàn bộ giỏ hàng
export async function clearCart() {
  try {
    const res = await fetch(BASE_URL, { method: "DELETE" });
    if (!res.ok) throw new Error("Không thể xóa toàn bộ giỏ hàng");
    return await res.json();
  } catch (err) {
    console.error("❌ Lỗi clear giỏ hàng:", err);
    return null;
  }
}
