/**
 * Dashboard Placeholder & Completion Page for Raitha Mitra.
 * 
 * @file src/pages/DashboardPlaceholderPage.tsx
 * @description Onboarding completion screen summarizing selected role, mobile number, and chosen language,
 * preparing the application for Phase 2 backend authentication integration.
 */

import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { CheckCircle2, RotateCcw, Tractor, Users, Globe, Phone, ShieldCheck } from 'lucide-react';
import { PageContainer } from '../components/common/PageContainer';
import { Header } from '../components/common/Header';
import { Button } from '../components/ui/Button';
import { useOnboarding } from '../context/OnboardingContext';
import { COUNTRY_CODE, SUPPORTED_LANGUAGES } from '../constants/app.constants';
import { ROUTES } from '../routes/routes.config';

export const DashboardPlaceholderPage: React.FC = () => {
  const navigate = useNavigate();
  const { t, i18n } = useTranslation(['common', 'onboarding']);
  const { selectedRole, mobileNumber, resetOnboarding } = useOnboarding();

  const currentLangObj = SUPPORTED_LANGUAGES.find((item) => item.code === i18n.language) || SUPPORTED_LANGUAGES[0];
  const RoleIcon = selectedRole === 'FARMER' ? Tractor : Users;

  const handleRestart = () => {
    resetOnboarding();
    navigate(ROUTES.SPLASH);
  };

  return (
    <div className="min-h-screen flex flex-col bg-slate-50">
      <Header />

      <PageContainer>
        <div className="flex flex-col items-center text-center space-y-4 my-auto">
          <div className="w-20 h-20 rounded-full bg-emerald-100 text-emerald-700 flex items-center justify-center shadow-inner">
            <CheckCircle2 className="w-12 h-12" />
          </div>

          <div className="space-y-2">
            <h2 className="text-2xl font-extrabold text-slate-900 leading-tight">
              {t('onboarding:complete.title')}
            </h2>
            <p className="text-sm text-slate-600 leading-relaxed max-w-xs">
              {t('onboarding:complete.subtitle')}
            </p>
          </div>

          {/* Onboarding Summary Card */}
          <div className="w-full bg-white border border-slate-200 rounded-2xl p-5 shadow-xs space-y-4 text-left my-4">
            <div className="flex items-center space-x-3 pb-3 border-b border-slate-100">
              <div className="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-700 flex items-center justify-center shrink-0">
                <RoleIcon className="w-6 h-6" />
              </div>
              <div>
                <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                  {t('onboarding:complete.roleSelected')}
                </p>
                <p className="text-base font-bold text-slate-900">
                  {selectedRole === 'FARMER'
                    ? t('onboarding:roles.farmer.title')
                    : t('onboarding:roles.labourer.title')}
                </p>
              </div>
            </div>

            <div className="flex items-center space-x-3 pb-3 border-b border-slate-100">
              <div className="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-700 flex items-center justify-center shrink-0">
                <Phone className="w-5 h-5" />
              </div>
              <div>
                <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                  {t('onboarding:complete.mobileEntered')}
                </p>
                <p className="text-base font-bold text-slate-900">
                  {COUNTRY_CODE} {mobileNumber}
                </p>
              </div>
            </div>

            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-700 flex items-center justify-center shrink-0">
                <Globe className="w-5 h-5" />
              </div>
              <div>
                <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                  {t('onboarding:complete.languageSelected')}
                </p>
                <p className="text-base font-bold text-slate-900">
                  {currentLangObj.name} ({currentLangObj.englishName})
                </p>
              </div>
            </div>
          </div>

          <div className="p-3.5 rounded-xl bg-blue-50 border border-blue-200 text-xs text-blue-900 flex items-start space-x-2 text-left">
            <ShieldCheck className="w-5 h-5 text-blue-700 shrink-0 mt-0.5" />
            <p className="leading-relaxed">
              <strong>Phase 1 Architecture Complete:</strong> React + TypeScript, Vite, i18next (EN/KN/HI), mobile-first layout, and RHF + Zod validation foundation are active. Phase 2 will introduce Spring Boot REST API integration and JWT security.
            </p>
          </div>
        </div>

        <div className="pt-4">
          <Button
            onClick={handleRestart}
            variant="outline"
            size="lg"
            leftIcon={<RotateCcw className="w-5 h-5" />}
          >
            Restart Onboarding Test
          </Button>
        </div>
      </PageContainer>
    </div>
  );
};
