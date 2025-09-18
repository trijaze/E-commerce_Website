// src/api/categoryApi.ts
import axiosClient from './axiosClient';
export const categoryApi = {
  list: () => axiosClient.get('/categories').then((r) => r.data),
};
