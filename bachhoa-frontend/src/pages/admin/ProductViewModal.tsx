import React from 'react';
import { XMarkIcon } from '@heroicons/react/24/outline';
import { AdminProduct } from '../../types/admin';

interface ProductViewModalProps {
  product: AdminProduct;
  onClose: () => void;
}

const ProductViewModal: React.FC<ProductViewModalProps> = ({ product, onClose }) => {
  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND'
    }).format(price);
  };

  const formatDate = (dateString: string | undefined) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('vi-VN');
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      <div className="flex items-center justify-center min-h-screen px-4 pt-4 pb-20 text-center sm:block sm:p-0">
        <div className="fixed inset-0 transition-opacity bg-gray-500 bg-opacity-75" onClick={onClose}></div>

        <div className="inline-block w-full max-w-2xl p-6 my-8 overflow-hidden text-left align-middle transition-all transform bg-white shadow-xl rounded-lg">
          {/* Header */}
          <div className="flex items-center justify-between mb-6">
            <h3 className="text-lg font-medium leading-6 text-gray-900">
              Chi tiết sản phẩm
            </h3>
            <button
              type="button"
              className="text-gray-400 hover:text-gray-600"
              onClick={onClose}
            >
              <XMarkIcon className="w-6 h-6" />
            </button>
          </div>

          {/* Content */}
          <div className="space-y-6">
            {/* Product Image and Basic Info */}
            <div className="flex flex-col sm:flex-row gap-6">
              <div className="flex-shrink-0">
                <img
                  className="h-48 w-48 rounded-lg object-cover border"
                  src={product.imageUrl || '/images/placeholder.jpg'}
                  alt={product.name}
                  onError={(e) => {
                    const target = e.target as HTMLImageElement;
                    target.src = '/images/placeholder.jpg';
                  }}
                />
              </div>
              
              <div className="flex-1 space-y-4">
                <div>
                  <h4 className="text-xl font-semibold text-gray-900 mb-2">
                    {product.name}
                  </h4>
                  <div className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-sm font-medium ${
                    product.status
                      ? 'bg-green-100 text-green-800'
                      : 'bg-red-100 text-red-800'
                  }`}>
                    {product.status ? 'Đang hoạt động' : 'Không hoạt động'}
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4 text-sm">
                  <div>
                    <span className="font-medium text-gray-700">ID:</span>
                    <span className="ml-2 text-gray-600">#{product.id}</span>
                  </div>
                  <div>
                    <span className="font-medium text-gray-700">Danh mục:</span>
                    <span className="ml-2 text-gray-600">
                      {product.categoryName || `ID: ${product.categoryId}`}
                    </span>
                  </div>
                  <div>
                    <span className="font-medium text-gray-700">Giá:</span>
                    <span className="ml-2 text-gray-900 font-semibold">
                      {formatPrice(product.price)}
                    </span>
                  </div>
                  <div>
                    <span className="font-medium text-gray-700">Tồn kho:</span>
                    <span className={`ml-2 font-medium ${
                      product.stock > 10 
                        ? 'text-green-600' 
                        : product.stock > 0 
                          ? 'text-yellow-600' 
                          : 'text-red-600'
                    }`}>
                      {product.stock} sản phẩm
                    </span>
                  </div>
                </div>
              </div>
            </div>

            {/* Description */}
            {product.description && (
              <div>
                <h5 className="text-sm font-medium text-gray-700 mb-2">Mô tả sản phẩm:</h5>
                <div className="text-sm text-gray-600 bg-gray-50 p-3 rounded-md">
                  {product.description}
                </div>
              </div>
            )}

            {/* Image URL */}
            {product.imageUrl && (
              <div>
                <h5 className="text-sm font-medium text-gray-700 mb-2">URL hình ảnh:</h5>
                <div className="text-sm text-gray-600 bg-gray-50 p-3 rounded-md break-all">
                  <a 
                    href={product.imageUrl} 
                    target="_blank" 
                    rel="noopener noreferrer"
                    className="text-blue-600 hover:text-blue-800 underline"
                  >
                    {product.imageUrl}
                  </a>
                </div>
              </div>
            )}

            {/* Timestamps */}
            <div className="grid grid-cols-2 gap-4 text-sm text-gray-600 pt-4 border-t border-gray-200">
              <div>
                <span className="font-medium text-gray-700">Ngày tạo:</span>
                <span className="ml-2">{formatDate(product.createdAt)}</span>
              </div>
              <div>
                <span className="font-medium text-gray-700">Cập nhật:</span>
                <span className="ml-2">{formatDate(product.updatedAt)}</span>
              </div>
            </div>

            {/* Stock Status Warning */}
            {product.stock === 0 && (
              <div className="bg-red-50 border border-red-200 rounded-md p-3">
                <div className="flex items-center">
                  <div className="text-sm">
                    <span className="font-medium text-red-800">Cảnh báo:</span>
                    <span className="text-red-700 ml-1">Sản phẩm đã hết hàng</span>
                  </div>
                </div>
              </div>
            )}

            {product.stock > 0 && product.stock <= 10 && (
              <div className="bg-yellow-50 border border-yellow-200 rounded-md p-3">
                <div className="flex items-center">
                  <div className="text-sm">
                    <span className="font-medium text-yellow-800">Cảnh báo:</span>
                    <span className="text-yellow-700 ml-1">Sản phẩm sắp hết hàng (còn {product.stock} sản phẩm)</span>
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* Footer */}
          <div className="flex justify-end pt-6 border-t border-gray-200 mt-6">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 bg-gray-600 text-white rounded-md hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-gray-500"
            >
              Đóng
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductViewModal;