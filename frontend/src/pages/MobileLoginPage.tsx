/**
 * Mobile Number Login Screen for Raitha Mitra.
 * 
 * @file src/pages/MobileLoginPage.tsx
 * @description Form screen requiring a 10-digit Indian mobile number with +91 fixed badge
 * and RHF + Zod schema validation.
 */

import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { ArrowRight, Phone } from 'lucide-react';
import { PageContainer } from '../components/common/PageContainer';
import { Header } from '../components/common/Header';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { useOnboarding } from '../context/OnboardingContext';
import { createMobileNumberSchema, type MobileNumberFormData } from '../validators/auth.validator';
import { COUNTRY_CODE } from '../constants/app.constants';
import { ROUTES } from '../routes/routes.config';

export const MobileLoginPage: React.FC = () => {
  const navigate = useNavigate();
  const { t } = useTranslation(['common', 'onboarding', 'validation']);
  const { mobileNumber, setMobileNumber } = useOnboarding();

  const mobileSchema = createMobileNumberSchema((key) => t(key as unknown as TemplateStringsArray));

  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors, isSubmitting },
  } = useForm<MobileNumberFormData>({
    resolver: zodResolver(mobileSchema),
    defaultValues: {
      mobileNumber: mobileNumber || '',
    },
  });

  const onSubmit = (data: MobileNumberFormData) => {
    setMobileNumber(data.mobileNumber);
    navigate(ROUTES.OTP_VERIFICATION);
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    // Restrict input strictly to numeric digits, max 10 chars
    const sanitized = e.target.value.replace(/\D/g, '').slice(0, 10);
    setValue('mobileNumber', sanitized, { shouldValidate: true });
  };

  return (
    <div className="min-h-screen flex flex-col bg-slate-50">
      <Header showBack onBack={() => navigate(ROUTES.ROLE_SELECTION)} />

      <PageContainer>
        <div className="space-y-2">
          <h2 className="text-2xl font-bold text-slate-900 leading-tight">
            {t('onboarding:login.title')}
          </h2>
          <p className="text-sm text-slate-600 font-normal">
            {t('onboarding:login.subtitle')}
          </p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6 my-auto">
          <Input
            {...register('mobileNumber')}
            onChange={handleInputChange}
            label={t('common:labels.mobileNumber')}
            type="tel"
            inputMode="numeric"
            pattern="[0-9]*"
            maxLength={10}
            placeholder={t('onboarding:login.placeholder')}
            prefixBadge={COUNTRY_CODE}
            error={errors.mobileNumber?.message}
            leftIcon={<Phone className="w-5 h-5 text-slate-400" />}
            autoFocus
          />

          <div className="p-3.5 rounded-xl bg-amber-50 border border-amber-200 text-xs text-amber-900 leading-relaxed font-medium">
            {t('onboarding:login.disclaimer')}
          </div>

          <div className="pt-2">
            <Button
              type="submit"
              variant="primary"
              size="lg"
              isLoading={isSubmitting}
              rightIcon={<ArrowRight className="w-5 h-5" />}
            >
              {t('common:buttons.continue')}
            </Button>
          </div>
        </form>
      </PageContainer>
    </div>
  );
};
