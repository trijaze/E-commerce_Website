// src/components/ProductReviews.tsx

import React, { useEffect, useState } from 'react';
import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { fetchReviews, submitReview } from '@/features/reviews/reviewSlice';
import { formatDate } from '@/utils/format';
import Button from './ui/Button';
import Input from './ui/Input';

// SỬA LỖI 1: Tạo interface cho props và đánh dấu là readonly
interface ProductReviewsProps {
  readonly productId: string;
}

export default function ProductReviews({ productId }: ProductReviewsProps) {
  const dispatch = useAppDispatch();
  const { items: reviews, loading } = useAppSelector((s) => s.reviews);
  const { user } = useAppSelector((s) => s.auth);

  const [rating, setRating] = useState(5);
  const [title, setTitle] = useState('');
  const [comment, setComment] = useState('');

  useEffect(() => {
    if (productId) dispatch(fetchReviews(productId));
  }, [productId, dispatch]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!comment.trim()) {
      alert("Vui lòng nhập nội dung đánh giá.");
      return;
    }
    dispatch(submitReview({ productId, rating, title, comment }));
    setTitle(''); setComment(''); setRating(5);
  };

  return (
    <div className="mt-10 border-t pt-8">
      <h2 className="text-2xl font-semibold mb-6">Đánh giá từ khách hàng</h2>
      {user && (
        <form onSubmit={handleSubmit} className="mb-8 p-4 border rounded-lg bg-gray-50">
          <h3 className="font-semibold mb-3">Để lại đánh giá của bạn</h3>
          <div className="flex items-center gap-4 mb-3">
            {/* SỬA LỖI 2: Thêm htmlFor vào label */}
            <label htmlFor="quality-rating-select">Chất lượng:</label>
            {/* SỬA LỖI 2: Thêm id tương ứng vào select */}
            <select id="quality-rating-select" value={rating} onChange={(e) => setRating(Number(e.target.value))} className="border rounded p-2">
              {[5, 4, 3, 2, 1].map(n => <option key={n} value={n}>{n} sao</option>)}
            </select>
          </div>
          <Input placeholder="Tiêu đề (VD: Sản phẩm tuyệt vời!)" value={title} onChange={(e) => setTitle(e.target.value)} className="mb-3" />
          <textarea placeholder="Nội dung đánh giá..." value={comment} onChange={(e) => setComment(e.target.value)} required className="w-full p-2 border rounded" rows={3}></textarea>
          <Button type="submit" className="mt-3">Gửi đánh giá</Button>
        </form>
      )}
      <div className="space-y-6">
        {loading && <p>Đang tải đánh giá...</p>}
        {reviews.length === 0 && !loading && <p>Chưa có đánh giá nào cho sản phẩm này.</p>}
        {reviews.map((review) => (
          <div key={review.reviewId} className="flex gap-4 border-b pb-4">
            <div className="w-12 h-12 rounded-full bg-gray-200 flex items-center justify-center font-bold text-gray-600 shrink-0">
              {review.username.substring(0, 2).toUpperCase()}
            </div>
            <div>
              <p className="font-semibold">{review.username} <span className="text-sm font-normal text-yellow-500">{'★'.repeat(review.rating)}{'☆'.repeat(5 - review.rating)}</span></p>
              <p className="text-sm text-gray-500 mb-1">{formatDate(review.createdAt)}</p>
              {review.title && <p className="font-bold">{review.title}</p>}
              <p className="text-gray-700 whitespace-pre-line">{review.comment}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}