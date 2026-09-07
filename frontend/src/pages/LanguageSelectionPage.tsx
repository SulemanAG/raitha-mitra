/**
 * Language Selection Page for Raitha Mitra.
 * 
 * @file src/pages/LanguageSelectionPage.tsx
 * @description Primary choice screen allowing users to pick English, Kannada, or Hindi.
 */

import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { ArrowRight, Check } from 'lucide-react';
import { PageContainer } from '../components/common/PageContainer';
import { Header } from '../components/common/Header';
import { Button } from '../components/ui/Button';
import { Card } from '../components/ui/Card';
import { useOnboarding } from '../context/OnboardingContext';
import { SUPPORTED_LANGUAGES } from '../constants/app.constants';
import type { SupportedLanguage } from '../types/language.types';
import { ROUTES } from '../routes/routes.config';

export const LanguageSelectionPage: React.FC = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const { selectedLanguage, setLanguage } = useOnboarding();

  const handleSelect = (code: SupportedLanguage) => {
    setLanguage(code);
  };

  const handleContinue = () => {
    navigate(ROUTES.ROLE_SELECTION);
  };

  return (
    <div className="min-h-screen flex flex-col bg-slate-50">
      <Header showBack onBack={() => navigate(ROUTES.SPLASH)} />

      <PageContainer>
        <div className="space-y-2">
          <h2 className="text-2xl font-bold text-slate-900 leading-tight">
            {t('onboarding.language.title')}
          </h2>
          <p className="text-sm text-slate-600 font-normal">
            {t('onboarding.language.subtitle')}
          </p>
        </div>

        <div className="space-y-3.5 my-auto">
          {SUPPORTED_LANGUAGES.map((lang) => {
            const isSelected = selectedLanguage === lang.code;
            return (
              <Card
                key={lang.code}
                selected={isSelected}
                onClick={() => handleSelect(lang.code)}
                className="p-4 flex items-center justify-between"
              >
                <div className="flex items-center space-x-4">
                  <div
                    className={`w-12 h-12 rounded-xl flex items-center justify-center font-bold text-lg ${
                      isSelected
                        ? 'bg-emerald-700 text-white'
                        : 'bg-slate-100 text-slate-700'
                    }`}
                  >
                    {lang.flagSymbol}
                  </div>

                  <div>
                    <h3 className="text-lg font-bold text-slate-900">{lang.name}</h3>
                    {lang.code !== 'en' && (
                      <p className="text-xs text-slate-500 font-medium">{lang.englishName}</p>
                    )}
                  </div>
                </div>

                <div
                  className={`w-6 h-6 rounded-full border-2 flex items-center justify-center ${
                    isSelected
                      ? 'border-emerald-700 bg-emerald-700 text-white'
                      : 'border-slate-300'
                  }`}
                >
                  {isSelected && <Check className="w-4 h-4" />}
                </div>
              </Card>
            );
          })}
        </div>

        <div className="pt-4">
          <Button
            onClick={handleContinue}
            variant="primary"
            size="lg"
            rightIcon={<ArrowRight className="w-5 h-5" />}
          >
            {t('common.buttons.continue')}
          </Button>
        </div>
      </PageContainer>
    </div>
  );
};
