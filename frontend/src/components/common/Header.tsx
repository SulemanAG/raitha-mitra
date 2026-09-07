/**
 * Mobile-First Application Header Component for Raitha Mitra.
 * 
 * @file src/components/common/Header.tsx
 * @description Header banner providing back navigation, app branding, and language selection.
 */

import React from 'react';
import { ArrowLeft, Sprout } from 'lucide-react';
import { useTranslation } from 'react-i18next';
import { LanguageSelector } from './LanguageSelector';
import { IconButton } from '../ui/IconButton';

export interface HeaderProps {
  showBack?: boolean;
  onBack?: () => void;
  title?: string;
  hideLanguageSelector?: boolean;
}

export const Header: React.FC<HeaderProps> = ({
  showBack = false,
  onBack,
  title,
  hideLanguageSelector = false,
}) => {
  const { t } = useTranslation();

  return (
    <header className="sticky top-0 z-40 bg-white/95 backdrop-blur-xs border-b border-slate-200 px-4 py-3 shadow-2xs">
      <div className="max-w-md mx-auto flex items-center justify-between">
        <div className="flex items-center space-x-3">
          {showBack && onBack ? (
            <IconButton
              aria-label={t('common.buttons.back')}
              onClick={onBack}
              size="md"
              variant="ghost"
              className="-ml-2 text-slate-700 hover:text-slate-900"
            >
              <ArrowLeft className="w-6 h-6" />
            </IconButton>
          ) : (
            <div className="w-10 h-10 rounded-xl bg-emerald-700 text-white flex items-center justify-center shadow-xs shrink-0">
              <Sprout className="w-6 h-6" />
            </div>
          )}

          <div>
            <h1 className="text-lg font-bold text-slate-900 leading-tight">
              {title || t('appName')}
            </h1>
            {!title && (
              <p className="text-xs text-slate-500 font-medium hidden sm:block">
                {t('appTagline')}
              </p>
            )}
          </div>
        </div>

        {!hideLanguageSelector && <LanguageSelector />}
      </div>
    </header>
  );
};
