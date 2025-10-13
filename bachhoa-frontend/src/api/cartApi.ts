// 🛒 Kết nối API giỏ hàng (sử dụng session BE)
const BASE_URL = "http://localhost:8080/bachhoa/api/cart";

//  Lấy danh sách sản phẩm trong giỏ hàng (GET /api/cart)
export async function getCartItems() {
  try {
    const res = await fetch(BASE_URL, {
      method: "GET",
      credentials: "include", // GỬI COOKIE SESSION ĐỂ TOMCAT NHẬN userId
    });
    if (!res.ok) throw new Error(`Không thể tải giỏ hàng (${res.status})`);
    return await res.json();
  } catch (err) {
    console.error(" Lỗi khi lấy giỏ hàng:", err);
    return [];
  }
}

//  Thêm sản phẩm vào giỏ (POST /api/cart)
export async function addToCart(productId: number, quantity: number = 1) {
  try {
    const res = await fetch(BASE_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include", // GỬI COOKIE SESSION
      body: JSON.stringify({ productId, quantity }),
    });
    if (!res.ok) throw new Error(`Không thể thêm sản phẩm (${res.status})`);
    return await res.json();
  } catch (err) {
    console.error(" Lỗi khi thêm vào giỏ:", err);
    return { message: "Thêm thất bại" };
  }
}

//  Xóa 1 sản phẩm khỏi giỏ
export async function removeFromCart(productId: number) {
  try {
    const res = await fetch(BASE_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify({ productId }),
    });
    if (!res.ok) throw new Error(`Không thể xóa sản phẩm (${res.status})`);
    return await res.json();
  } catch (err) {
    console.error(" Lỗi khi xóa sản phẩm:", err);
    return { message: "Xóa thất bại" };
  }
}

//  Xóa toàn bộ giỏ hàng
export async function clearCart() {
  try {
    const res = await fetch(BASE_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify({ action: "clear" }),
    });
    if (!res.ok) throw new Error(`Không thể xóa giỏ hàng (${res.status})`);
    return await res.json();
  } catch (err) {
    console.error(" Lỗi khi xóa giỏ hàng:", err);
    return { message: "Xóa giỏ thất bại" };
  }
}
