import { Link } from "react-router-dom";
import { Product } from "../features/products/productTypes";
import { formatCurrency, shortText } from "../utils/format";

type Props = {
  product: Product;
  onBuy?: (p: Product) => void; // optional: hook vào giỏ hàng
};

export default function ProductCard({ product, onBuy }: Props) {
  const href = `/products/${product.productId}`;


  // ✅ Xử lý URL ảnh  
  const imageUrl = product.imageUrls?.[0] || '/images/placeholder.jpg';

  const handleBuy = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault(); // không chuyển trang khi bấm MUA
    e.stopPropagation();
    onBuy?.(product);
    if (!onBuy) console.log("BUY:", product.productId, product.name);
  };

   return (
    <Link
      to={href}
      className="flex flex-col bg-white rounded-2xl shadow-md hover:shadow-xl 
                 transition-all duration-300 border border-gray-100 overflow-hidden 
                 hover:-translate-y-1 h-full"
    >
      {/* Ảnh sản phẩm */}
      <div className="relative bg-gray-50 flex items-center justify-center w-full aspect-square">
        <img
          src={imageUrl}
          alt={product.name}
          className="object-contain max-h-[80%] max-w-[80%]"
          loading="lazy"
        />
        {/* Giá */}
        <div className="absolute bottom-2 right-2 bg-green-500 text-white text-sm font-semibold px-3 py-1 rounded-full shadow">
          {formatCurrency(product.basePrice)}
        </div>
      </div>

      {/* Nội dung */}
      <div className="flex flex-col flex-1 justify-between p-4 text-left">
        <div>
          <h3 className="font-semibold text-base text-gray-800 line-clamp-1">
            {product.name}
          </h3>

          {product.supplierName && (
            <div className="text-sm text-emerald-700 font-medium mt-1">
              {product.supplierName}
            </div>
          )}

          <p className="text-sm text-gray-600 mt-2 line-clamp-2">
            {shortText(product.description ?? "", 100)}
          </p>
        </div>
      </div>
    </Link>
  );
}
