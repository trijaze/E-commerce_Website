// src/features/products/productTypes.ts
export type Product = {
  id: string;
  name: string;
  description?: string;
  price: number;
  stock: number;
  brand?: string;
  images?: string[];
  categoryId?: string;
};
export type ProductsState = {
  items: Product[];
  loading: boolean;
  error?: string | null;
};
