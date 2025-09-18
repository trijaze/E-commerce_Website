import axiosClient from './axiosClient';

// Định nghĩa type Product ở đây
export interface Product {
  id: string;
  name: string;
  price: number;
  brand: string;
  stock: number;
  images?: string[];
}

// Query type cho list
export type ProductListQuery = { 
  take?: number; 
  skip?: number; 
  categoryId?: string; 
  brand?: string; 
  min?: number; 
  max?: number; 
  q?: string 
};

export const productApi = {
  list: (q?: ProductListQuery) =>
    axiosClient.get<Product[]>('/products', { params: q }).then((r) => r.data),

  getById: (id: string) =>
    axiosClient.get<Product>(`/products/${id}`).then((r) => r.data),

  create: (payload: Omit<Product, 'id'>) =>
    axiosClient.post<Product>('/products', payload).then((r) => r.data),

  update: (id: string, payload: Partial<Product>) =>
    axiosClient.put<Product>(`/products/${id}`, payload).then((r) => r.data),

  remove: (id: string) =>
    axiosClient.delete(`/products/${id}`).then((r) => r.data),
};
