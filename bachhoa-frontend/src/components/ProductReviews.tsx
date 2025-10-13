// src/components/ProductReviews.tsx
import React, { useEffect, useState } from 'react';
import axiosClient from '@/api/axiosClient';

interface Review {
  reviewId: number;
  userId: number;
  username: string;
  rating: number;
  title?: string;
  content: string;
  createdAt: string;
}

interface Props {
  productId: number;
}

export default function ProductReviews({ productId }: Props) {
  const [reviews, setReviews] = useState<Review[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [rating, setRating] = useState(5);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState<string | null>(null);

  const fetchReviews = async () => {
    setLoading(true);
    try {
      // ✅ axiosClient tự động thêm token qua interceptor
      const response = await axiosClient.get(`/secure/reviews?productId=${productId}`);
      setReviews(response.data);
      setError(null);
    } catch (err: any) {
      console.error('Fetch reviews error:', err);
      setError('Không tải được đánh giá.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReviews();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [productId]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!content.trim()) {
      setSubmitError('Vui lòng nhập nội dung đánh giá.');
      return;
    }

    setSubmitting(true);
    setSubmitError(null);

    try {
      // ✅ Bỏ headers vì axiosClient đã tự động thêm token
      await axiosClient.post(`/secure/reviews?productId=${productId}`, {
        rating,
        title: title.trim() || undefined,
        content: content.trim(),
      });
      
      // Reset form
      setTitle('');
      setContent('');
      setRating(5);
      
      // Reload reviews
      await fetchReviews();
      
    } catch (err: any) {
      console.error('Submit review error:', err);
      
      if (err.response?.status === 401) {
        setSubmitError('Bạn cần đăng nhập để gửi đánh giá.');
      } else {
        setSubmitError(err.response?.data?.error || 'Gửi đánh giá thất bại.');
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="mt-10">
      <h2 className="text-xl font-semibold mb-4">Đánh giá sản phẩm</h2>

      {/* Form gửi review */}
      <form onSubmit={handleSubmit} className="mb-6 space-y-3 p-4 border rounded-lg bg-gray-50">
        <div>
          <label className="block text-sm font-medium">Số sao</label>
          <select
            value={rating}
            onChange={(e) => setRating(Number(e.target.value))}
            className="mt-1 block w-full border rounded px-2 py-1"
          >
            {[5, 4, 3, 2, 1].map((v) => (
              <option key={v} value={v}>
                {v} sao
              </option>
            ))}
          </select>
        </div>
        
        <div>
          <label className="block text-sm font-medium">Tiêu đề (tùy chọn)</label>
          <input
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Tiêu đề"
            className="mt-1 block w-full border rounded px-3 py-2"
          />
        </div>
        
        <div>
          <label className="block text-sm font-medium">Nội dung đánh giá</label>
          <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="Nội dung đánh giá..."
            className="mt-1 block w-full border rounded px-3 py-2"
            rows={4}
            required
          />
        </div>
        
        {submitError && (
          <div className="text-red-600 text-sm bg-red-50 p-2 rounded">
            {submitError}
          </div>
        )}
        
        <button
          type="submit"
          disabled={submitting}
          className="bg-emerald-600 text-white px-4 py-2 rounded hover:bg-emerald-700 disabled:bg-emerald-300"
        >
          {submitting ? 'Đang gửi...' : 'Gửi đánh giá'}
        </button>
      </form>

      {/* Danh sách review */}
      {loading ? (
        <div className="text-gray-500">Đang tải đánh giá...</div>
      ) : error ? (
        <div className="text-red-600">{error}</div>
      ) : reviews.length === 0 ? (
        <div className="text-gray-500">Chưa có đánh giá nào.</div>
      ) : (
        <ul className="space-y-4">
          {reviews.map((r) => (
            <li key={r.reviewId} className="border p-3 rounded-lg bg-white">
              <div className="flex justify-between items-center mb-1">
                <span className="font-semibold">{r.username}</span>
                <span className="text-yellow-500">
                  {'★'.repeat(r.rating)}{'☆'.repeat(5 - r.rating)}
                </span>
              </div>
              {r.title && <div className="font-medium">{r.title}</div>}
              <div className="text-gray-700 whitespace-pre-line">{r.content}</div>
              <div className="text-gray-400 text-xs mt-1">
                {new Date(r.createdAt).toLocaleString('vi-VN')}
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}