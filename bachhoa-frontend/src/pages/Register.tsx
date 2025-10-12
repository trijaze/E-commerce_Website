import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { useAppDispatch, useAppSelector } from '../app/hooks';
import { register as registerAction } from '../features/auth/authSlice';
import { Navigate, Link, useNavigate } from 'react-router-dom';
import { useState } from 'react';

// Schema validation không thay đổi
const schema = z
  .object({
    username: z.string().min(3, 'Tên đăng nhập phải có ít nhất 3 ký tự'),
    phone: z.string().min(10, 'Số điện thoại không hợp lệ'),
    email: z.string().email('Email không hợp lệ'),
    password: z.string().min(6, 'Mật khẩu phải có ít nhất 6 ký tự'),
    confirm: z.string().min(6, 'Mật khẩu xác nhận phải có ít nhất 6 ký tự'),
  })
  .refine((v) => v.password === v.confirm, {
    message: 'Mật khẩu không khớp',
    path: ['confirm'],
  });

type FormData = z.infer<typeof schema>;

export default function Register() {
  const dispatch = useAppDispatch();
  const auth = useAppSelector((s) => s.auth);
  const navigate = useNavigate(); // <-- 1. Thêm hook useNavigate

  // 2. Thêm state để quản lý thông báo thành công
  const [successMessage, setSuccessMessage] = useState('');

  const { register, handleSubmit, formState } = useForm<FormData>({
    resolver: zodResolver(schema),
  });
  const { errors } = formState;

  // 3. Cập nhật hàm onSubmit để xử lý sau khi đăng ký thành công
  const onSubmit = async (data: FormData) => {
    try {
      // Dùng unwrap() để bắt lỗi nếu thunk bị reject
      await dispatch(registerAction({ username: data.username, phone: data.phone, email: data.email, password: data.password })).unwrap();

      // Nếu không có lỗi, đặt thông báo thành công
      setSuccessMessage('Đăng ký thành công! Đang chuyển đến trang đăng nhập...');

      // Đợi 3 giây để người dùng đọc thông báo, sau đó chuyển hướng
      setTimeout(() => {
        navigate('/login');
      }, 3000);

    } catch (error) {
      // Lỗi đã được authSlice xử lý và hiển thị qua auth.error, không cần làm gì thêm
      console.error("Đăng ký thất bại:", error);
    }
  };

  return (
    <div className="max-w-l mx-auto p-6 bg-white shadow rounded">
      <h2 className="text-2xl font-semibold mb-4 text-center">Tạo tài khoản</h2>

      {/* Hiển thị thông báo lỗi hoặc thành công */}
      {auth.error && !successMessage && <div className="mb-3 text-red-600 text-sm p-3 bg-red-100 rounded">{auth.error}</div>}
      {successMessage && <div className="mb-3 text-green-700 text-sm p-3 bg-green-100 rounded">{successMessage}</div>}

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div>
          <label className="block text-sm font-medium mb-1">Tên đăng nhập</label>
          <input {...register('username')} className="w-full border rounded px-3 py-2" placeholder="Tên đăng nhập của bạn" />
          {errors.username && <p className="text-red-500 text-sm mt-1">{errors.username.message}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">Số điện thoại</label>
          <input {...register('phone')} type="tel" className="w-full border rounded px-3 py-2" placeholder="Số điện thoại của bạn" />
          {errors.phone && <p className="text-red-500 text-sm mt-1">{errors.phone.message}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">Email</label>
          <input {...register('email')} type="email" className="w-full border rounded px-3 py-2" placeholder="Email của bạn" />
          {errors.email && <p className="text-red-500 text-sm mt-1">{errors.email.message}</p>}
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Mật khẩu</label>
          <input {...register('password')} type="password" className="w-full border rounded px-3 py-2" placeholder="••••••••" />
          {errors.password && <p className="text-red-500 text-sm mt-1">{errors.password.message}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">Xác nhận mật khẩu</label>
          <input {...register('confirm')} type="password" className="w-full border rounded px-3 py-2" placeholder="••••••••" />
          {errors.confirm && <p className="text-red-500 text-sm mt-1">{errors.confirm.message}</p>}
        </div>

        <div className="flex items-center gap-x-3">
          <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:bg-blue-300" disabled={auth.loading || !!successMessage}>
            {auth.loading ? 'Đang tạo...' : 'Tạo tài khoản'}
          </button>
          <Link to="/login" className="text-sm text-blue-600 hover:underline">Đã có tài khoản?</Link>
        </div>
      </form>
    </div>
  );
}
