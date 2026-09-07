/**
 * Role Selection Page for Raitha Mitra.
 * 
 * @file src/pages/RoleSelectionPage.tsx
 * @description Role choice screen allowing user to pick Farmer or Labourer domain role.
 */

import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { ArrowRight } from 'lucide-react';
import { PageContainer } from '../components/common/PageContainer';
import { Header } from '../components/common/Header';
import { Button } from '../components/ui/Button';
import { UserTypeCard } from '../components/common/UserTypeCard';
import { useOnboarding } from '../context/OnboardingContext';
import type { UserRole } from '../types/role.types';
import { ROUTES } from '../routes/routes.config';

export const RoleSelectionPage: React.FC = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const { selectedRole, setRole } = useOnboarding();

  const handleRoleSelect = (role: UserRole) => {
    setRole(role);
  };

  const handleContinue = () => {
    if (selectedRole) {
      navigate(ROUTES.MOBILE_LOGIN);
    }
  };

  return (
    <div className="min-h-screen flex flex-col bg-slate-50">
      <Header showBack onBack={() => navigate(ROUTES.LANGUAGE_SELECTION)} />

      <PageContainer>
        <div className="space-y-2">
          <h2 className="text-2xl font-bold text-slate-900 leading-tight">
            {t('onboarding.roles.title')}
          </h2>
          <p className="text-sm text-slate-600 font-normal">
            {t('onboarding.roles.subtitle')}
          </p>
        </div>

        <div className="space-y-4 my-auto">
          <UserTypeCard
            role="FARMER"
            title={t('onboarding.roles.farmer.title')}
            description={t('onboarding.roles.farmer.description')}
            selected={selectedRole === 'FARMER'}
            onSelect={handleRoleSelect}
          />

          <UserTypeCard
            role="LABOURER"
            title={t('onboarding.roles.labourer.title')}
            description={t('onboarding.roles.labourer.description')}
            selected={selectedRole === 'LABOURER'}
            onSelect={handleRoleSelect}
          />
        </div>

        <div className="pt-4">
          <Button
            disabled={!selectedRole}
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
