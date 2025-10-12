// src/api/reviewApi.ts

import axiosClient from './axiosClient';
import type { Review } from '../features/products/productTypes';

// Kiểu dữ liệu khi gửi một review mới
export type AddReviewPayload = {
  productId: string;
  rating: number;
  title: string;
  comment: string;
};

export const reviewApi = {
  /**
   * Lấy danh sách review của một sản phẩm (công khai, không cần token)
   * URL mới: GET /api/reviews?productId={id}
   */
  getReviewsByProductId: (productId: string): Promise<Review[]> => {
  // Đúng: Axios sẽ tự động tạo URL thành /api/reviews?productId=...
  return axiosClient.get('/reviews', { params: { productId } }).then((res) => res.data);
 },

  /**
   * Thêm một review mới (cần xác thực)
   * URL mới: POST /api/reviews?productId={id}
   */
  addReview: (payload: AddReviewPayload): Promise<Review> => {
    const { productId, ...reviewData } = payload;
    return axiosClient.post('/reviews', reviewData, { params: { productId } }).then((res) => res.data);
  },
};