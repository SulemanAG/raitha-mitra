/**
 * Reusable Selectable Card Component for Raitha Mitra.
 * 
 * @file src/components/ui/Card.tsx
 * @description Accessible interactive card container supporting selection state, high-contrast borders,
 * touch-friendly padding, and keyboard interaction (Space/Enter key selection).
 */

import React, { type HTMLAttributes } from 'react';

export interface CardProps extends HTMLAttributes<HTMLDivElement> {
  selected?: boolean;
  clickable?: boolean;
  disabled?: boolean;
}

export const Card: React.FC<CardProps> = ({
  children,
  selected = false,
  clickable = true,
  disabled = false,
  className = '',
  onClick,
  onKeyDown,
  ...props
}) => {
  const handleKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (clickable && !disabled && (e.key === 'Enter' || e.key === ' ')) {
      e.preventDefault();
      onClick?.(e as unknown as React.MouseEvent<HTMLDivElement>);
    }
    onKeyDown?.(e);
  };

  return (
    <div
      role={clickable ? 'button' : undefined}
      tabIndex={clickable && !disabled ? 0 : undefined}
      aria-pressed={clickable ? selected : undefined}
      aria-disabled={disabled}
      onClick={disabled ? undefined : onClick}
      onKeyDown={handleKeyDown}
      className={`relative p-5 rounded-2xl border-2 transition-all duration-150 select-none
        ${
          selected
            ? 'border-emerald-700 bg-emerald-50/70 shadow-md ring-2 ring-emerald-600/30'
            : 'border-slate-200 bg-white hover:border-slate-300 shadow-xs'
        }
        ${
          clickable && !disabled
            ? 'cursor-pointer active:scale-[0.99] focus-visible:outline-2 focus-visible:outline-emerald-700'
            : ''
        }
        ${disabled ? 'opacity-50 cursor-not-allowed' : ''}
        ${className}
      `}
      {...props}
    >
      {children}
    </div>
  );
};
