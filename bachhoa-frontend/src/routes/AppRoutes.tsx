// src/routes/AppRoutes.tsx
import { Routes, Route } from 'react-router-dom';
import ProductList from '../pages/ProductList';
import ProductDetail from '../pages/ProductDetail';
import Cart from '../pages/Cart';
import Checkout from '../pages/Checkout';
import Profile from '../pages/Profile';
import Orders from '../pages/Orders';
import AdminDashboard from '../pages/admin/Dashboard';

// HOA: Trang "/" dùng ProductList để hiện data thật từ BE (tránh data mock ở Home)
export default function AppRoutes() {
  return (
    <Routes>
      {/* HOA: Trang chủ -> danh sách sản phẩm */}
      <Route path="/" element={<ProductList />} />

      {/* Giữ /products để đồng nhất với menu */}
      <Route path="/products" element={<ProductList />} />
      <Route path="/products/:id" element={<ProductDetail />} />

      <Route path="/cart" element={<Cart />} />
      <Route path="/checkout" element={<Checkout />} />
      <Route path="/profile" element={<Profile />} />
      <Route path="/orders" element={<Orders />} />
      <Route path="/admin" element={<AdminDashboard />} />
    </Routes>
  );
}
