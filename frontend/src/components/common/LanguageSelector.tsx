/**
 * Accessible Language Selector Component for Raitha Mitra.
 * 
 * @file src/components/common/LanguageSelector.tsx
 * @description Language switcher allowing dynamic switching between English, Kannada, and Hindi
 * with persistent localStorage storage and reactive i18n update.
 */

import React, { useState, useRef, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { Globe, Check, ChevronDown } from 'lucide-react';
import { SUPPORTED_LANGUAGES } from '../../constants/app.constants';
import type { SupportedLanguage } from '../../types/language.types';
import { setStoredLanguage } from '../../utils/storage';

export const LanguageSelector: React.FC = () => {
  const { i18n, t } = useTranslation();
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const currentLangCode = (i18n.language || 'en') as SupportedLanguage;
  const currentLang =
    SUPPORTED_LANGUAGES.find((item) => item.code === currentLangCode) || SUPPORTED_LANGUAGES[0];

  const handleLanguageSelect = (code: SupportedLanguage) => {
    i18n.changeLanguage(code);
    setStoredLanguage(code);
    setIsOpen(false);
  };

  // Close dropdown on outside click or Escape key
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };

    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        setIsOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    document.addEventListener('keydown', handleKeyDown);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, []);

  return (
    <div ref={dropdownRef} className="relative inline-block text-left">
      <button
        type="button"
        onClick={() => setIsOpen(!isOpen)}
        aria-expanded={isOpen}
        aria-haspopup="listbox"
        aria-label={t('common.buttons.changeLanguage')}
        className="inline-flex items-center space-x-2 px-3 py-2 bg-white border border-slate-300 rounded-xl text-sm font-semibold text-slate-800 shadow-xs hover:bg-slate-50 focus-visible:outline-2 focus-visible:outline-emerald-700 touch-target"
      >
        <Globe className="w-4 h-4 text-emerald-700 shrink-0" />
        <span className="font-medium text-slate-900">{currentLang.name}</span>
        <ChevronDown className={`w-4 h-4 text-slate-500 transition-transform ${isOpen ? 'rotate-180' : ''}`} />
      </button>

      {isOpen && (
        <div
          role="listbox"
          aria-label={t('common.labels.selectLanguage')}
          className="absolute right-0 mt-2 w-48 bg-white border border-slate-200 rounded-2xl shadow-lg z-50 py-1.5 focus:outline-hidden"
        >
          {SUPPORTED_LANGUAGES.map((lang) => {
            const isSelected = lang.code === currentLangCode;
            return (
              <button
                key={lang.code}
                role="option"
                aria-selected={isSelected}
                onClick={() => handleLanguageSelect(lang.code)}
                className={`w-full px-4 py-3 text-left flex items-center justify-between text-base font-medium transition-colors touch-target ${
                  isSelected
                    ? 'bg-emerald-50 text-emerald-800 font-bold'
                    : 'text-slate-700 hover:bg-slate-50'
                }`}
              >
                <div className="flex flex-col">
                  <span>{lang.name}</span>
                  {lang.code !== 'en' && (
                    <span className="text-xs text-slate-500 font-normal">{lang.englishName}</span>
                  )}
                </div>
                {isSelected && <Check className="w-5 h-5 text-emerald-700 shrink-0" />}
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
};
