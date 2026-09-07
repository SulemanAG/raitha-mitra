/**
 * Reusable Accessible Loading Indicator Component for Raitha Mitra.
 * 
 * @file src/components/ui/LoadingIndicator.tsx
 * @description Accessible loading spinner with role="status" and screen-reader label.
 */

import React from 'react';

export interface LoadingIndicatorProps {
  size?: 'sm' | 'md' | 'lg';
  color?: 'white' | 'emerald' | 'amber' | 'slate';
  label?: string;
}

export const LoadingIndicator: React.FC<LoadingIndicatorProps> = ({
  size = 'md',
  color = 'emerald',
  label = 'Loading...',
}) => {
  const sizeClasses = {
    sm: 'w-4 h-4 border-2',
    md: 'w-6 h-6 border-3',
    lg: 'w-10 h-10 border-4',
  };

  const colorClasses = {
    white: 'border-white/30 border-t-white',
    emerald: 'border-emerald-200 border-t-emerald-700',
    amber: 'border-amber-200 border-t-amber-600',
    slate: 'border-slate-300 border-t-slate-700',
  };

  return (
    <div role="status" className="inline-flex items-center justify-center">
      <div
        className={`animate-spin rounded-full ${sizeClasses[size]} ${colorClasses[color]}`}
      />
      <span className="sr-only">{label}</span>
    </div>
  );
};
