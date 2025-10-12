import { Link } from "react-router-dom";
import { Product } from "../features/products/productTypes";
import { formatCurrency, shortText } from "../utils/format";

type Props = {
  product: Product;
  onBuy?: (p: Product) => void; // optional: hook vào giỏ hàng
};

export default function ProductCard({ product, onBuy }: Props) {
  const href = `/products/${product.productId}`;

  // Xử lý URL ảnh (đã được chuẩn hóa từ productApi)
  const imageUrl =
    product.imageUrls?.[0]?.startsWith("http")
      ? product.imageUrls[0]
      : `http://localhost:8080${product.imageUrls?.[0] ?? ""}`;

  const handleBuy = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault(); // không chuyển trang khi bấm MUA
    e.stopPropagation();
    onBuy?.(product);
    if (!onBuy) console.log("BUY:", product.productId, product.name);
  };

  return (
    <Link
      to={href}
      className="block bg-white rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 
                 flex flex-col overflow-hidden hover:-translate-y-1 border border-gray-100"
    >
      {/* Ảnh sản phẩm */}
      <div className="relative bg-gray-100 flex items-center justify-center aspect-[4/3]">
        <img
          src={imageUrl}
          alt={product.name}
          className="max-h-full w-auto object-contain"
          loading="lazy"
        />
        {/* Huy hiệu giá nổi bật trên ảnh */}
        <div className="absolute bottom-2 right-2 bg-green-500 text-white text-sm font-semibold px-3 py-1 rounded-full shadow">
          {formatCurrency(product.basePrice)}
        </div>
      </div>

      {/* Nội dung */}
      <div className="p-4 flex-1 flex flex-col justify-between">
        <div>
          <h3 className="font-semibold text-base text-gray-800 line-clamp-1">
            {product.name}
          </h3>

          {/* Nếu có thương hiệu hoặc nhà cung cấp */}
          {product.supplierName && (
            <div className="text-sm text-emerald-700 font-medium">
              {product.supplierName}
            </div>
          )}

          <p className="text-sm text-gray-600 mt-2 line-clamp-3">
            {shortText(product.description ?? "", 100)}
          </p>
        </div>

        {/* Nút MUA */}
        <div className="mt-4">
          <button
            onClick={handleBuy}
            className="w-full rounded-xl bg-green-500 text-white py-2
                       font-semibold hover:bg-emerald-700 active:translate-y-px transition"
          >
            MUA
          </button>
        </div>
      </div>
    </Link>
  );
}
