/**
 * Mobile-First Page Container Component for Raitha Mitra.
 * 
 * @file src/components/common/PageContainer.tsx
 * @description Accessible page layout container ensuring mobile-first viewport constraints (max-w-md),
 * padding, and flex alignment.
 */

import React, { type ReactNode } from 'react';

export interface PageContainerProps {
  children: ReactNode;
  className?: string;
}

export const PageContainer: React.FC<PageContainerProps> = ({ children, className = '' }) => {
  return (
    <main className="w-full flex-1 flex flex-col justify-between p-4 sm:p-6 max-w-md mx-auto min-h-[calc(100vh-65px)]">
      <div className={`w-full flex flex-col space-y-6 ${className}`}>{children}</div>
    </main>
  );
};
