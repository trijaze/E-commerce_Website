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
                    <label htmlFor="username-input" className="block text-sm font-medium">Tên đăng nhập</label>
                    <input id="username-input" name="username" value={formData.username} onChange={handleChange} className="mt-1 w-full px-3 py-2 border rounded" required />
                </div>
                <div>
                    <label htmlFor="phoneNumber-input" className="block text-sm font-medium">Số điện thoại</label>
                    <input id="phoneNumber-input" name="phoneNumber" value={formData.phoneNumber} onChange={handleChange} className="mt-1 w-full px-3 py-2 border rounded" required />
                </div>
                 <div>
                    <label htmlFor="email-input" className="block text-sm font-medium">Email</label>
                    <input id="email-input" value={formData.email} disabled className="mt-1 w-full px-3 py-2 border rounded bg-gray-100 cursor-not-allowed" />
                </div>
            </div>
          </fieldset>
          
          <fieldset className="border p-4 rounded-md">
            <legend className="text-lg font-semibold px-2">Địa chỉ giao hàng</legend>
            <div className="space-y-4">
              <div>
                <label htmlFor="label-input" className="block text-sm font-medium">Tên gợi nhớ</label>
                <input id="label-input" name="label" value={address.label} onChange={handleAddressChange} className="mt-1 w-full px-3 py-2 border rounded" />
              </div>
              <div>
                <label htmlFor="addressLine-input" className="block text-sm font-medium">Địa chỉ chi tiết</label>
                <input id="addressLine-input" name="addressLine" value={address.addressLine} onChange={handleAddressChange} placeholder="Số nhà, tên đường..." className="mt-1 w-full px-3 py-2 border rounded" required />
              </div>
              <div>
                <label htmlFor="city-input" className="block text-sm font-medium">Tỉnh/Thành phố</label>
                <input id="city-input" name="city" value={address.city} onChange={handleAddressChange} className="mt-1 w-full px-3 py-2 border rounded" required />
              </div>
            </div>
          </fieldset>

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


// --- Main Profile Component ---
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
            <div className="bg-gradient-to-r from-blue-600 to-indigo-500 p-6 text-white">
                <h1 className="text-3xl font-semibold mb-1">Hồ sơ người dùng</h1>
                <p className="text-sm opacity-80">Thông tin tài khoản và cài đặt cá nhân</p>
            </div>
            <div className="p-6 bg-white space-y-6">
                <div className="flex items-center space-x-5">
                    <img
                        src={`https://api.dicebear.com/9.x/initials/svg?seed=${user.username ?? 'User'}`}
                        alt="avatar"
                        className="w-24 h-24 rounded-full border-4 border-white shadow-md"
                    />
                    <div>
                        <h2 className="text-2xl font-bold text-gray-800">{user.username}</h2>
                        <p className="text-gray-500">{user.email}</p>
                        <p className="text-gray-500">{user.phoneNumber}</p>
                    </div>
                </div>
            
                <div className="border-t pt-6">
                   <h3 className="text-lg font-semibold text-gray-700 mb-4">Địa chỉ mặc định</h3>
                   {mainAddress?.addressLine ? (
                     <div className="text-gray-800">
                        <p className="font-bold">{mainAddress.label}</p>
                        <p>{mainAddress.addressLine}</p>
                        <p>{mainAddress.city}, {mainAddress.country}</p>
                     </div>
                   ) : (
                     <p className="text-gray-500">Bạn chưa cập nhật địa chỉ.</p>
                   )}
                </div>

                <div className="flex justify-end gap-3 pt-4 border-t">
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