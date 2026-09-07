/**
 * OTP Verification Placeholder Page for Raitha Mitra.
 * 
 * @file src/pages/OtpVerificationPage.tsx
 * @description Accessible 6-digit OTP input with auto-advance, backspace navigation, paste handling,
 * resend cooldown timer, and clear Phase 1 simulation indicators.
 */

import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { CheckCircle, Info, RefreshCw } from 'lucide-react';
import { PageContainer } from '../components/common/PageContainer';
import { Header } from '../components/common/Header';
import { Button } from '../components/ui/Button';
import { ErrorMessage } from '../components/ui/ErrorMessage';
import { useOnboarding } from '../context/OnboardingContext';
import { COUNTRY_CODE, OTP_LENGTH } from '../constants/app.constants';
import { ROUTES } from '../routes/routes.config';
import { authApi } from '../api/authApi';

export const OtpVerificationPage: React.FC = () => {
  const navigate = useNavigate();
  const { t } = useTranslation(['common', 'onboarding', 'validation', 'errors']);
  const { mobileNumber, verifyOtpPlaceholder } = useOnboarding();

  const [otpDigits, setOtpDigits] = useState<string[]>(Array(OTP_LENGTH).fill(''));
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [isVerifying, setIsVerifying] = useState(false);

  // 30 second resend timer simulation
  const [cooldown, setCooldown] = useState<number>(30);
  const inputRefs = useRef<(HTMLInputElement | null)[]>([]);

  useEffect(() => {
    let timer: ReturnType<typeof setInterval>;
    if (cooldown > 0) {
      timer = setInterval(() => {
        setCooldown((prev) => prev - 1);
      }, 1000);
    }
    return () => clearInterval(timer);
  }, [cooldown]);

  const handleChange = (index: number, value: string) => {
    // Only accept numeric digits
    const sanitized = value.replace(/\D/g, '');
    if (!sanitized && value !== '') return;

    const newDigits = [...otpDigits];
    newDigits[index] = sanitized.slice(-1); // Take latest char
    setOtpDigits(newDigits);
    setErrorMsg(null);

    // Auto-advance to next input field
    if (sanitized && index < OTP_LENGTH - 1) {
      inputRefs.current[index + 1]?.focus();
    }
  };

  const handleKeyDown = (index: number, e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Backspace') {
      if (!otpDigits[index] && index > 0) {
        // Move focus backward if current cell is empty
        inputRefs.current[index - 1]?.focus();
      }
    }
  };

  const handlePaste = (e: React.ClipboardEvent<HTMLInputElement>) => {
    e.preventDefault();
    const pastedData = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, OTP_LENGTH);
    if (pastedData) {
      const digits = pastedData.split('');
      const newDigits = Array(OTP_LENGTH).fill('');
      digits.forEach((digit, i) => {
        newDigits[i] = digit;
      });
      setOtpDigits(newDigits);
      setErrorMsg(null);

      // Focus last populated index or last digit
      const nextFocus = Math.min(digits.length, OTP_LENGTH - 1);
      inputRefs.current[nextFocus]?.focus();
    }
  };

  const handleVerify = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    const fullOtp = otpDigits.join('');

    if (fullOtp.length !== OTP_LENGTH) {
      setErrorMsg(t('validation:otp.invalidLength'));
      return;
    }

    setIsVerifying(true);
    setErrorMsg(null);

    const formattedMobile = `${COUNTRY_CODE}${mobileNumber}`;

    try {
      const sessionRes = await authApi.verifyOtp(formattedMobile, fullOtp);
      if (sessionRes.accessToken) {
        localStorage.setItem('accessToken', sessionRes.accessToken);
        sessionStorage.setItem('accessToken', sessionRes.accessToken);
        setIsVerifying(false);
        navigate(ROUTES.DASHBOARD_PLACEHOLDER);
        return;
      }
    } catch (err: unknown) {
      console.warn('Backend verify-otp error:', err);
    }

    // Fallback simulation
    const success = await verifyOtpPlaceholder(fullOtp);
    setIsVerifying(false);

    if (success) {
      navigate(ROUTES.DASHBOARD_PLACEHOLDER);
    } else {
      setErrorMsg(t('errors:generic'));
    }
  };

  const handleResend = () => {
    if (cooldown === 0) {
      setCooldown(30);
      setOtpDigits(Array(OTP_LENGTH).fill(''));
      setErrorMsg(null);
      inputRefs.current[0]?.focus();
    }
  };

  return (
    <div className="min-h-screen flex flex-col bg-slate-50">
      <Header showBack onBack={() => navigate(ROUTES.MOBILE_LOGIN)} />

      <PageContainer>
        <div className="space-y-2">
          <h2 className="text-2xl font-bold text-slate-900 leading-tight">
            {t('onboarding:otp.title')}
          </h2>
          <div className="text-sm text-slate-600 font-normal space-y-1">
            <p>{t('onboarding:otp.subtitle')}</p>
            <p className="font-bold text-emerald-800 text-base">
              {COUNTRY_CODE} {mobileNumber}
            </p>
            <button
              type="button"
              onClick={() => navigate(ROUTES.MOBILE_LOGIN)}
              className="text-xs text-emerald-700 font-semibold underline hover:text-emerald-900 focus-visible:outline-2"
            >
              {t('onboarding:otp.changeNumber')}
            </button>
          </div>
        </div>

        <form onSubmit={handleVerify} className="space-y-6 my-auto">
          {/* 6 Digit Input Group */}
          <div className="flex items-center justify-between gap-2 my-2">
            {otpDigits.map((digit, idx) => (
              <input
                key={idx}
                ref={(el) => {
                  inputRefs.current[idx] = el;
                }}
                type="text"
                inputMode="numeric"
                pattern="[0-9]*"
                maxLength={1}
                value={digit}
                autoFocus={idx === 0}
                onChange={(e) => handleChange(idx, e.target.value)}
                onKeyDown={(e) => handleKeyDown(idx, e)}
                onPaste={handlePaste}
                aria-label={`OTP Digit ${idx + 1}`}
                className="w-12 h-14 sm:w-13 sm:h-16 text-center text-2xl font-bold text-slate-900 bg-white border-2 border-slate-300 rounded-xl focus:border-emerald-700 focus:ring-2 focus:ring-emerald-200 outline-hidden transition-colors shadow-xs"
              />
            ))}
          </div>

          {errorMsg && <ErrorMessage message={errorMsg} />}

          {/* Phase 1 Simulation Note Banner */}
          <div className="p-3.5 rounded-xl bg-emerald-50 border border-emerald-200 text-xs text-emerald-900 flex items-start space-x-2.5">
            <Info className="w-5 h-5 text-emerald-700 shrink-0 mt-0.5" />
            <p className="leading-relaxed font-medium">
              {t('onboarding:otp.simNote')}
            </p>
          </div>

          {/* Resend Action */}
          <div className="text-center pt-1">
            {cooldown > 0 ? (
              <p className="text-sm text-slate-500 font-medium">
                {t('onboarding:otp.resendCooldown', { seconds: cooldown })}
              </p>
            ) : (
              <button
                type="button"
                onClick={handleResend}
                className="inline-flex items-center space-x-1.5 text-sm font-bold text-emerald-700 hover:text-emerald-900 focus-visible:outline-2"
              >
                <RefreshCw className="w-4 h-4" />
                <span>{t('common:buttons.resend')}</span>
              </button>
            )}
          </div>

          <div className="pt-2">
            <Button
              type="submit"
              variant="primary"
              size="lg"
              isLoading={isVerifying}
              rightIcon={<CheckCircle className="w-5 h-5" />}
            >
              {t('common:buttons.verify')}
            </Button>
          </div>
        </form>
      </PageContainer>
    </div>
  );
};
