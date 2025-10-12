// src/api/authApi.ts
import axiosClient from './axiosClient';
import type { User } from '../features/auth/authTypes';


export type LoginPayload = { identifier: string; password: string };
export type RegisterPayload = { username: string; phoneNumber: string; email: string; password: string };
export type ForgotPasswordPayload = { username: string; phoneNumber: string; newPassword: string }
export type ChangePasswordPayload = { oldPassword: string; newPassword: string };

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
}

export const authApi = {
  // --- API công khai (Public APIs) ---
  login: (payload: LoginPayload): Promise<AuthResponse> =>
    axiosClient.post('/auth/login', payload).then((r) => r.data),

  register: (payload: RegisterPayload): Promise<AuthResponse> =>
    axiosClient.post('/auth/register', payload).then((r) => r.data),

  refresh: (refreshToken: string): Promise<{ accessToken: string }> =>
    axiosClient.post('/auth/refresh', { refreshToken }).then((r) => r.data),

  forgotPassword: (payload: ForgotPasswordPayload) =>
    axiosClient.post('/auth/forgotPassword', payload).then((r) => r.data),

  // --- API được bảo vệ cho người dùng thường (Secured User APIs) ---
  me: (): Promise<User> =>
    axiosClient.get('/secure/users/me').then((r) => r.data),

  changePassword: (payload: ChangePasswordPayload) =>
    axiosClient.post('/secure/users/changepassword', payload).then(r => r.data),

  logout: () =>
    axiosClient.post('/secure/users/logout').then(r => r.data),

  // --- API được bảo vệ chỉ dành cho Admin (Secured Admin APIs) nếu có ---
  //getAllUsers: (): Promise<User[]> => 
  // axiosClient.get('/secure/admin/users').then((r) => r.data),

};
