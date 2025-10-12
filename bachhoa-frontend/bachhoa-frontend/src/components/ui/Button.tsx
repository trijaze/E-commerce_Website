// src/components/ui/Button.tsx
import React from 'react';

type Variant = 'primary' | 'secondary' | 'danger' | 'ghost';

type Props = React.ButtonHTMLAttributes<HTMLButtonElement> & {
  children: React.ReactNode;
  variant?: Variant;
};

export default function Button({
  children,
  variant = 'primary',
  className = '',
  ...rest
}: Props) {
  const base =
    'px-4 py-2 rounded-md font-semibold transition focus:outline-none focus:ring-2 focus:ring-offset-2';

  const styles: Record<Variant, string> = {
    primary: 'bg-blue-600 text-white hover:bg-blue-700 focus:ring-blue-400',
    secondary: 'bg-gray-100 text-gray-800 hover:bg-gray-200 focus:ring-gray-400',
    danger: 'bg-red-600 text-white hover:bg-red-700 focus:ring-red-400',
    ghost: 'bg-transparent text-blue-600 hover:bg-blue-50 focus:ring-blue-400',
  };

  return (
    <button className={`${base} ${styles[variant]} ${className}`} {...rest}>
      {children}
    </button>
  );
}
