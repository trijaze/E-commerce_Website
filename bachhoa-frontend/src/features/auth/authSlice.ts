import { createSlice, createAsyncThunk, PayloadAction, isAnyOf } from '@reduxjs/toolkit';
import { jwtDecode } from 'jwt-decode'; // Sử dụng jwt-decode để giải mã JWT: npm install jwt-decode
import { authApi, LoginPayload, RegisterPayload } from '../../api/authApi';
import { setAccessToken, setRefreshToken, clearTokens } from '../../utils/token';
import type { AuthState, User } from './authTypes';

const initialState: AuthState = {
  user: null,
  loading: false,
  error: null,
};

// --- HÀM HELPER ĐỂ GIẢI MÃ TOKEN VÀ LẤY THÔNG TIN USER ---
const getUserFromToken = (token: string): User => {
  // Định nghĩa kiểu cho payload đã giải mã để TypeScript hiểu
  const decodedToken: { sub: string; userId: number; role: 'customer' | 'admin' } = jwtDecode(token);
  return {
    userId: decodedToken.userId,
    username: decodedToken.sub,
    role: decodedToken.role,
    // Các trường này ban đầu là rỗng và sẽ được điền đầy đủ khi `fetchMe` được gọi sau này
    phoneNumber: '',
    email: '',
    createdAt: ''
  };
};

// --- CÁC HÀNH ĐỘNG ASYNC (ASYNC THUNKS) ---

export const login = createAsyncThunk<User, LoginPayload>(
  'auth/login',
  async (payload, { rejectWithValue }) => {
    try {
      const data = await authApi.login(payload);
      setAccessToken(data.accessToken);
      setRefreshToken(data.refreshToken);
      // Giải mã token ngay lập tức để lấy thông tin user, tránh race condition
      const user = getUserFromToken(data.accessToken);
      return user;
    } catch (err: any) {
      const errorMsg = err?.response?.data?.error || 'Đăng nhập thất bại. Vui lòng thử lại.';
      return rejectWithValue(errorMsg);
    }
  }
);

export const register = createAsyncThunk<User, RegisterPayload>(
  'auth/register',
  async (payload, { rejectWithValue }) => {
    try {
      const data = await authApi.register(payload);
      setAccessToken(data.accessToken);
      setRefreshToken(data.refreshToken);
      // Giải mã token ngay lập tức để lấy thông tin user, tránh race condition
      const user = getUserFromToken(data.accessToken);
      return user;
    } catch (err: any) {
      const errorMsg = err?.response?.data?.error || 'Đăng ký thất bại. Vui lòng thử lại.';
      return rejectWithValue(errorMsg);
    }
  }
);

// fetchMe vẫn giữ nguyên để lấy thông tin đầy đủ và kiểm tra phiên tự động khi tải lại trang
export const fetchMe = createAsyncThunk<User, void>(
  'auth/me',
  async (_, { rejectWithValue }) => {
    try {
      const user = await authApi.me();
      return user;
    } catch (err: any) {
      // SỬA LỖI: Dùng rejectWithValue để thunk thất bại một cách chính xác
      // và gửi đi một thông báo lỗi có thể dùng được trong slice.
      const errorMessage = err?.response?.data?.error || 'Đã xảy ra lỗi không mong muốn.';
      return rejectWithValue(errorMessage);
    }
  }
);

export const logout = createAsyncThunk('auth/logout', async () => {
  try {
    await authApi.logout();
  } catch (e) {
    // Việc này cho SonarLint biết rằng bạn đã xem xét khả năng có lỗi
    // và quyết định không làm gì cả, vì finally vẫn sẽ chạy.
  } finally {
    clearTokens();
  }
});

// --- TẠO SLICE ---
const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    // Reducer để xóa lỗi thủ công từ UI
    clearError(state) {
      state.error = null;
    },
  },
  // Xử lý các trạng thái của async thunks
  extraReducers: (builder) => {
    builder
      // Xử lý riêng cho hành động logout.fulfilled
      .addCase(logout.fulfilled, (state) => {
        state.user = null;
        state.loading = false;
        state.error = null;
      })
      // Xử lý 'pending' cho tất cả các thunks (login, register, fetchMe)
      .addMatcher(
        isAnyOf(login.pending, register.pending, fetchMe.pending),
        (state) => {
          state.loading = true;
          state.error = null;
        }
      )
      // Xử lý 'fulfilled' cho tất cả các thunks (login, register, fetchMe)
      .addMatcher(
        isAnyOf(login.fulfilled, register.fulfilled, fetchMe.fulfilled),
        (state, action: PayloadAction<User>) => {
          state.loading = false;
          state.user = action.payload;
        }
      )
      // Xử lý 'rejected' cho tất cả các thunks
      .addMatcher(
        isAnyOf(login.rejected, register.rejected, fetchMe.rejected),
        (state, action) => {
          state.loading = false;
          state.user = null;
          // Chỉ hiển thị lỗi cho các hành động login và register,
          // fetchMe sẽ thất bại một cách "thầm lặng".
          if (action.type !== fetchMe.rejected.type) {
            state.error = action.payload as string;
          }
        }
      );
  },
});

export const { clearError } = authSlice.actions;
export default authSlice.reducer;

