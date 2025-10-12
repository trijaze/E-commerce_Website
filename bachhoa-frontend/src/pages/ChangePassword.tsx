import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { useState } from 'react';
import { authApi } from '../api/authApi';
import { toast } from 'react-toastify';

// 1. Định nghĩa schema validation cho form đổi mật khẩu
const schema = z.object({
    oldPassword: z.string().min(6, 'Mật khẩu cũ phải có ít nhất 6 ký tự'),
    newPassword: z.string().min(6, 'Mật khẩu mới phải có ít nhất 6 ký tự'),
    confirmPassword: z.string().min(6, 'Mật khẩu xác nhận là bắt buộc'),
}).refine(data => data.newPassword === data.confirmPassword, {
    message: "Mật khẩu mới không khớp",
    path: ["confirmPassword"], // Lỗi sẽ được gán cho trường confirmPassword
});

type FormData = z.infer<typeof schema>;

export default function ChangePassword() {
    const [loading, setLoading] = useState(false);
    // Lấy thêm hàm `reset` từ useForm để xóa form sau khi thành công
    const { register, handleSubmit, formState: { errors }, reset } = useForm<FormData>({
        resolver: zodResolver(schema),
    });

    // 2. Hàm xử lý khi submit form
    const onSubmit = async (data: FormData) => {
        setLoading(true);
        try {
            // Chỉ gửi oldPassword và newPassword lên server theo yêu cầu của API
            const payload = { oldPassword: data.oldPassword, newPassword: data.newPassword };
            const response = await authApi.changePassword(payload);

            // Sử dụng toast để hiển thị thông báo thành công
            toast.success(response.message || "Đổi mật khẩu thành công!");

            // Xóa các trường trong form sau khi thành công
            reset();

        } catch (err: any) {
            // Hiển thị thông báo lỗi từ server
            toast.error(err?.response?.data?.error || "Đã xảy ra lỗi không mong muốn.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 max-w-md">
            <div>
                <label className="block text-sm font-medium mb-1">Mật khẩu cũ</label>
                <input {...register('oldPassword')} type="password" className="w-full border rounded px-3 py-2" placeholder="••••••••" />
                {errors.oldPassword && <p className="text-red-500 text-sm mt-1">{errors.oldPassword.message}</p>}
            </div>

            <div>
                <label className="block text-sm font-medium mb-1">Mật khẩu mới</label>
                <input {...register('newPassword')} type="password" className="w-full border rounded px-3 py-2" placeholder="••••••••" />
                {errors.newPassword && <p className="text-red-500 text-sm mt-1">{errors.newPassword.message}</p>}
            </div>

            <div>
                <label className="block text-sm font-medium mb-1">Xác nhận mật khẩu mới</label>
                <input {...register('confirmPassword')} type="password" className="w-full border rounded px-3 py-2" placeholder="••••••••" />
                {errors.confirmPassword && <p className="text-red-500 text-sm mt-1">{errors.confirmPassword.message}</p>}
            </div>

            <div>
                <button type="submit" className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 disabled:bg-green-300" disabled={loading}>
                    {loading ? 'Đang lưu...' : 'Lưu Thay Đổi'}
                </button>
            </div>
        </form>
    );
}

