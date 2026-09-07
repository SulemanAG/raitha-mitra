/**
 * Reusable Accessible Input Component for Raitha Mitra.
 * 
 * @file src/components/ui/Input.tsx
 * @description Accessible text input component with label, error message, leading icon/badge (+91),
 * high contrast focus states, and minimum 48px touch targets.
 */

import React, { type InputHTMLAttributes, forwardRef } from 'react';
import { AlertCircle } from 'lucide-react';

export interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  helperText?: string;
  prefixBadge?: React.ReactNode;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
  (
    {
      label,
      error,
      helperText,
      prefixBadge,
      leftIcon,
      rightIcon,
      id,
      className = '',
      disabled,
      ...props
    },
    ref
  ) => {
    const inputId = id || (label ? label.toLowerCase().replace(/\s+/g, '-') : undefined);
    const errorId = inputId ? `${inputId}-error` : undefined;
    const helperId = inputId ? `${inputId}-helper` : undefined;

    return (
      <div className="w-full flex flex-col space-y-1.5">
        {label && (
          <label
            htmlFor={inputId}
            className="text-sm font-semibold text-slate-800 flex items-center justify-between"
          >
            <span>{label}</span>
          </label>
        )}

        <div className="relative flex items-center rounded-xl shadow-xs">
          {prefixBadge && (
            <div className="px-3.5 py-3 bg-slate-100 border border-r-0 border-slate-300 rounded-l-xl text-slate-700 font-bold text-base select-none flex items-center justify-center min-h-[48px]">
              {prefixBadge}
            </div>
          )}

          {leftIcon && !prefixBadge && (
            <div className="absolute left-3.5 text-slate-400 pointer-events-none flex items-center">
              {leftIcon}
            </div>
          )}

          <input
            ref={ref}
            id={inputId}
            disabled={disabled}
            aria-invalid={!!error}
            aria-describedby={
              error ? errorId : helperText ? helperId : undefined
            }
            className={`w-full min-h-[48px] px-4 py-3 text-base text-slate-900 bg-white border transition-colors duration-150
              ${prefixBadge ? 'rounded-r-xl rounded-l-none' : 'rounded-xl'}
              ${leftIcon && !prefixBadge ? 'pl-11' : ''}
              ${rightIcon ? 'pr-11' : ''}
              ${
                error
                  ? 'border-red-500 focus:border-red-600 focus:ring-2 focus:ring-red-200'
                  : 'border-slate-300 focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100'
              }
              disabled:bg-slate-100 disabled:text-slate-500 disabled:cursor-not-allowed
              ${className}
            `}
            {...props}
          />

          {rightIcon && (
            <div className="absolute right-3.5 text-slate-400 pointer-events-none flex items-center">
              {rightIcon}
            </div>
          )}
        </div>

        {error && (
          <div id={errorId} role="alert" className="flex items-center space-x-1.5 text-red-600 text-sm font-medium pt-0.5">
            <AlertCircle className="w-4 h-4 shrink-0" />
            <span>{error}</span>
          </div>
        )}

        {helperText && !error && (
          <p id={helperId} className="text-xs text-slate-500 pt-0.5">
            {helperText}
          </p>
        )}
      </div>
    );
  }
);

Input.displayName = 'Input';
