import axiosClient from './axiosClient';

// Types for admin operations
export interface CreateProductRequest {
  name: string;
  description?: string;
  price: number;
  stock?: number;
  categoryId: number;
  imageUrl?: string;
  status?: boolean;
}

export interface UpdateProductRequest extends Partial<CreateProductRequest> {
  id: number;
}

export interface AdminProduct {
  id: number;
  name: string;
  description?: string;
  price: number;
  stock: number;
  categoryId: number;
  categoryName?: string;
  imageUrl?: string;
  status: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface AdminApiResponse<T> {
  data: T;
  error?: string;
}

// Admin Product API
export const adminApi = {
  // Get all products (including inactive for admin)
  getAllProducts: async (params?: {
    categoryId?: number;
    status?: boolean;
    search?: string;
  }): Promise<AdminApiResponse<AdminProduct[]>> => {
    const queryParams = new URLSearchParams();
    if (params?.categoryId) queryParams.append('categoryId', params.categoryId.toString());
    if (params?.status !== undefined) queryParams.append('status', params.status.toString());
    if (params?.search) queryParams.append('search', params.search);
    
    const url = `/products${queryParams.toString() ? `?${queryParams}` : ''}`;
    return axiosClient.get(url);
  },

  // Get product by ID
  getProductById: async (id: number): Promise<AdminApiResponse<AdminProduct>> => {
    return axiosClient.get(`/products/${id}`);
  },

  // Create new product
  createProduct: async (product: CreateProductRequest): Promise<AdminApiResponse<AdminProduct>> => {
    return axiosClient.post('/products', product);
  },

  // Update product
  updateProduct: async (id: number, product: Partial<CreateProductRequest>): Promise<AdminApiResponse<AdminProduct>> => {
    return axiosClient.put(`/products/${id}`, product);
  },

  // Delete product  
  deleteProduct: async (id: number): Promise<AdminApiResponse<{ message: string; id: number }>> => {
    return axiosClient.delete(`/products/${id}`);
  },

  // Bulk operations
  bulkUpdateStatus: async (ids: number[], status: boolean): Promise<AdminApiResponse<{ message: string }>> => {
    return axiosClient.post('/products/bulk-status', { ids, status });
  },

  // Get categories for dropdown
  getCategories: async (): Promise<AdminApiResponse<{ id: number; name: string }[]>> => {
    return axiosClient.get('/categories');
  },
};

// Mock implementation for development (can be toggled)
const USE_MOCK = true; // Set to true for development without backend

// Mock database - in-memory storage
const mockProducts: AdminProduct[] = [
  {
    id: 1,
    name: 'Gạo ST25 Premium',
    description: 'Gạo thơm cao cấp từ An Giang',
    price: 85000,
    stock: 100,
    categoryId: 1,
    categoryName: 'Gạo - Ngũ cốc',
    imageUrl: '/images/gao-st25.jpg',
    status: true,
  },
  {
    id: 2,
    name: 'Thịt heo ba chỉ',
    description: 'Thịt heo tươi sạch',
    price: 150000,
    stock: 50,
    categoryId: 2,
    categoryName: 'Thịt tươi sống',
    imageUrl: '/images/thit-heo.jpg',
    status: false,
  },
];

let nextId = 3;

// Helper function to get category name
const getCategoryName = (categoryId: number): string => {
  const categories = {
    1: 'Gạo - Ngũ cốc',
    2: 'Thịt tươi sống',
    3: 'Rau củ quả',
    4: 'Đồ uống',
  };
  return categories[categoryId as keyof typeof categories] || 'Khác';
};

export const adminApiMock = {
  getAllProducts: async (): Promise<AdminApiResponse<AdminProduct[]>> => {
    await new Promise(resolve => setTimeout(resolve, 500)); // Simulate API delay
    console.log('🔥 Mock API getAllProducts returning:', mockProducts);
    return {
      data: [...mockProducts] // Return copy to avoid mutation issues
    };
  },

  getProductById: async (id: number): Promise<AdminApiResponse<AdminProduct>> => {
    await new Promise(resolve => setTimeout(resolve, 300));
    return {
      data: {
        id,
        name: 'Mock Product',
        description: 'Mock description',
        price: 100000,
        stock: 10,
        categoryId: 1,
        imageUrl: '/images/mock.jpg',
        status: true,
      }
    };
  },

  createProduct: async (product: CreateProductRequest): Promise<AdminApiResponse<AdminProduct>> => {
    await new Promise(resolve => setTimeout(resolve, 800));
    
    const newProduct: AdminProduct = {
      id: nextId++,
      name: product.name,
      description: product.description || '',
      price: product.price,
      stock: product.stock ?? 0,
      categoryId: product.categoryId,
      categoryName: getCategoryName(product.categoryId),
      imageUrl: product.imageUrl || '/images/default.jpg',
      status: product.status ?? true,
    };
    
    mockProducts.push(newProduct);
    console.log('🔥 Mock API created product:', newProduct);
    console.log('🔥 Mock products now:', mockProducts);
    
    return {
      data: newProduct
    };
  },

  updateProduct: async (id: number, product: Partial<CreateProductRequest>): Promise<AdminApiResponse<AdminProduct>> => {
    await new Promise(resolve => setTimeout(resolve, 600));
    
    const existingIndex = mockProducts.findIndex(p => p.id === id);
    if (existingIndex === -1) {
      throw new Error('Product not found');
    }
    
    const existingProduct = mockProducts[existingIndex];
    const updatedProduct: AdminProduct = {
      ...existingProduct,
      name: product.name || existingProduct.name,
      description: product.description ?? existingProduct.description,
      price: product.price ?? existingProduct.price,
      stock: product.stock ?? existingProduct.stock,
      categoryId: product.categoryId ?? existingProduct.categoryId,
      categoryName: product.categoryId ? getCategoryName(product.categoryId) : existingProduct.categoryName,
      imageUrl: product.imageUrl ?? existingProduct.imageUrl,
      status: product.status ?? existingProduct.status,
    };
    
    mockProducts[existingIndex] = updatedProduct;
    console.log('🔥 Mock API updated product:', updatedProduct);
    
    return {
      data: updatedProduct
    };
  },

  deleteProduct: async (id: number): Promise<AdminApiResponse<{ message: string; id: number }>> => {
    await new Promise(resolve => setTimeout(resolve, 400));
    
    const existingIndex = mockProducts.findIndex(p => p.id === id);
    if (existingIndex === -1) {
      throw new Error('Product not found');
    }
    
    const deletedProduct = mockProducts.splice(existingIndex, 1)[0];
    console.log('🔥 Mock API deleted product:', deletedProduct);
    console.log('🔥 Mock products now:', mockProducts);
    
    return {
      data: {
        message: 'Product deleted successfully',
        id
      }
    };
  },

  getCategories: async (): Promise<AdminApiResponse<{ id: number; name: string }[]>> => {
    return {
      data: [
        { id: 1, name: 'Gạo - Ngũ cốc' },
        { id: 2, name: 'Thịt tươi sống' },
        { id: 3, name: 'Rau củ quả' },
        { id: 4, name: 'Đồ uống' },
      ]
    };
  }
};

// Export the appropriate API based on environment
export default USE_MOCK ? adminApiMock : adminApi;