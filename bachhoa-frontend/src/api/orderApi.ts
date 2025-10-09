// src/api/orderApi.ts
import axiosClient from './axiosClient';
export const orderApi = {
  create: (payload: { items: { productId: string; quantity: number }[] }) => axiosClient.post('/orders', payload).then((r) => r.data),
  list: () => axiosClient.get('/orders').then((r) => r.data),
  byId: (id: string) => axiosClient.get(`/orders/${id}`).then((r) => r.data),
};
