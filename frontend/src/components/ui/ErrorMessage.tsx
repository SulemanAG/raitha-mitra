/**
 * Reusable Accessible Error Message Banner for Raitha Mitra.
 * 
 * @file src/components/ui/ErrorMessage.tsx
 * @description Accessible banner component displaying sanitized localized error messages.
 */

import React from 'react';
import { AlertTriangle } from 'lucide-react';

export interface ErrorMessageProps {
  message: string;
  title?: string;
  onRetry?: () => void;
  retryLabel?: string;
}

export const ErrorMessage: React.FC<ErrorMessageProps> = ({
  message,
  title,
  onRetry,
  retryLabel = 'Retry',
}) => {
  return (
    <div
      role="alert"
      className="p-4 rounded-xl bg-red-50 border border-red-200 text-red-800 flex items-start space-x-3 my-2"
    >
      <AlertTriangle className="w-5 h-5 text-red-600 shrink-0 mt-0.5" />
      <div className="flex-1 text-sm">
        {title && <h4 className="font-bold text-red-900 mb-0.5">{title}</h4>}
        <p className="font-medium">{message}</p>
        {onRetry && (
          <button
            onClick={onRetry}
            className="mt-2 text-xs font-bold text-red-700 underline hover:text-red-900 focus-visible:outline-2"
          >
            {retryLabel}
          </button>
        )}
      </div>
    </div>
  );
};
