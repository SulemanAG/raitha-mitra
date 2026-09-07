/**
 * Reusable Accessible Button Component for Raitha Mitra.
 * 
 * @file src/components/ui/Button.tsx
 * @description Mobile-first button primitive with size variants, loading states, high contrast focus rings,
 * and minimum 48px touch targets for agricultural users.
 */

import React, { type ButtonHTMLAttributes } from 'react';
import { LoadingIndicator } from './LoadingIndicator';

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  fullWidth?: boolean;
  isLoading?: boolean;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
}

export const Button: React.FC<ButtonProps> = ({
  children,
  variant = 'primary',
  size = 'lg',
  fullWidth = true,
  isLoading = false,
  leftIcon,
  rightIcon,
  disabled,
  className = '',
  ...props
}) => {
  // Base classes ensure 48px minimum touch target (WCAG 2.1 AA)
  const baseClasses =
    'inline-flex items-center justify-center font-medium rounded-xl transition-all duration-150 focus-visible:outline-2 focus-visible:outline-offset-2 disabled:opacity-60 disabled:cursor-not-allowed touch-target active:scale-[0.98] select-none';

  const variantClasses = {
    primary: 'bg-emerald-700 text-white hover:bg-emerald-800 focus-visible:outline-emerald-700 shadow-sm border border-transparent',
    secondary: 'bg-amber-600 text-white hover:bg-amber-700 focus-visible:outline-amber-600 shadow-sm border border-transparent',
    outline: 'bg-white text-emerald-800 border-2 border-emerald-700 hover:bg-emerald-50 focus-visible:outline-emerald-700',
    ghost: 'bg-transparent text-slate-700 hover:bg-slate-100 focus-visible:outline-slate-600',
    danger: 'bg-red-600 text-white hover:bg-red-700 focus-visible:outline-red-600 shadow-sm',
  };

  const sizeClasses = {
    sm: 'text-sm px-3 py-2 min-h-[40px]',
    md: 'text-base px-4 py-3 min-h-[48px]',
    lg: 'text-lg px-6 py-3.5 min-h-[52px] font-semibold',
  };

  const widthClass = fullWidth ? 'w-full' : 'w-auto';

  return (
    <button
      disabled={disabled || isLoading}
      className={`${baseClasses} ${variantClasses[variant]} ${sizeClasses[size]} ${widthClass} ${className}`}
      {...props}
    >
      {isLoading ? (
        <LoadingIndicator size="sm" color={variant === 'outline' || variant === 'ghost' ? 'emerald' : 'white'} />
      ) : (
        <>
          {leftIcon && <span className="mr-2.5 inline-flex items-center">{leftIcon}</span>}
          <span>{children}</span>
          {rightIcon && <span className="ml-2.5 inline-flex items-center">{rightIcon}</span>}
        </>
      )}
    </button>
  );
};
