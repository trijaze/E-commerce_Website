// src/components/ProductCard.tsx
import { Product } from '../features/products/productTypes';
import { formatCurrency, shortText } from '../utils/format';
import { Link } from 'react-router-dom';

export default function ProductCard({ product }: { product: Product }) {
  return (
    <div className="bg-white rounded-lg shadow p-4 flex flex-col">
      <div className="h-40 w-full bg-gray-100 rounded overflow-hidden flex items-center justify-center">
        <img src={product.images?.[0] ?? '/placeholder.jpg'} alt={product.name} className="h-full object-cover" />
      </div>
      <div className="mt-3 flex-1">
        <h3 className="font-semibold text-sm">{product.name}</h3>
        <div className="text-xs text-gray-500">{product.brand}</div>
        <div className="mt-2 text-lg font-medium">{formatCurrency(Number(product.price))}</div>
        <p className="text-sm text-gray-600 mt-2">{shortText(product.description, 100)}</p>
      </div>
      <div className="mt-3 flex items-center justify-between">
        <Link to={`/products/${product.id}`} className="text-sm text-blue-600">View</Link>
        <div className="text-xs text-gray-500">Stock: {product.stock}</div>
      </div>
    </div>
  );
}
