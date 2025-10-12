// src/components/layout/Navbar.tsx
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAppSelector, useAppDispatch } from '../../app/hooks';
import { useState, Fragment } from 'react';
import { Bars3Icon, XMarkIcon, ChevronDownIcon } from '@heroicons/react/24/outline';
import { Menu, Transition } from '@headlessui/react';
import { logout } from '../../features/auth/authSlice';


export default function Navbar() {
  const cartCount = useAppSelector((s) =>
    s.cart.items.reduce((a, b) => a + b.qty, 0)
  );

  const { user } = useAppSelector((s) => s.auth);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault(); // Ngăn trang tải lại
      if (searchTerm.trim()) {
          const encodedTerm = encodeURIComponent(searchTerm.trim());
          navigate(`/products?q=${encodedTerm}`);
          setSearchTerm(''); // reset ô tìm kiếm
      }
  };

  // Hàm xử lý đăng xuất
  const handleLogout = () => {
    dispatch(logout());
    window.location.href = '/'; // Chuyển hướng về trang chủ sau khi đăng xuất
  };

  const navItems = [
    { to: "/products", label: "Products" },
    { to: "/orders", label: "Orders" },
    { to: "/admin", label: "Admin" },
    { to: "/cart", label: "Cart", withBadge: true },
  ];

  return (
    <nav className="bg-green-100 border-b sticky top-0 z-50">
      <div className="container mx-auto px-4 py-3 flex items-center justify-between">
        {/* Logo */}
        <Link to="/" className="flex items-center gap-3">
          <img src="/logo.png" alt="logo" className="w-10 h-10 object-contain" />
          <div className="font-semibold text-lg">Bách Hóa Online</div>
        </Link>

        {/* Search bar */}
        <div className="flex-grow flex justify-center">
          <form onSubmit={handleSearch} className="relative w-full max-w-lg">
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Tìm kiếm sản phẩm..."
              className="w-full px-4 py-2 text-gray-900 bg-white border border-gray-300 rounded-full focus:outline-none focus:ring-2 focus:ring-green-500"
            />
            <button type="submit" className="absolute top-0 right-0 mt-2 mr-4">
              {/* Icon kính lúp (SVG) */}
              <svg className="w-6 h-6 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-4.35-4.35m1.35-5.65a7 7 0 11-14 0 7 7 0 0114 0z"></path>
              </svg>
            </button>
          </form>
        </div>

        {/* Desktop Menu */}
        <div className="hidden md:flex items-center gap-6">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className="hover:text-blue-600 transition-colors relative"
            >
              {item.label}
              {item.withBadge && cartCount > 0 && (
                <span className="ml-2 inline-flex items-center justify-center px-2 py-0.5 text-xs font-medium rounded-full bg-red-500 text-white">
                  {cartCount}
                </span>
              )}
            </NavLink>
          ))}
          {/*Cập nhật khi có user và khi không}
          {/* User Dropdown */}
          {user ? (
            <Menu as="div" className="relative">
              <Menu.Button className="flex items-center gap-2 hover:text-blue-600">
                <img
                  src="/user.png"
                  alt="User"
                  className="w-9 h-9 rounded-full border"
                />
                <ChevronDownIcon className="w-4 h-4" />
              </Menu.Button>

              <Transition
                as={Fragment}
                enter="transition ease-out duration-200"
                enterFrom="opacity-0 scale-95"
                enterTo="opacity-100 scale-100"
                leave="transition ease-in duration-150"
                leaveFrom="opacity-100 scale-100"
                leaveTo="opacity-0 scale-95"
              >
                <Menu.Items className="absolute right-0 mt-2 w-48 bg-white border rounded-lg shadow-lg py-2 z-50">
                  <Menu.Item>
                    {({ active }) => (
                      <Link
                        to="/profile"
                        className={`block px-4 py-2 text-sm ${active ? 'bg-gray-100 text-blue-600' : 'text-gray-700'
                          }`}
                      >
                        Profile
                      </Link>
                    )}
                  </Menu.Item>
                  <Menu.Item>
                    {({ active }) => (
                      <Link
                        to="/settings"
                        className={`block px-4 py-2 text-sm ${active ? 'bg-gray-100 text-blue-600' : 'text-gray-700'
                          }`}
                      >
                        Settings
                      </Link>
                    )}
                  </Menu.Item>
                  <Menu.Item>
                    {({ active }) => (
                      <button
                        onClick={handleLogout}
                        className={`block w-full text-left px-4 py-2 text-sm ${active ? 'bg-gray-100 text-red-600' : 'text-red-500'
                          }`}
                      >
                        Logout
                      </button>
                    )}
                  </Menu.Item>
                </Menu.Items>
              </Transition>
            </Menu>
          ) : (
            // Nếu chưa đăng nhập: Hiển thị nút Đăng nhập/Đăng ký
            <div className="flex items-center gap-4">
              <Link to="/login" className="text-sm font-medium hover:text-blue-600">Log in</Link>
              <Link to="/register" className="text-sm font-medium bg-green-500 text-white px-4 py-2 rounded-full hover:bg-green-600">Register</Link>
            </div>
          )}
        </div>

        {/* Mobile Hamburger */}
        <button
          className="md:hidden text-gray-800 w-8 h-8"
          onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
        >
          {mobileMenuOpen ? <XMarkIcon /> : <Bars3Icon />}
        </button>
      </div>

      {/* Mobile Menu */}
      <div
        className={`md:hidden overflow-hidden transition-all duration-300 ${mobileMenuOpen ? 'max-h-96' : 'max-h-0'
          }`}
      >
        <div className="flex flex-col bg-white px-4 py-2 gap-3">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className="hover:text-blue-600 transition-colors py-2 relative"
              onClick={() => setMobileMenuOpen(false)}
            >
              {item.label}
              {item.withBadge && cartCount > 0 && (
                <span className="ml-2 inline-flex items-center justify-center px-2 py-0.5 text-xs font-medium rounded-full bg-red-500 text-white">
                  {cartCount}
                </span>
              )}
            </NavLink>
          ))}

          {/* User Dropdown (Mobile) */}
          <div className="border-t pt-3">
            <Link to="/profile" className="block py-2 hover:text-blue-600">
              Profile
            </Link>
            <Link to="/settings" className="block py-2 hover:text-blue-600">
              Settings
            </Link>
            <button
              onClick={() => console.log("Logout clicked")}
              className="block w-full text-left py-2 text-red-500 hover:text-red-600"
            >
              Logout
            </button>
          </div>
        </div>
      </div>
    </nav >
  );
}
