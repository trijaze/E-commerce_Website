// src/routes/AppRoutes.tsx
import { Routes, Route } from 'react-router-dom';
import ProductList from '../pages/ProductList';
import ProductDetail from '../pages/ProductDetail';
import Cart from '../pages/Cart';
import Checkout from '../pages/Checkout';
import Profile from '../pages/Profile';
import Orders from '../pages/Orders';
import AdminDashboard from '../pages/admin/Dashboard';
import ProductManagement from '../pages/admin/ProductManagement';
import Login from '../pages/Login';
import Register from '../pages/Register';
import ChangePassword from '../pages/ChangePassword';
import ForgotPassword from '../pages/ForgotPassword';
import AuthLayout from '../components/layout/AuthLayout';
import AdminLayout from '../components/layout/AdminLayout';
import MainLayout from '../components/layout/MainLayout';
import Home from '@/pages/Home';
import AdminLoginPage from '../pages/admin/LoginPage';

// HOA: Trang "/" dùng ProductList để hiện data thật từ BE (tránh data mock ở Home)
export default function AppRoutes() {
  return (
    <Routes>
      <Route element={<MainLayout />}>
        {/* HOA: Trang chủ -> danh sách sản phẩm */}
        <Route path="/" element={<Home />} />

        {/* Giữ /products để đồng nhất với menu */}
        <Route path="/products" element={<ProductList />} />
        <Route path="/products/:id" element={<ProductDetail />} />

        <Route path="/cart" element={<Cart />} />
        <Route path="/checkout" element={<Checkout />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/orders" element={<Orders />} />
      </Route>

      {/* Admin Routes */}
      <Route element={<AdminLayout />}>
        <Route path="/admin" element={<AdminDashboard />} />
        <Route path="/admin/products" element={<ProductManagement />} />
        <Route path="/admin/orders" element={<div className="p-6"><h1 className="text-2xl font-bold">Đơn hàng - Coming Soon</h1></div>} />
        <Route path="/admin/users" element={<div className="p-6"><h1 className="text-2xl font-bold">Người dùng - Coming Soon</h1></div>} />
        <Route path="/admin/settings" element={<div className="p-6"><h1 className="text-2xl font-bold">Cài đặt - Coming Soon</h1></div>} />
        <Route path="/admin/reports" element={<div className="p-6"><h1 className="text-2xl font-bold">Báo cáo - Coming Soon</h1></div>} />
      </Route>

      {/* Auth Routes */}
      <Route element={<AuthLayout />}>
        <Route path="/login" element={<Login />} />
        <Route path="/admin/login" element={<AdminLoginPage />} />
        <Route path="/register" element={<Register />} />
        <Route path="/forgotPassword" element={<ForgotPassword />} />
        <Route path="/changePassword" element={<ChangePassword />} />
      </Route>
    </Routes>
  );
}
