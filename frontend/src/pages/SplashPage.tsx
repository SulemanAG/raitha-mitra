/**
 * Splash & Welcome Page for Raitha Mitra.
 * 
 * @file src/pages/SplashPage.tsx
 * @description Initial presentation screen displaying app branding, tagline, and call-to-action to begin onboarding.
 */

import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { Sprout, ArrowRight, ShieldCheck } from 'lucide-react';
import { PageContainer } from '../components/common/PageContainer';
import { Button } from '../components/ui/Button';
import { Header } from '../components/common/Header';
import { ROUTES } from '../routes/routes.config';

export const SplashPage: React.FC = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();

  return (
    <div className="min-h-screen flex flex-col bg-gradient-to-b from-emerald-800 via-emerald-700 to-emerald-900 text-white">
      <Header hideLanguageSelector={false} />

      <PageContainer className="justify-between my-auto py-8">
        <div className="flex flex-col items-center text-center space-y-6 my-auto">
          {/* Animated Sprout Branding Icon */}
          <div className="w-24 h-24 rounded-3xl bg-white/10 backdrop-blur-md border border-white/20 flex items-center justify-center shadow-2xl animate-bounce-slow">
            <Sprout className="w-14 h-14 text-emerald-300" />
          </div>

          <div className="space-y-3 max-w-xs">
            <h1 className="text-3xl font-extrabold tracking-tight text-white leading-tight">
              {t('onboarding.splash.title')}
            </h1>
            <p className="text-base text-emerald-100/90 leading-relaxed font-normal">
              {t('onboarding.splash.subtitle')}
            </p>
          </div>

          <div className="inline-flex items-center space-x-2 px-3 py-1.5 rounded-full bg-white/10 border border-white/15 text-xs font-semibold text-emerald-200">
            <ShieldCheck className="w-4 h-4 text-emerald-300" />
            <span>Built for Farmers & Labourers</span>
          </div>
        </div>

        <div className="w-full pt-6">
          <Button
            onClick={() => navigate(ROUTES.LANGUAGE_SELECTION)}
            variant="secondary"
            size="lg"
            rightIcon={<ArrowRight className="w-5 h-5" />}
            className="shadow-xl"
          >
            {t('onboarding.splash.getStarted')}
          </Button>
        </div>
      </PageContainer>
    </div>
  );
};
