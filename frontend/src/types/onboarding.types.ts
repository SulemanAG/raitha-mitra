/**
 * Onboarding State domain types for Raitha Mitra.
 * 
 * @file src/types/onboarding.types.ts
 * @description State interfaces for the onboarding workflow prior to real authentication.
 */

import type { SupportedLanguage } from './language.types';
import type { UserRole } from './role.types';

export interface OnboardingState {
  selectedLanguage: SupportedLanguage;
  selectedRole: UserRole | null;
  mobileNumber: string;
  isOtpVerified: boolean;
}

export interface OnboardingContextType extends OnboardingState {
  setLanguage: (lang: SupportedLanguage) => void;
  setRole: (role: UserRole) => void;
  setMobileNumber: (mobile: string) => void;
  verifyOtpPlaceholder: (otp: string) => Promise<boolean>;
  resetOnboarding: () => void;
}
