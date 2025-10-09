// src/api/authApi.ts
import axiosClient from './axiosClient';

export type LoginPayload = { email: string; password: string };
export type RegisterPayload = { name: string; email: string; password: string };

export const authApi = {
  login: (payload: LoginPayload) => axiosClient.post('/auth/login', payload).then((r) => r.data),
  register: (payload: RegisterPayload) => axiosClient.post('/auth/register', payload).then((r) => r.data),
  refresh: (refreshToken: string) => axiosClient.post('/auth/refresh', { refreshToken }).then((r) => r.data),
  me: () => axiosClient.get('/users/me').then((r) => r.data),
  logout: () => Promise.resolve(true),
};
