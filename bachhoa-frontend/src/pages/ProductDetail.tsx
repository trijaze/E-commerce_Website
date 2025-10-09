import { useEffect, useMemo, useRef, useState } from 'react';
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

  const [variantIdx, setVariantIdx] = useState(0);
  const [mainImgIdx, setMainImgIdx] = useState(0);

  // --- Số lượng: cho phép xóa hẳn "1" rồi gõ "9"; cấm "0"
  const [qtyInput, setQtyInput] = useState('1');
  const [qtyTouched, setQtyTouched] = useState(false);

  const qtyNum = useMemo(() => {
    if (qtyInput === '') return undefined;
    const n = parseInt(qtyInput, 10);
    return Number.isNaN(n) ? undefined : n;
  }, [qtyInput]);
  const qtyInvalid = qtyInput === '0';
  const canBuy = !qtyInvalid && qtyInput !== '' && (qtyNum ?? 0) >= 1;

  const handleQtyChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const v = e.target.value.replace(/[^\d]/g, '');
    setQtyInput(v);
    if (!qtyTouched) setQtyTouched(true);
  };
  const handleQtyBlur = () => {
    if (qtyInput === '' || qtyInput === '0') {
      setQtyInput('1');
      setQtyTouched(false);
    }
  };
  const decQty = () => setQtyInput(String(Math.max(1, (qtyNum ?? 1) - 1)));
  const incQty = () => setQtyInput(String((qtyNum ?? 1) + 1));

  // thumbnails ref
  const thumbsRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    let alive = true;
    if (!productId || Number.isNaN(productId)) {
      setError('Đường dẫn sản phẩm không hợp lệ');
      setLoading(false);
      return;
    }

    setLoading(true);
    window.scrollTo(0, 0);

    getProductDetail(productId)
      .then((d) => {
        if (!alive) return;
        setDetail(d);
        setMainImgIdx(0);
        setVariantIdx(0);
        setQtyInput('1');
        document.title = `${d.name} - Bách Hóa Online`;
      })
      .catch((e: any) => setError(e?.message || 'Lỗi tải dữ liệu'))
      .finally(() => alive && setLoading(false));

    return () => {
      alive = false;
    };
  }, [productId]);

  const fmt = (n: number) =>
    new Intl.NumberFormat('vi-VN').format(n || 0) + ' đ';

  const formatVariantLabel = (v: ProductVariant) => {
    const attrs = v.attributes ?? {};
    const keys = Object.keys(attrs);
    const label =
      keys.length === 1
        ? String(Object.values(attrs)[0])
        : keys.map((k) => `${k}: ${attrs[k]}`).join(' / ');
    return `${label} — ${fmt(Number(v.price || 0))}`;
  };

  const variants = useMemo(() => {
    const arr = detail?.variants ?? [];
    const seen = new Set<string>();
    return arr.filter((v) => {
      const key = JSON.stringify(v.attributes || {});
      if (seen.has(key)) return false;
      seen.add(key);
      return true;
    });
  }, [detail]);

  const images = useMemo(() => {
    const arr = detail?.images ?? [];
    return arr.length ? arr : [{ imageId: -1, imageUrl: '/logo.png', isMain: true }];
  }, [detail]);

  const currentPrice = useMemo(() => {
    if (!detail) return 0;
    if (variants?.length)
      return Number(variants[variantIdx]?.price || detail.minPrice);
    return Number(detail.minPrice || 0);
  }, [detail, variants, variantIdx]);

  const priceRangeLabel = useMemo(() => {
    if (!detail) return fmt(0);
    const prices = variants?.length
      ? variants.map((v) => Number(v.price || 0))
      : [Number(detail.minPrice || 0)];
    const min = Math.min(...prices);
    const max = Math.max(...prices);
    return min === max ? fmt(min) : `${fmt(min)} – ${fmt(max)}`;
  }, [detail, variants]);

  const base = Number(detail?.basePrice || 0);
  const showDiscount = base > 0 && base > currentPrice;
  const pct = showDiscount ? Math.round(((base - currentPrice) / base) * 100) : 0;

  const prevImg = () => setMainImgIdx((i) => Math.max(0, i - 1));
  const nextImg = () => setMainImgIdx((i) => Math.min(images.length - 1, i + 1));

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'ArrowLeft') prevImg();
      if (e.key === 'ArrowRight') nextImg();
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [images.length]);

  // ===================== REVIEW =====================
  const [reviews, setReviews] = useState<
    { id: number; name: string; rating: number; comment: string; createdAt: string }[]
  >([]);
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState('');

  // load review list
  useEffect(() => {
    if (!productId) return;
    fetch(`/api/reviews?productId=${productId}`)
      .then((r) => r.json())
      .then((data) => {
        if (data.ok) setReviews(data.data);
      })
      .catch(() => {});
  }, [productId]);

  const handleAddReview = async (e: React.FormEvent) => {
    e.preventDefault();
    const formData = new URLSearchParams();
    formData.append('productId', String(productId));
    formData.append('userId', '1'); // mock user id
    formData.append('rating', rating.toString());
    formData.append('comment', comment);

    const res = await fetch('/api/reviews', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: formData.toString(),
    });

    const data = await res.json();
    if (data.ok) {
      alert('Đã gửi đánh giá!');
      setComment('');
      fetch(`/api/reviews?productId=${productId}`)
        .then((r) => r.json())
        .then((data) => data.ok && setReviews(data.data));
    }
  };

  // ===================== GALLERY SCROLL =====================
  useEffect(() => {
    const el = thumbsRef.current;
    const btn = el?.querySelector<HTMLButtonElement>(
      `button[data-idx="${mainImgIdx}"]`
    );
    if (el && btn) {
      const bx = btn.offsetLeft - el.clientWidth / 2 + btn.clientWidth / 2;
      el.scrollTo({ left: Math.max(0, bx), behavior: 'smooth' });
    }
  }, [mainImgIdx]);

  // ===================== RENDER =====================
  if (loading) {
    return <div className="p-6">Đang tải...</div>;
  }
  if (error) return <div className="p-6 text-red-600">Lỗi: {error}</div>;
  if (!detail) return <div className="p-6">Không tìm thấy sản phẩm.</div>;

  const hero = images[mainImgIdx]?.imageUrl ?? '/logo.png';

  return (
    <div className="mx-auto max-w-6xl p-4 md:p-6">
      {/* breadcrumb */}
      <div className="mb-3 text-sm text-gray-500">
        <Link to="/" className="hover:underline">Trang chủ</Link> /{' '}
        <Link to="/products" className="hover:underline">Sản phẩm</Link> /{' '}
        <span className="text-gray-700">{detail.name}</span>
      </div>

      {/* === layout === */}
      <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
        {/* ảnh */}
        <div>
          <div className="aspect-[4/3] overflow-hidden rounded-2xl bg-gray-50">
            <img src={hero} alt={detail.name} className="h-full w-full object-cover" />
          </div>
          <div ref={thumbsRef} className="mt-2 flex gap-2 overflow-x-auto">
            {images.map((img, i) => (
              <button
                key={i}
                data-idx={i}
                onClick={() => setMainImgIdx(i)}
                className={`h-16 w-24 overflow-hidden rounded-xl border ${
                  i === mainImgIdx ? 'ring-2 ring-emerald-500' : ''
                }`}
              >
                <img src={img.imageUrl} alt={`Ảnh ${i + 1}`} className="h-full w-full object-cover" />
              </button>
            ))}
          </div>
        </div>

        {/* panel phải */}
        <div className="rounded-2xl border bg-white p-5 shadow-sm">
          <h1 className="text-2xl font-semibold">{detail.name}</h1>
          <div className="mt-2 flex items-center gap-3">
            <div className="text-emerald-600 text-3xl font-bold">{priceRangeLabel}</div>
            {showDiscount && <span className="text-red-500 font-semibold">-{pct}%</span>}
          </div>

          {/* Biến thể */}
          {variants?.length > 0 && (
            <div className="mt-5">
              <div className="mb-1 text-sm font-medium">Chọn biến thể</div>
              <select
                className="w-full rounded-xl border px-3 py-2"
                value={variantIdx}
                onChange={(e) => setVariantIdx(Number(e.target.value))}
              >
                {variants.map((v, i) => (
                  <option key={v.variantId ?? i} value={i}>
                    {formatVariantLabel(v)}
                  </option>
                ))}
              </select>
            </div>
          )}

          {/* Số lượng + CTA */}
          <div className="mt-4 flex items-center gap-3">
            <div className="flex items-stretch">
              <button type="button" onClick={decQty} className="h-10 w-10 rounded-l-xl border bg-white">-</button>
              <input
                type="text"
                inputMode="numeric"
                pattern="[0-9]*"
                value={qtyInput}
                onChange={handleQtyChange}
                onBlur={handleQtyBlur}
                className="h-10 w-20 text-center border-t border-b"
              />
              <button type="button" onClick={incQty} className="h-10 w-10 rounded-r-xl border bg-white">+</button>
            </div>
            <button
              disabled={!canBuy}
              className="flex-1 rounded-xl bg-emerald-600 px-4 py-2 font-semibold text-white hover:bg-emerald-700"
            >
              Thêm vào giỏ
            </button>
          </div>
        </div>
      </div>

      {/* Mô tả */}
      {detail.description && (
        <div className="mt-10">
          <h2 className="mb-3 text-lg font-semibold">Thông tin sản phẩm</h2>
          <div className="rounded-2xl border bg-white p-5 text-gray-700 whitespace-pre-line">
            {detail.description}
          </div>
        </div>
      )}

      {/* Sản phẩm liên quan */}
      <RelatedProducts productId={id ?? ''} limit={8} />

      {/* Đánh giá */}
      <section id="reviews" className="mt-10 rounded-2xl border bg-white p-5">
        <h2 className="text-lg font-semibold mb-3">Đánh giá & nhận xét</h2>

        <form onSubmit={handleAddReview} className="mb-5 space-y-3">
          <label className="block text-sm font-medium text-gray-700">
            Đánh giá (sao):
            <select
              value={rating}
              onChange={(e) => setRating(Number(e.target.value))}
              className="ml-2 rounded border px-2 py-1"
            >
              {[5,4,3,2,1].map(n => <option key={n} value={n}>{n}</option>)}
            </select>
          </label>

          <textarea
            value={comment}
            onChange={(e) => setComment(e.target.value)}
            placeholder="Nhập nhận xét của bạn..."
            className="w-full rounded border px-3 py-2"
            rows={3}
          />

          <button
            type="submit"
            className="rounded bg-emerald-600 px-4 py-2 text-white font-semibold hover:bg-emerald-700"
          >
            Gửi đánh giá
          </button>
        </form>

        {reviews.length === 0 ? (
          <p className="text-gray-500">Chưa có đánh giá nào.</p>
        ) : (
          <ul className="space-y-3">
            {reviews.map((r) => (
              <li key={r.id} className="rounded-xl border p-3">
                <div className="font-semibold text-gray-800">{r.name}</div>
                <div className="text-yellow-500 text-sm">⭐ {r.rating}/5</div>
                <div className="text-gray-700 mt-1">{r.comment}</div>
                <div className="text-xs text-gray-400">{new Date(r.createdAt).toLocaleString()}</div>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  );
}
