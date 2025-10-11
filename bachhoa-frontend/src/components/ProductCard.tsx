// src/components/ProductCard.tsx
import { Link } from "react-router-dom";
import type { Product } from "../features/products/productTypes";
import { formatCurrency } from "../utils/format";

type Props = {
  product: Product;
  onBuy?: (p: Product) => void; // optional: hook vào giỏ hàng
};

export default function ProductCard({ product, onBuy }: Props) {
  const href = `/products/${product.id}`;

  const handleBuy = (e: React.MouseEvent<HTMLButtonElement>) => {
    // chặn điều hướng sang trang chi tiết khi bấm MUA
    e.preventDefault();
    e.stopPropagation();
    onBuy?.(product);
    // tạm thời: nếu chưa nối giỏ hàng, cứ console
    if (!onBuy) console.log("BUY:", product.id, product.name);
  };

  return (
    <Link
      to={href}
      className="block bg-white rounded-2xl shadow-sm hover:shadow-md transition
                 border border-gray-100 group overflow-hidden"
    >
      {/* Ảnh 4:3 */}
      <div className="bg-gray-50 aspect-[4/3] w-full">
        <img
          src={product.images?.[0] ?? "/placeholder.jpg"}
          alt={product.name}
          className="w-full h-full object-cover"
          loading="lazy"
        />
      </div>

      {/* Nội dung */}
      <div className="p-3">
        <h3 className="text-[15px] font-medium text-gray-800 line-clamp-2 min-h-[40px]">
          {product.name}
        </h3>

        <div className="mt-1">
          <span className="text-emerald-600 font-semibold text-lg">
            {formatCurrency(Number(product.price))}
          </span>
        </div>

        {/* foot */}
        <div className="mt-2 flex items-center justify-between text-xs text-gray-500">
          <span>Tồn kho: {product.stock ?? 0}</span>
          {/* có thể hiển thị nhãn giảm giá/brand nếu muốn */}
        </div>

        {/* CTA MUA */}
        <button
          onClick={handleBuy}
          className="mt-3 w-full rounded-xl bg-emerald-600 text-white py-2
                     font-semibold hover:bg-emerald-700 active:translate-y-px"
        >
          MUA
        </button>
      </div>
    </Link>
  );
}
