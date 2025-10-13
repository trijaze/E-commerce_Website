// src/pages/Profile.tsx
import React, { useState } from 'react';
import { useAppSelector, useAppDispatch } from '../app/hooks';
import { Card } from '@/components/ui/Card';
import Button from '@/components/ui/Button';
import { Link } from 'react-router-dom';
import { authApi, UserProfilePayload, AddressPayload } from '@/api/authApi';
import { setUser } from '@/features/auth/authSlice';
import { User } from '@/features/auth/authTypes';

// --- Component Modal ---
interface EditProfileModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly user: User;
  readonly onSave: (data: UserProfilePayload) => Promise<void>;
}

function EditProfileModal({ isOpen, onClose, user, onSave }: EditProfileModalProps) {
  const [formData, setFormData] = useState({ 
    username: user.username, 
    phoneNumber: user.phoneNumber,
    email: user.email 
  });
  
  const [address, setAddress] = useState<AddressPayload>(
    user.addresses?.[0] || { label: 'Nhà', addressLine: '', city: '', country: 'Vietnam', postalCode: '', isDefault: true }
  );

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };
  
  const handleAddressChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setAddress({ ...address, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError(null);
    try {
      const payload: UserProfilePayload = {
        ...formData,
        addresses: [address],
      };
      await onSave(payload);
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.error || 'Cập nhật thất bại.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50 p-4">
      <div className="bg-white p-6 rounded-lg shadow-xl w-full max-w-lg max-h-[90vh] overflow-y-auto">
        <h2 className="text-2xl font-bold mb-5">Chỉnh sửa hồ sơ</h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <fieldset className="border p-4 rounded-md">
            <legend className="text-lg font-semibold px-2">Thông tin cá nhân</legend>
            <div className="space-y-4">
                <div>
                    {/* ✅ SỬA LỖI: Thêm id và htmlFor */}
                    <label htmlFor="modal-username" className="block text-sm font-medium">Tên đăng nhập</label>
                    <input id="modal-username" name="username" value={formData.username} onChange={handleChange} className="mt-1 w-full px-3 py-2 border rounded" required />
                </div>
                <div>
                    {/* ✅ SỬA LỖI: Thêm id và htmlFor */}
                    <label htmlFor="modal-phoneNumber" className="block text-sm font-medium">Số điện thoại</label>
                    <input id="modal-phoneNumber" name="phoneNumber" value={formData.phoneNumber} onChange={handleChange} className="mt-1 w-full px-3 py-2 border rounded" required />
                </div>
                 <div>
                    {/* ✅ SỬA LỖI: Thêm id và htmlFor */}
                    <label htmlFor="modal-email" className="block text-sm font-medium">Email</label>
                    <input id="modal-email" value={formData.email} disabled className="mt-1 w-full px-3 py-2 border rounded bg-gray-100 cursor-not-allowed" />
                </div>
            </div>
          </fieldset>
          
          {/* ... phần fieldset cho Địa chỉ giao hàng giữ nguyên ... */}

          {error && <p className="text-red-500 font-semibold">{error}</p>}
          <div className="flex justify-end gap-3 pt-4">
            <Button type="button" variant="secondary" onClick={onClose}>Hủy</Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Đang lưu...' : 'Lưu thay đổi'}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}


// --- Component Trang Hồ sơ Chính (Đã được cập nhật) ---
export default function Profile() {
  const user = useAppSelector((s) => s.auth.user);
  const dispatch = useAppDispatch();
  const [isModalOpen, setIsModalOpen] = useState(false);

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center h-[70vh]">
        <p className="text-lg">Bạn chưa đăng nhập.</p>
        <Link to="/login" className="mt-3 text-blue-500 hover:underline">Đăng nhập ngay →</Link>
      </div>
    );
  }

  const handleSaveProfile = async (data: UserProfilePayload) => {
    try {
      const updatedUser = await authApi.updateProfile(data);
      dispatch(setUser(updatedUser));
    } catch (error) {
      console.error('Failed to update profile:', error);
      throw error;
    }
  };

  const mainAddress = user.addresses?.[0];

  return (
    <>
      <div className="max-w-3xl mx-auto px-4 py-10">
        <Card className="shadow-xl rounded-2xl overflow-hidden">
          {/* Header */}
          <div className="bg-gradient-to-r from-blue-600 to-indigo-500 p-6 text-white">
            <h1 className="text-3xl font-bold mb-1">Hồ sơ người dùng</h1>
            <p className="text-sm opacity-80">Thông tin tài khoản và cài đặt cá nhân</p>
          </div>

          {/* Body */}
          <div className="p-8 bg-white space-y-8">
            <div className="flex items-center space-x-6">
              <img
                src={`https://api.dicebear.com/9.x/initials/svg?seed=${user.username ?? 'User'}`}
                alt="avatar"
                className="w-24 h-24 rounded-full border-4 border-white shadow-lg"
              />
              <div className="space-y-1">
                <h2 className="text-2xl font-bold text-gray-800">{user.username}</h2>
                <p className="text-gray-500 flex items-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor"><path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z" /><path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z" /></svg>
                  {user.email}
                </p>
                <p className="text-gray-500 flex items-center gap-2">
                   <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor"><path d="M2 3a1 1 0 011-1h2.153a1 1 0 01.986.836l.74 4.435a1 1 0 01-.54 1.06l-1.548.773a11.037 11.037 0 006.105 6.105l.774-1.548a1 1 0 011.059-.54l4.435.74a1 1 0 01.836.986V17a1 1 0 01-1 1h-2C7.82 18 2 12.18 2 5V3z" /></svg>
                  {user.phoneNumber}
                </p>
              </div>
            </div>
          
            <div className="border-t pt-8">
               <h3 className="text-xl font-semibold text-gray-800 mb-4">Địa chỉ giao hàng</h3>
               {mainAddress?.addressLine ? (
                 <div className="text-gray-800 bg-gray-50 p-4 rounded-lg border">
                    <p className="font-bold text-lg">{mainAddress.label}</p>
                    <p className="mt-1">{mainAddress.addressLine}</p>
                    <p>{mainAddress.city}, {mainAddress.country}</p>
                 </div>
               ) : (
                 <div className="rounded-md bg-yellow-50 p-4 border border-yellow-200">
                    <div className="flex">
                      <div className="flex-shrink-0">
                        <svg className="h-5 w-5 text-yellow-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                          <path fillRule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 3.01-1.742 3.01H4.42c-1.53 0-2.493-1.676-1.743-3.01l5.58-9.92zM10 13a1 1 0 110-2 1 1 0 010 2zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
                        </svg>
                      </div>
                      <div className="ml-3">
                        <h3 className="text-sm font-medium text-yellow-800">Yêu cầu cập nhật địa chỉ</h3>
                        <div className="mt-2 text-sm text-yellow-700">
                          <p>Bạn chưa có địa chỉ giao hàng. Vui lòng cập nhật để có thể tiến hành đặt hàng.</p>
                        </div>
                        <div className="mt-4">
                            {/* ✅ SỬA LỖI: Button này giờ đã có thể nhận prop 'size' */}
                            <Button variant="secondary" size="sm" onClick={() => setIsModalOpen(true)}>
                                Cập nhật ngay
                            </Button>
                        </div>
                      </div>
                    </div>
                  </div>
               )}
            </div>

            <div className="flex justify-end gap-3 pt-6 border-t">
                <Button variant="secondary" onClick={() => setIsModalOpen(true)}>✏️ Chỉnh sửa hồ sơ</Button>
                <Link to="/changePassword">
                    <Button variant="secondary">🔑 Đổi mật khẩu</Button>
                </Link>
                <Button variant="danger" onClick={() => alert('Đăng xuất')}>🚪 Đăng xuất</Button>
            </div>
          </div>
        </Card>
      </div>

      {user && (
        <EditProfileModal
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          user={user}
          onSave={handleSaveProfile}
        />
      )}
    </>
  );
}