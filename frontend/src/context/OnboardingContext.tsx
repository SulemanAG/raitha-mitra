/**
 * Onboarding State Context Provider for Raitha Mitra.
 * 
 * @file src/context/OnboardingContext.tsx
 * @description Provides reactive state management for language choice, role selection, mobile input,
 * and OTP verification status prior to Phase 2 backend authentication integration.
 */

import React, { createContext, useContext, useState, useEffect, type ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import type { SupportedLanguage } from '../types/language.types';
import type { UserRole } from '../types/role.types';
import type { OnboardingContextType, OnboardingState } from '../types/onboarding.types';
import {
  getStoredLanguage,
  getStoredOnboardingState,
  setStoredLanguage,
  setStoredOnboardingState,
  clearStoredOnboardingState,
} from '../utils/storage';

const OnboardingContext = createContext<OnboardingContextType | undefined>(undefined);

export const OnboardingProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const { i18n } = useTranslation();

  const [state, setState] = useState<OnboardingState>(() => {
    const storedLang = getStoredLanguage();
    const storedOnboarding = getStoredOnboardingState();

    return {
      selectedLanguage: storedLang,
      selectedRole: storedOnboarding?.selectedRole || null,
      mobileNumber: storedOnboarding?.mobileNumber || '',
      isOtpVerified: storedOnboarding?.isOtpVerified || false,
    };
  });

  // Sync state changes with localStorage
  useEffect(() => {
    setStoredOnboardingState({
      selectedRole: state.selectedRole,
      mobileNumber: state.mobileNumber,
      isOtpVerified: state.isOtpVerified,
    });
  }, [state.selectedRole, state.mobileNumber, state.isOtpVerified]);

  const setLanguage = (lang: SupportedLanguage) => {
    i18n.changeLanguage(lang);
    setStoredLanguage(lang);
    setState((prev) => ({ ...prev, selectedLanguage: lang }));
  };

  const setRole = (role: UserRole) => {
    setState((prev) => ({ ...prev, selectedRole: role }));
  };

  const setMobileNumber = (mobile: string) => {
    setState((prev) => ({ ...prev, mobileNumber: mobile }));
  };

  const verifyOtpPlaceholder = async (otp: string): Promise<boolean> => {
    // Phase 1 Frontend-only simulation delay (300ms)
    await new Promise((resolve) => setTimeout(resolve, 300));
    if (otp && otp.length === 6 && /^\d+$/.test(otp)) {
      setState((prev) => ({ ...prev, isOtpVerified: true }));
      return true;
    }
    return false;
  };

  const resetOnboarding = () => {
    clearStoredOnboardingState();
    setState({
      selectedLanguage: getStoredLanguage(),
      selectedRole: null,
      mobileNumber: '',
      isOtpVerified: false,
    });
  };

  return (
    <OnboardingContext.Provider
      value={{
        ...state,
        setLanguage,
        setRole,
        setMobileNumber,
        verifyOtpPlaceholder,
        resetOnboarding,
      }}
    >
      {children}
    </OnboardingContext.Provider>
  );
};

export const useOnboarding = (): OnboardingContextType => {
  const context = useContext(OnboardingContext);
  if (!context) {
    throw new Error('useOnboarding must be used within an OnboardingProvider');
  }
  return context;
};
