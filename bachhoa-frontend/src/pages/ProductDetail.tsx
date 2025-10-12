// src/pages/ProductDetail.tsx
import React, { useEffect, useMemo, useState, useCallback } from 'react';
import { Link, useParams } from 'react-router-dom';
import {
  getProductDetail,
  type ProductDetail as Detail,
  type ProductVariant,
  type ProductImage,
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
      .then((d) => {
        if (!mounted) return;
        setDetail(d);
        setVariantIdx(0);
        setMainImgIdx(0);
        setError(null);
      })
      .catch((e) => {
        console.error(e);
        if (!mounted) return;
        setError('Không tải được chi tiết sản phẩm.');
      })
      .finally(() => mounted && setLoading(false));
    return () => {
      mounted = false;
    };
  }, [productId]);

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

  const images = detail.images ?? [];
  const variants: ProductVariant[] = detail.variants ?? [];

  // Giá theo biến thể (nếu có) hoặc min/base price
  const currentPrice = useMemo(() => {
    if (!variants?.length) return detail?.minPrice ?? detail?.basePrice ?? 0;
    const v = variants[Math.min(variantIdx, variants.length - 1)];
    return v?.price ?? detail?.minPrice ?? detail?.basePrice ?? 0;
  }, [detail, variants, variantIdx]);

  const formatPrice = (v: number) =>
    v.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });

  // Đảm bảo index hợp lệ khi danh sách ảnh đổi
  useEffect(() => {
    if (!images.length) setMainImgIdx(0);
    else if (mainImgIdx > images.length - 1) setMainImgIdx(0);
  }, [images.length, mainImgIdx]);

  // Mũi tên chuyển ảnh — thêm z-10 để luôn click được
  const prevImg = useCallback(
    () => setMainImgIdx((i: number) => (images.length ? (i - 1 + images.length) % images.length : 0)),
    [images.length]
  );
  const nextImg = useCallback(
    () => setMainImgIdx((i: number) => (images.length ? (i + 1) % images.length : 0)),
    [images.length]
  );

  const onChangeQty = (val: string) => {
    val = val.replace(/[^\d]/g, '');
    setQtyInput(val === '' ? '' : String(Math.min(999, Math.max(1, Number(val)))));
  };

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
                className="max-h-full max-w-full object-contain select-none"
                draggable={false}
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
                  className="absolute z-10 left-2 top-1/2 -translate-y-1/2 rounded-full border bg-white/80 px-3 py-2 hover:bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                >
                  ‹
                </button>
                <button
                  type="button"
                  aria-label="Ảnh kế"
                  onClick={nextImg}
                  className="absolute z-10 right-2 top-1/2 -translate-y-1/2 rounded-full border bg-white/80 px-3 py-2 hover:bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                >
                  ›
                </button>
              </>
            )}
          </div>

          {/* Thumbnails */}
          {!!images.length && (
            <div className="mt-3 grid grid-cols-5 sm:grid-cols-6 md:grid-cols-7 lg:grid-cols-8 gap-2">
              {images.map((img: ProductImage, idx: number) => (
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
          <div className="text-xl font-bold text-rose-600 mb-3">{formatPrice(currentPrice)}</div>

          {/* Biến thể (nếu có) */}
          {!!(detail.variants?.length) && (
            <div className="mb-4">
              <div className="text-sm text-gray-600 mb-2">Chọn loại</div>
              <div className="flex flex-wrap gap-2">
                {variants.map((v, i) => {
                  const label = Object.entries(v.attributes || {})
                    .map(([k, vv]) => `${k}: ${vv}`)
                    .join(' · ') || `Mẫu ${i + 1}`;
                  const active = i === variantIdx;
                  return (
                    <button
                      key={v.variantId ?? i}
                      type="button"
                      onClick={() => setVariantIdx(i)}
                      className={`px-3 py-1.5 rounded-lg border text-sm ${
                        active ? 'border-indigo-600 bg-indigo-50 text-indigo-700' : 'hover:bg-gray-50'
                      }`}
                    >
                      {label}
                    </button>
                  );
                })}
              </div>
            </div>
          )}

          {/* Số lượng */}
          <div className="mb-4">
            <div className="text-sm text-gray-600 mb-2">Số lượng</div>
            <div className="flex items-center gap-2">
              <button
                type="button"
                onClick={() => onChangeQty(String(Math.max(1, Number(qtyInput || '1') - 1)))}
                className="w-9 h-9 rounded-lg border hover:bg-gray-50"
              >
                −
              </button>
              <input
                value={qtyInput}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => onChangeQty(e.target.value)}
                onBlur={() => setQtyTouched(true)}
                className="w-16 text-center rounded-lg border px-2 py-1.5"
                inputMode="numeric"
              />
              <button
                type="button"
                onClick={() => onChangeQty(String(Math.min(999, Number(qtyInput || '1') + 1)))}
                className="w-9 h-9 rounded-lg border hover:bg-gray-50"
              >
                +
              </button>
            </div>
            {qtyTouched && (Number(qtyInput || '0') < 1 || Number(qtyInput) > 999) && (
              <div className="text-xs text-rose-600 mt-1">Số lượng phải từ 1 tới 999.</div>
            )}
          </div>

          {/* Nút hành động */}
          <div className="flex items-center gap-3">
            <button
              type="button"
              className="px-5 py-2 rounded-xl bg-indigo-600 text-white hover:bg-indigo-700"
              onClick={() => {
                // TODO: gọi API add to cart sau khi nhóm thống nhất
                console.log('add-to-cart', {
                  productId,
                  variant: variants[variantIdx],
                  qty: Math.max(1, Number(qtyInput || '1')),
                });
              }}
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
