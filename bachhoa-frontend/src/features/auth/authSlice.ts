import { createSlice, createAsyncThunk, PayloadAction, isAnyOf } from '@reduxjs/toolkit';
import { jwtDecode } from 'jwt-decode';
import { authApi, LoginPayload, RegisterPayload } from '../../api/authApi';
import { setAccessToken, setRefreshToken, clearTokens } from '../../utils/token';
import type { AuthState, User } from './authTypes';

const initialState: AuthState = {
  user: null,
  loading: false,
  error: null,
  isInitialized: false, // <-- THÊM: Trạng thái khởi tạo ban đầu là false
};

// --- HÀM HELPER VÀ ASYNC THUNKS GIỮ NGUYÊN ---
const getUserFromToken = (token: string): User => {
  const decodedToken: { sub: string; userId: number; role: 'customer' | 'admin' } = jwtDecode(token);
  return {
    userId: decodedToken.userId,
    username: decodedToken.sub,
    role: decodedToken.role,
    phoneNumber: '',
    email: '',
    createdAt: ''
  };
};

export const login = createAsyncThunk<User, LoginPayload>(
  'auth/login',
  async (payload, { rejectWithValue }) => {
    try {
      const data = await authApi.login(payload);
      setAccessToken(data.accessToken);
      setRefreshToken(data.refreshToken);
      return getUserFromToken(data.accessToken);
    } catch (err: any) {
      return rejectWithValue(err?.response?.data?.error || 'Đăng nhập thất bại.');
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
      return getUserFromToken(data.accessToken);
    } catch (err: any) {
      return rejectWithValue(err?.response?.data?.error || 'Đăng ký thất bại.');
    }
  });

export const fetchMe = createAsyncThunk<User, void>(
  'auth/me',
  async (_, { rejectWithValue }) => {
    try {
      const user = await authApi.me();
      return user;
    } catch (err: any) {
      return rejectWithValue('Failed to auto-login');
    }
  }
);

export const logout = createAsyncThunk('auth/logout', async () => {
  try {
    await authApi.logout();
  } finally {
    clearTokens();
  }
});


// --- TẠO SLICE ---
const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    clearError(state) {
      state.error = null;
    },
    // Reducer để đánh dấu đã khởi tạo xong khi không có token
    setInitialized(state) {
      state.isInitialized = true;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(logout.fulfilled, (state) => {
        state.user = null;
        state.loading = false;
        state.error = null;
      })
      .addMatcher(
        isAnyOf(login.pending, register.pending, fetchMe.pending),
        (state) => {
          state.loading = true;
          state.error = null;
        }
      )
      .addMatcher(
        isAnyOf(login.fulfilled, register.fulfilled, fetchMe.fulfilled),
        (state, action: PayloadAction<User>) => {
          state.loading = false;
          state.user = action.payload;
          // CẬP NHẬT: Khi fetchMe thành công, đánh dấu đã khởi tạo xong
          if (action.type === fetchMe.fulfilled.type) {
            state.isInitialized = true;
          }
        }
      )
      .addMatcher(
        isAnyOf(login.rejected, register.rejected, fetchMe.rejected),
        (state, action) => {
          state.loading = false;
          state.user = null;

          if (action.type === fetchMe.rejected.type) {
            state.isInitialized = true;
          } else {
            state.error = action.payload as string;
          }
        }
      );
  },
});

// CẬP NHẬT: Export action mới
export const { clearError, setInitialized } = authSlice.actions;
export default authSlice.reducer;