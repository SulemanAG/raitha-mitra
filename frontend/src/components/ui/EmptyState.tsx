/**
 * Reusable Accessible Empty State View for Raitha Mitra.
 * 
 * @file src/components/ui/EmptyState.tsx
 * @description Accessible placeholder component for empty states or missing selections.
 */

import React from 'react';
import { Inbox } from 'lucide-react';
import { Button } from './Button';

export interface EmptyStateProps {
  title: string;
  description?: string;
  icon?: React.ReactNode;
  actionLabel?: string;
  onAction?: () => void;
}

export const EmptyState: React.FC<EmptyStateProps> = ({
  title,
  description,
  icon,
  actionLabel,
  onAction,
}) => {
  return (
    <div className="flex flex-col items-center justify-center p-8 text-center bg-slate-50 border-2 border-dashed border-slate-200 rounded-2xl my-4">
      <div className="w-16 h-16 rounded-full bg-emerald-50 text-emerald-700 flex items-center justify-center mb-4">
        {icon || <Inbox className="w-8 h-8" />}
      </div>
      <h3 className="text-lg font-bold text-slate-800">{title}</h3>
      {description && <p className="text-sm text-slate-600 max-w-xs mt-1.5">{description}</p>}
      {actionLabel && onAction && (
        <Button onClick={onAction} variant="outline" size="sm" className="mt-5 w-auto">
          {actionLabel}
        </Button>
      )}
    </div>
  );
};
