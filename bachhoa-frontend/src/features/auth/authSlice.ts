// src/features/auth/authSlice.ts
import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { authApi, LoginPayload, RegisterPayload } from '../../api/authApi';
import { setAccessToken, setRefreshToken, clearTokens } from '../../utils/token';
import type { AuthState, User } from './authTypes';

const initialState: AuthState = {
  user: {
    id: '1',
    name: 'Trịnh Trần Phương Tuấn',
    email: 'ttpt@test.com',
    role: 'USER',
    createdAt: '2024-10-09T00:00:00Z'
  },
  loading: false,
  error: null
};


export const login = createAsyncThunk('auth/login', async (payload: LoginPayload, { rejectWithValue }) => {
  try {
    const data = await authApi.login(payload);
    // expect data: { accessToken, refreshToken, user? }
    if (data.accessToken) setAccessToken(data.accessToken);
    if (data.refreshToken) setRefreshToken(data.refreshToken);
    // try to fetch user if not provided
    const user = data.user ?? (await authApi.me().catch(() => null));
    return user as User | null;
  } catch (err: any) {
    if (err?.response?.data) return rejectWithValue(err.response.data);
    return rejectWithValue({ message: err.message ?? 'Login failed' });
  }
});

export const register = createAsyncThunk('auth/register', async (payload: RegisterPayload, { rejectWithValue }) => {
  try {
    const data = await authApi.register(payload);
    if (data.accessToken) setAccessToken(data.accessToken);
    if (data.refreshToken) setRefreshToken(data.refreshToken);
    const user = data.user ?? (await authApi.me().catch(() => null));
    return user as User | null;
  } catch (err: any) {
    if (err?.response?.data) return rejectWithValue(err.response.data);
    return rejectWithValue({ message: err.message ?? 'Register failed' });
  }
});

export const fetchMe = createAsyncThunk('auth/me', async (_, { rejectWithValue }) => {
  try {
    const data = await authApi.me();
    return data as User;
  } catch (err: any) {
    if (err?.response?.data) return rejectWithValue(err.response.data);
    return rejectWithValue({ message: err.message ?? 'Fetch profile failed' });
  }
});

export const logout = createAsyncThunk('auth/logout', async () => {
  clearTokens();
  // optionally call backend logout endpoint if exists
  try {
    await authApi.logout();
  } catch {
    /* ignore */
  }
  return true;
});

const slice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setUser(state, action: PayloadAction<User | null>) {
      state.user = action.payload;
    },
    clearError(state) {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // login
      .addCase(login.pending, (s) => {
        s.loading = true;
        s.error = null;
      })
      .addCase(login.fulfilled, (s, a) => {
        s.loading = false;
        s.user = a.payload;
      })
      .addCase(login.rejected, (s, a) => {
        s.loading = false;
        s.error = (a.payload as any)?.message ?? 'Login failed';
      })

      // register
      .addCase(register.pending, (s) => {
        s.loading = true;
        s.error = null;
      })
      .addCase(register.fulfilled, (s, a) => {
        s.loading = false;
        s.user = a.payload;
      })
      .addCase(register.rejected, (s, a) => {
        s.loading = false;
        s.error = (a.payload as any)?.message ?? 'Register failed';
      })

      // fetchMe
      .addCase(fetchMe.pending, (s) => {
        s.loading = true;
        s.error = null;
      })
      .addCase(fetchMe.fulfilled, (s, a) => {
        s.loading = false;
        s.user = a.payload;
      })
      .addCase(fetchMe.rejected, (s) => {
        s.loading = false;
        s.user = null;
      })

      // logout
      .addCase(logout.fulfilled, (s) => {
        s.user = null;
        s.error = null;
        s.loading = false;
      });
  },
});

export const { setUser, clearError } = slice.actions;
export default slice.reducer;
