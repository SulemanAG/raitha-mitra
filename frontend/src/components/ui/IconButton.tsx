/**
 * Reusable Accessible Icon Button Component for Raitha Mitra.
 * 
 * @file src/components/ui/IconButton.tsx
 * @description Icon button wrapper enforcing aria-label for accessibility.
 */

import React, { type ButtonHTMLAttributes } from 'react';

export interface IconButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  'aria-label': string; // Mandatory for accessibility
  variant?: 'ghost' | 'outline' | 'solid';
  size?: 'sm' | 'md' | 'lg';
}

export const IconButton: React.FC<IconButtonProps> = ({
  children,
  'aria-label': ariaLabel,
  variant = 'ghost',
  size = 'md',
  className = '',
  ...props
}) => {
  const baseClasses =
    'inline-flex items-center justify-center rounded-xl transition-all duration-150 focus-visible:outline-2 focus-visible:outline-emerald-700 disabled:opacity-50 disabled:cursor-not-allowed touch-target active:scale-95';

  const variantClasses = {
    ghost: 'bg-transparent text-slate-700 hover:bg-slate-100',
    outline: 'bg-white border border-slate-300 text-slate-700 hover:bg-slate-50',
    solid: 'bg-emerald-700 text-white hover:bg-emerald-800',
  };

  const sizeClasses = {
    sm: 'w-9 h-9 min-w-[36px] min-h-[36px]',
    md: 'w-12 h-12 min-w-[48px] min-h-[48px]',
    lg: 'w-14 h-14 min-w-[56px] min-h-[56px]',
  };

  return (
    <button
      aria-label={ariaLabel}
      className={`${baseClasses} ${variantClasses[variant]} ${sizeClasses[size]} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
};
