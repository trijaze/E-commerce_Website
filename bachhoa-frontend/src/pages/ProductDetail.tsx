// src/pages/ProductDetail.tsx
import React, { useEffect, useMemo, useState, useCallback } from 'react';
import { Link, useParams } from 'react-router-dom';
import {
  getProductDetail,
  type ProductDetail as Detail,
  type VariantDTO as ProductVariant,
  type ImageDTO as ProductImage,
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

  const formatPrice = (v: number) =>
    v.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });

  // ===== Hooks phải được gọi TRƯỚC mọi return =====

  // Danh sách ảnh/biến thể (ổn dù detail chưa có)
  const images: ProductImage[] = useMemo(() => detail?.images ?? [], [detail]);
  const variants: ProductVariant[] = useMemo(() => detail?.variants ?? [], [detail]);

  // Giá hiện hành: theo biến thể đang chọn (nếu có) hoặc basePrice
  const currentPrice = useMemo(() => {
    const base = detail?.basePrice ?? 0;
    if (!variants.length) return base;
    const v = variants[Math.min(variantIdx, variants.length - 1)];
    return v?.price ?? base;
  }, [detail?.basePrice, variants, variantIdx]);

  // Đảm bảo index hợp lệ khi ảnh đổi
  useEffect(() => {
    if (!images.length) setMainImgIdx(0);
    else if (mainImgIdx > images.length - 1) setMainImgIdx(0);
  }, [images.length, mainImgIdx]);

  // Fetch chi tiết
  useEffect(() => {
    let mounted = true;
    setLoading(true);
    getProductDetail(productId)
      .then((d) => {
        if (!mounted) return;
        setDetail(d);
        setVariantIdx(0);
        setMainImgIdx(0);
        setError(null);
      })
      .catch(() => {
        if (!mounted) return;
        setError('Không tải được chi tiết sản phẩm.');
      })
      .finally(() => mounted && setLoading(false));
    return () => {
      mounted = false;
    };
  }, [productId]);

  // Handlers
  const handleSelectVariant = useCallback(
    (idx: number) => {
      setVariantIdx(idx);
      // Nếu có ảnh gắn với variant thì auto chọn
      const targetVariantId = variants[idx]?.variantId ?? null;
      if (targetVariantId) {
        const foundIdx = images.findIndex((im) => im.variantId === targetVariantId);
        if (foundIdx >= 0) setMainImgIdx(foundIdx);
      }
    },
    [variants, images]
  );

  const handleThumbClick = useCallback((idx: number) => {
    setMainImgIdx(idx);
  }, []);

  // ===== Returns (sau khi đã gọi mọi Hook) =====
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
        <div className="text-rose-600">{error ?? 'Không tìm thấy sản phẩm.'}</div>
        <Link to="/" className="inline-block mt-4 text-indigo-600 hover:underline">
          ← Về trang chủ
        </Link>
      </div>
    );
  }

  // ===== Render chính =====
  return (
    <div className="container mx-auto px-4 py-8">
      <nav className="text-sm text-gray-500 mb-4 space-x-1">
        <Link to="/" className="hover:underline">Trang chủ</Link>
        <span>/</span>
        <Link to="/products" className="hover:underline">Sản phẩm</Link>
        <span>/</span>
        <span className="text-gray-700">{detail.name}</span>
      </nav>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Ảnh sản phẩm */}
        <section className="relative">
          <div className="relative w-full aspect-square bg-white border rounded-2xl flex items-center justify-center overflow-hidden">
            {images.length ? (
              <img
                src={images[mainImgIdx]?.imageUrl}
                alt={detail.name}
                className="max-h-full max-w-full object-contain select-none"
                draggable={false}
              />
            ) : (
              <div className="text-sm text-gray-400">Chưa có ảnh</div>
            )}
          </div>

          {/* thumbnails */}
          {images.length > 1 && (
            <div className="mt-3 grid grid-cols-5 gap-2">
              {images.map((img, idx) => (
                <button
                  key={img.imageId}
                  onClick={() => handleThumbClick(idx)}
                  className={`border rounded-lg overflow-hidden ${
                    idx === mainImgIdx ? 'ring-2 ring-emerald-500' : ''
                  }`}
                  title={`Ảnh ${idx + 1}`}
                >
                  <img src={img.imageUrl} alt={`thumb-${idx}`} className="w-full h-20 object-cover" />
                </button>
              ))}
            </div>
          )}
        </section>

        {/* Thông tin sản phẩm */}
        <section>
          <h1 className="text-2xl font-semibold">{detail.name}</h1>
          <div className="text-sm text-gray-500 mt-1">
            {detail.categoryName} • {detail.supplierName}
          </div>

          <div className="mt-4 text-emerald-600 text-xl font-bold">
            {formatPrice(currentPrice)}
          </div>

          {/* Variants */}
          {variants.length > 0 && (
            <div className="mt-4">
              <div className="text-sm text-gray-600 mb-2">Chọn phân loại:</div>
              <div className="flex flex-wrap gap-2">
                {variants.map((v, idx) => {
                  let label = v.name || v.sku || `Mẫu ${idx + 1}`;
                  try {
                    const parsed = v.name ? JSON.parse(v.name) : null;
                    if (parsed && typeof parsed === 'object') {
                      label = Object.entries(parsed)
                        .map(([k, val]) => `${k}: ${String(val)}`)
                        .join(', ');
                    }
                  } catch {}
                  return (
                    <button
                      key={v.variantId}
                      onClick={() => handleSelectVariant(idx)}
                      className={`px-3 py-2 rounded-full border text-sm ${
                        idx === variantIdx
                          ? 'bg-emerald-600 text-white border-emerald-600'
                          : 'hover:border-emerald-400'
                      }`}
                      title={v.sku ?? ''}
                    >
                      {label}
                    </button>
                  );
                })}
              </div>
            </div>
          )}

          {/* Actions */}
          <div className="mt-6 flex gap-3">
            <button className="px-5 py-3 rounded-xl bg-emerald-600 text-white">
              Thêm vào giỏ
            </button>
            <Link to="/products" className="px-5 py-3 rounded-xl border hover:bg-gray-50">
              Quay lại
            </Link>
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

      <div className="mt-10">
        <RelatedProducts productId={productId} />
      </div>
    </div>
  );
}
