import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { PencilIcon, TrashIcon, PlusIcon, EyeIcon } from '@heroicons/react/24/outline';
import adminApi from '../../api/adminApi';
import { AdminProduct, CreateProductRequest } from '../../types/admin';
import SimpleProductForm from './SimpleProductForm';
import ProductViewModal from './ProductViewModal';
import { getImageUrl } from '../../utils/imageUrl';

const ProductManagement: React.FC = () => {
  // console.log('🔥 ProductManagement rendering...');
  const [products, setProducts] = useState<AdminProduct[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [showViewModal, setShowViewModal] = useState(false);
  const [editingProduct, setEditingProduct] = useState<AdminProduct | null>(null);
  const [viewingProduct, setViewingProduct] = useState<AdminProduct | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<'all' | 'active' | 'inactive'>('all');

  // Load products
  const loadProducts = async () => {
    try {
      setLoading(true);
      const params: {
        categoryId?: number;
        status?: boolean;
        search?: string;
      } = {};
      if (statusFilter !== 'all') {
        params.status = statusFilter === 'active';
      }
      if (searchTerm.trim()) {
        params.search = searchTerm.trim();
      }
      
      const response = await adminApi.getAllProducts(params);
      // adminApi đã extract data array sẵn
      setProducts(response.data || []);
    } catch (error) {
      console.error('Error loading products:', error);
      toast.error('Không thể tải danh sách sản phẩm');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProducts();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [statusFilter, searchTerm]);

  // Handle create product
  const handleCreate = async (productData: CreateProductRequest | Partial<CreateProductRequest>) => {
    try {
      await adminApi.createProduct(productData as CreateProductRequest);
      
      setShowForm(false);
      toast.success('Tạo sản phẩm thành công!');
      
      // Reload products from server to get fresh data
      await loadProducts();
    } catch (error) {
      console.error('Error creating product:', error);
      toast.error('Không thể tạo sản phẩm');
    }
  };

  // Handle update product
  const handleUpdate = async (productData: CreateProductRequest | Partial<CreateProductRequest>) => {
    if (!editingProduct) return;
    
    try {
      await adminApi.updateProduct(editingProduct.id, productData);
      
      setShowForm(false);
      setEditingProduct(null);
      toast.success('Cập nhật sản phẩm thành công!');
      
      // Reload products from server to get fresh data
      await loadProducts();
    } catch (error) {
      console.error('Error updating product:', error);
      toast.error('Không thể cập nhật sản phẩm');
    }
  };

  // Handle delete product
  const handleDelete = async (product: AdminProduct) => {
    if (!confirm(`Bạn có chắc muốn xóa sản phẩm "${product.name}"?`)) {
      return;
    }

    try {
      await adminApi.deleteProduct(product.id);
      setProducts(products.filter(p => p.id !== product.id));
      toast.success('Xóa sản phẩm thành công!');
      // Reload products to ensure UI is up to date
      await loadProducts();
    } catch (error) {
      console.error('Error deleting product:', error);
      toast.error('Không thể xóa sản phẩm');
    }
  };

  // Open create form
  const openCreateForm = () => {
    setEditingProduct(null);
    setShowForm(true);
  };

  // Open edit form
  const openEditForm = (product: AdminProduct) => {
    setEditingProduct(product);
    setShowForm(true);
  };

  // Open view modal
  const openViewModal = (product: AdminProduct) => {
    setViewingProduct(product);
    setShowViewModal(true);
  };

  // Close forms and modals
  const closeForms = () => {
    setShowForm(false);
    setShowViewModal(false);
    setEditingProduct(null);
    setViewingProduct(null);
  };

  // Format price
  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND'
    }).format(price);
  };

  // Filter products
  const filteredProducts = products.filter(product => {
    const matchesSearch = product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         product.description?.toLowerCase().includes(searchTerm.toLowerCase());
    
    if (statusFilter === 'all') return matchesSearch;
    if (statusFilter === 'active') return matchesSearch && product.status;
    if (statusFilter === 'inactive') return matchesSearch && !product.status;
    return matchesSearch;
  });

  return (
    <div className="p-6">
      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Quản lý sản phẩm</h1>
          <p className="text-gray-600 mt-1">
            Tổng cộng: {filteredProducts.length} sản phẩm
          </p>
        </div>
        <button
          onClick={openCreateForm}
          className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 flex items-center gap-2"
        >
          <PlusIcon className="w-5 h-5" />
          Thêm sản phẩm
        </button>
      </div>

      {/* Filters */}
      <div className="mb-6 flex gap-4 flex-wrap">
        <div className="flex-1 min-w-64">
          <input
            type="text"
            placeholder="Tìm kiếm sản phẩm..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        <div>
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value as 'all' | 'active' | 'inactive')}
            className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="all">Tất cả trạng thái</option>
            <option value="active">Đang hoạt động</option>
            <option value="inactive">Không hoạt động</option>
          </select>
        </div>
      </div>



      {/* Loading */}
      {loading && (
        <div className="flex justify-center py-8">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        </div>
      )}

      {/* Product Table */}
      {!loading && (
        <div className="bg-white rounded-lg shadow overflow-hidden">
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Sản phẩm
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Giá
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Kho
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Danh mục
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Nhà cung cấp
                  </th>
                  <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Thao tác
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {filteredProducts.map((product) => (
                  <tr key={product.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        <div className="flex-shrink-0 h-12 w-12">
                          <img
                            className="h-12 w-12 rounded-md object-cover"
                            src={getImageUrl(product.imageUrl)}
                            alt={product.name}
                            onError={(e) => {
                              const target = e.target as HTMLImageElement;
                              target.src = '/images/placeholder.jpg';
                            }}
                          />
                        </div>
                        <div className="ml-4">
                          <div className="text-sm font-medium text-gray-900">
                            {product.name}
                          </div>
                          <div className="text-sm text-gray-500 max-w-xs truncate">
                            {product.description}
                          </div>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">
                        {formatPrice(product.price)}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">{product.stock}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">
                        {product.categoryName || `ID: ${product.categoryId}`}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">
                        {product.supplierName || `ID: ${product.supplierId || 'N/A'}`}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                      <div className="flex items-center justify-end gap-2">
                        <button
                          onClick={() => openViewModal(product)}
                          className="text-gray-600 hover:text-gray-900"
                          title="Xem chi tiết"
                        >
                          <EyeIcon className="w-5 h-5" />
                        </button>
                        <button
                          onClick={() => openEditForm(product)}
                          className="text-blue-600 hover:text-blue-900"
                          title="Chỉnh sửa"
                        >
                          <PencilIcon className="w-5 h-5" />
                        </button>

                        <button
                          onClick={() => handleDelete(product)}
                          className="text-red-600 hover:text-red-900"
                          title="Xóa"
                        >
                          <TrashIcon className="w-5 h-5" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          
          {filteredProducts.length === 0 && (
            <div className="text-center py-12">
              <div className="text-gray-500">Không tìm thấy sản phẩm nào</div>
            </div>
          )}
        </div>
      )}

      {/* Product Form Modal */}
      {showForm && (
        <SimpleProductForm
          product={editingProduct}
          onSave={editingProduct ? handleUpdate : handleCreate}
          onCancel={closeForms}
        />
      )}

      {/* Product View Modal */}
      {showViewModal && viewingProduct && (
        <ProductViewModal
          product={viewingProduct}
          onClose={closeForms}
        />
      )}
    </div>
  );
};

export default ProductManagement;