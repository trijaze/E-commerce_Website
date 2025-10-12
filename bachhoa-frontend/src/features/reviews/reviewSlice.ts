// src/features/reviews/reviewSlice.ts

import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { reviewApi, AddReviewPayload } from '../../api/reviewApi';
import type { Review } from '../products/productTypes';

// Định nghĩa kiểu cho state của slice này
type ReviewState = {
  items: Review[];
  loading: boolean;
  error: string | null;
};

const initialState: ReviewState = {
  items: [],
  loading: false,
  error: null,
};

// Async Thunk để lấy danh sách reviews
export const fetchReviews = createAsyncThunk<Review[], string>(
  'reviews/fetch',
  async (productId, { rejectWithValue }) => {
    try {
      return await reviewApi.getReviewsByProductId(productId);
    } catch (err: any) {
      return rejectWithValue(err.message);
    }
  }
);

// Async Thunk để gửi review mới
export const submitReview = createAsyncThunk<Review, AddReviewPayload>(
  'reviews/submit',
  async (payload, { rejectWithValue }) => {
    try {
      return await reviewApi.addReview(payload);
    } catch (err: any) {
      // Lấy thông báo lỗi cụ thể từ backend nếu có
      return rejectWithValue(err?.response?.data?.error || 'Gửi đánh giá thất bại.');
    }
  }
);

const reviewSlice = createSlice({
  name: 'reviews',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchReviews.pending, (state) => { 
        state.loading = true; 
        state.error = null; // Reset lỗi khi bắt đầu tải
      })
      .addCase(fetchReviews.fulfilled, (state, action) => {
        state.loading = false;
        // SỬA LỖI: Luôn đảm bảo action.payload là một mảng
        // Nếu API trả về thứ gì đó không phải mảng, state.items sẽ là một mảng rỗng
        state.items = Array.isArray(action.payload) ? action.payload : [];
      })
      .addCase(fetchReviews.rejected, (state, action) => { 
        state.loading = false; 
        state.error = action.payload as string;
        state.items = []; // Reset về mảng rỗng khi có lỗi
      })
      .addCase(submitReview.fulfilled, (state, action) => {
        // Đảm bảo state.items là mảng trước khi unshift
        if (Array.isArray(state.items)) {
          state.items.unshift(action.payload);
        } else {
          state.items = [action.payload];
        }
      });
  },
});

export default reviewSlice.reducer;