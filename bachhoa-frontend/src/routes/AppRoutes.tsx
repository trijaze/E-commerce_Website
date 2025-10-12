// src/routes/AppRoutes.tsx
import { Routes, Route } from 'react-router-dom';
import ProductList from '../pages/ProductList';
import ProductDetail from '../pages/ProductDetail';
import Cart from '../pages/Cart';
import Checkout from '../pages/Checkout';
import Profile from '../pages/Profile';
import Orders from '../pages/Orders';
import AdminDashboard from '../pages/admin/Dashboard';
import Login from '../pages/Login';
import Register from '../pages/Register';
import ChangePassword from '../pages/ChangePassword';
import ForgotPassword from '../pages/ForgotPassword';
import AuthLayout from '../components/layout/AuthLayout';
import MainLayout from '../components/layout/MainLayout';

// HOA: Trang "/" dùng ProductList để hiện data thật từ BE (tránh data mock ở Home)
export default function AppRoutes() {
  return (
    <Routes>
      <Route element={<MainLayout />}>
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
      </Route>
      <Route element={<AuthLayout />}>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/forgotPassword" element={<ForgotPassword />} />
        <Route path="/changePassword" element={<ChangePassword />} />
      </Route>
    </Routes>
  );
}
