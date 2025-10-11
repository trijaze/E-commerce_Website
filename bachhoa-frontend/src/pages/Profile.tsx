// src/pages/Profile.tsx
import React from 'react';
import { useAppSelector } from '../app/hooks';
import { Card } from '@/components/ui/Card';
import Button from '@/components/ui/Button';
import { formatDate } from '@/utils/format';

export default function Profile() {
  const user = useAppSelector((s) => s.auth.user);

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center h-[70vh] text-gray-600">
        <p className="text-lg">Bạn chưa đăng nhập.</p>
        <a href="/login" className="mt-3 text-blue-500 hover:underline">
          Đăng nhập ngay →
        </a>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto px-4 py-10">
      <Card className="shadow-xl rounded-2xl overflow-hidden border border-gray-200">
        {/* Header */}
        <div className="bg-gradient-to-r from-blue-600 to-indigo-500 p-6 text-white">
          <h1 className="text-3xl font-semibold mb-1">Hồ sơ người dùng</h1>
          <p className="text-sm opacity-80">Thông tin tài khoản và cài đặt cá nhân</p>
        </div>

        {/* Body */}
        <div className="p-6 bg-white space-y-6">
          {/* Avatar */}
          <div className="flex flex-col sm:flex-row sm:items-center sm:space-x-5 space-y-4 sm:space-y-0">
            <img
              src={`https://api.dicebear.com/9.x/initials/svg?seed=${user.name ?? 'User'}`}
              alt="avatar"
              className="w-24 h-24 rounded-full border border-gray-300 shadow-sm"
            />
            <div>
              <h2 className="text-2xl font-bold text-gray-800">{user.name || 'Người dùng'}</h2>
              <p className="text-gray-500">{user.email}</p>
              <span
                className={`mt-2 inline-block px-3 py-1 text-sm font-medium rounded-full ${
                  user.role === 'ADMIN'
                    ? 'bg-red-100 text-red-700'
                    : 'bg-green-100 text-green-700'
                }`}
              >
                {user.role === 'ADMIN' ? 'Quản trị viên' : 'Khách hàng'}
              </span>
            </div>
          </div>

          {/* Info Section */}
          <div className="border-t border-gray-200 pt-6">
            <h3 className="text-lg font-semibold text-gray-700 mb-4">Thông tin chi tiết</h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-700">
              <div>
                <p className="text-sm text-gray-500">ID Người dùng</p>
                <p className="font-medium">{user.id}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Email</p>
                <p className="font-medium">{user.email}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Vai trò</p>
                <p className="font-medium">
                  {user.role === 'ADMIN' ? 'Quản trị viên' : 'Người dùng thường'}
                </p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Ngày tạo</p>
                <p className="font-medium">{formatDate(user.createdAt)}</p>
              </div>
            </div>
          </div>

          {/* Action Buttons */}
          <div className="flex justify-end gap-3 pt-4">
            <Button variant="secondary" onClick={() => alert('Chức năng chỉnh sửa đang phát triển')}>
              ✏️ Chỉnh sửa hồ sơ
            </Button>
            <Button variant="danger" onClick={() => alert('Đăng xuất')}>
              🚪 Đăng xuất
            </Button>
          </div>
        </div>
      </Card>
    </div>
  );
}
