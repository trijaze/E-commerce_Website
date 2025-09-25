// src/utils/format.ts
export const formatCurrency = (n: number) => {
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'VND' }).format(n);
};
export const shortText = (s?: string, len = 120) => (s && s.length > len ? s.slice(0, len) + '...' : s ?? '');
