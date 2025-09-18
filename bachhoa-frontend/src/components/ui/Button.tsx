// src/components/ui/Button.tsx
import React from 'react';
type Props = React.ButtonHTMLAttributes<HTMLButtonElement> & { children: React.ReactNode; variant?: 'primary' | 'ghost' };
export default function Button({ children, variant = 'primary', className = '', ...rest }: Props) {
  const base = 'px-4 py-2 rounded-md font-medium transition';
  const v = variant === 'primary'
    ? 'bg-blue-600 text-white hover:bg-blue-700'
    : 'bg-transparent text-blue-600 border border-blue-600 hover:bg-blue-50';
  return (
    <button className={`${base} ${v} ${className}`} {...rest}>
      {children}
    </button>
  );
}
