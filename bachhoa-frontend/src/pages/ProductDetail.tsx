// src/pages/ProductDetail.tsx
import { useEffect, useMemo, useState, useCallback } from 'react';
import { Link, useParams } from 'react-router-dom';
import {
  getProductDetail,
  type ProductDetail as Detail,
  type ProductVariant,
} from '@/api/productDetailApi';
import RelatedProducts from '@/components/RelatedProducts';

export default function ProductDetail() {
  const { id } = useParams<{ id: string }>();
  const productId = Number(id ?? 0);

  const [detail, setDetail] = useState<Detail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Biến thể & ảnh
  const [variantIdx, setVariantIdx] = useState(0);
  const [mainImgIdx, setMainImgIdx] = useState(0);

  // Số lượng
  const [qtyInput, setQtyInput] = useState('1');
  const [qtyTouched, setQtyTouched] = useState(false);

  useEffect(() => {
    let mounted = true;
    setLoading(true);
    getProductDetail(productId)
      .then((res) => {
        if (!mounted) return;
        setDetail(res);
        setVariantIdx(0);
        // Ảnh chính nếu có ảnh isMain
        const idx = res.images?.findIndex((i) => i.isMain) ?? -1;
        setMainImgIdx(idx >= 0 ? idx : 0);
      })
      .catch((e: unknown) => setError((e as Error).message || 'Load thất bại'))
      .finally(() => mounted && setLoading(false));
    return () => {
      mounted = false;
    };
  }, [productId]);

  const images = detail?.images ?? [];
  const variants: ProductVariant[] = detail?.variants ?? [];

  const currentPrice = useMemo(() => {
    if (!variants?.length) return detail?.minPrice ?? detail?.basePrice ?? 0;
    const v = variants[Math.min(variantIdx, variants.length - 1)];
    return v?.price ?? detail?.minPrice ?? detail?.basePrice ?? 0;
  }, [detail, variants, variantIdx]);

  const formatPrice = (v: number) =>
    v.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });

  const decQty = useCallback(() => {
    setQtyTouched(true);
    setQtyInput((prev) => {
      const n = Math.max(1, parseInt(prev || '1', 10) - 1);
      return String(n);
    });
  }, []);

  const incQty = useCallback(() => {
    setQtyTouched(true);
    setQtyInput((prev) => {
      const n = Math.max(1, parseInt(prev || '1', 10) + 1);
      return String(n);
    });
  }, []);

  const onQtyChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setQtyTouched(true);
    const val = e.target.value.replace(/[^\d]/g, '');
    setQtyInput(val === '' ? '' : String(Math.min(999, Math.max(1, Number(val)))));
  };

  const prevImg = () =>
    setMainImgIdx((i) => (images.length ? (i - 1 + images.length) % images.length : 0));
  const nextImg = () =>
    setMainImgIdx((i) => (images.length ? (i + 1) % images.length : 0));

  if (loading) {
    return (
      <div className="container mx-auto px-4 py-10">
        <div className="animate-pulse text-gray-500">Đang tải chi tiết sản phẩm…</div>
      </div>
    );
  }

  if (error || !detail) {
    return (
      <div className="container mx-auto px-4 py-10">
        <p className="text-red-600">Lỗi: {error || 'Không tìm thấy sản phẩm'}</p>
        <Link to="/" className="text-indigo-600 underline">Quay lại trang chủ</Link>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Breadcrumb */}
      <nav className="text-sm mb-6 text-gray-600">
        <Link to="/" className="hover:underline">Trang chủ</Link>
        <span className="mx-2">/</span>
        <span className="text-gray-900">{detail.name}</span>
      </nav>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Ảnh sản phẩm */}
        <section className="relative">
          <div
            className="relative w-full aspect-square bg-white border rounded-2xl flex items-center justify-center overflow-hidden"
          >
            {images.length ? (
              // eslint-disable-next-line @next/next/no-img-element
              <img
                src={images[mainImgIdx]?.imageUrl}
                alt={detail.name}
                className="max-h-full max-w-full object-contain"
              />
            ) : (
              <div className="text-sm text-gray-400">Chưa có ảnh</div>
            )}

            {/* Nút mũi tên */}
            {images.length > 1 && (
              <>
                <button
                  type="button"
                  aria-label="Ảnh trước"
                  onClick={prevImg}
                  className="absolute left-2 top-1/2 -translate-y-1/2 rounded-full border bg-white/80 px-3 py-2 hover:bg-white"
                >
                  ‹
                </button>
                <button
                  type="button"
                  aria-label="Ảnh kế"
                  onClick={nextImg}
                  className="absolute right-2 top-1/2 -translate-y-1/2 rounded-full border bg-white/80 px-3 py-2 hover:bg-white"
                >
                  ›
                </button>
              </>
            )}
          </div>

          {/* Thumbnails */}
          {!!images.length && (
            <div className="mt-3 grid grid-cols-5 sm:grid-cols-6 md:grid-cols-7 lg:grid-cols-8 gap-2">
              {images.map((img, idx) => (
                <button
                  key={`${img.imageUrl}-${idx}`}
                  type="button"
                  onClick={() => setMainImgIdx(idx)}
                  className={`border rounded-lg overflow-hidden aspect-square ${
                    idx === mainImgIdx ? 'ring-2 ring-indigo-500' : 'hover:opacity-90'
                  }`}
                  title={`Ảnh ${idx + 1}`}
                >
                  {/* eslint-disable-next-line @next/next/no-img-element */}
                  <img src={img.imageUrl} alt={`${detail.name} ${idx + 1}`} className="object-cover w-full h-full" />
                </button>
              ))}
            </div>
          )}
        </section>

        {/* Thông tin & tùy chọn */}
        <section>
          <h1 className="text-2xl font-semibold mb-2">{detail.name}</h1>
          <div className="text-xl font-bold text-rose-600 mb-4">
            {formatPrice(currentPrice)}
          </div>

          {/* Chọn biến thể */}
          {variants.length > 0 && (
            <div className="mb-4">
              <label className="block mb-1 text-sm text-gray-600">Biến thể</label>
              <select
                className="w-full border rounded-md px-3 py-2"
                value={variantIdx}
                onChange={(e) => setVariantIdx(Number(e.target.value))}
              >
                {variants.map((v, i) => {
                  // Hiển thị label biến thể đẹp: key=value, key2=value2…
                  const label =
                    typeof v.attributes === 'object' && v.attributes
                      ? Object.entries(v.attributes as Record<string, string>)
                          .map(([k, val]) => `${k}: ${val}`)
                          .join(', ')
                      : `Biến thể ${i + 1}`;

                  return (
                    <option key={i} value={i}>
                      {label} — {formatPrice(v.price)}
                    </option>
                  );
                })}
              </select>
            </div>
          )}

          {/* Số lượng */}
          <div className="mb-6">
            <label className="block mb-1 text-sm text-gray-600">Số lượng</label>
            <div className="inline-flex items-center border rounded-md">
              <button type="button" onClick={decQty} className="px-3 py-2">-</button>
              <input
                value={qtyInput}
                onChange={onQtyChange}
                onBlur={() => {
                  if (qtyInput === '' || Number(qtyInput) < 1) setQtyInput('1');
                }}
                className="w-16 text-center outline-none py-2"
                inputMode="numeric"
              />
              <button type="button" onClick={incQty} className="px-3 py-2">+</button>
            </div>
            {qtyTouched && (qtyInput === '' || Number(qtyInput) < 1) && (
              <p className="text-xs text-rose-600 mt-1">Số lượng tối thiểu là 1</p>
            )}
          </div>

          {/* Nút hành động (demo) */}
          <div className="flex gap-3">
            <button
              type="button"
              className="px-5 py-2 rounded-xl bg-indigo-600 text-white hover:bg-indigo-700"
              onClick={() =>
                console.log('add-to-cart', {
                  productId,
                  variant: variants[variantIdx],
                  qty: Math.max(1, Number(qtyInput || '1')),
                })
              }
            >
              Thêm vào giỏ
            </button>
            <button
              type="button"
              className="px-5 py-2 rounded-xl border hover:bg-gray-50"
            >
              Mua ngay
            </button>
          </div>

          {/* Mô tả */}
          {detail.description && (
            <div className="mt-8 prose max-w-none">
              <h2 className="text-lg font-semibold mb-2">Mô tả</h2>
              <p className="whitespace-pre-line text-gray-700">{detail.description}</p>
            </div>
          )}
        </section>
      </div>

      {/* Sản phẩm liên quan */}
      <div className="mt-10">
        <RelatedProducts productId={productId} />
      </div>
    </div>
  );
}
