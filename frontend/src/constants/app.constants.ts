/**
 * Application Constants for Raitha Mitra.
 * 
 * @file src/constants/app.constants.ts
 * @description Central constant definitions for languages, roles, validation rules, and storage keys.
 */

import type { LanguageOption, SupportedLanguage } from '../types/language.types';
import type { UserRoleOption } from '../types/role.types';

export const DEFAULT_LANGUAGE: SupportedLanguage = 'en';

export const SUPPORTED_LANGUAGES: LanguageOption[] = [
  {
    code: 'en',
    name: 'English',
    englishName: 'English',
    flagSymbol: 'EN',
  },
  {
    code: 'kn',
    name: 'ಕನ್ನಡ',
    englishName: 'Kannada',
    flagSymbol: 'KN',
  },
  {
    code: 'hi',
    name: 'हिंदी',
    englishName: 'Hindi',
    flagSymbol: 'HI',
  },
];

export const USER_ROLES: UserRoleOption[] = [
  {
    id: 'FARMER',
    iconName: 'Tractor',
    translationKey: 'onboarding.roles.farmer',
  },
  {
    id: 'LABOURER',
    iconName: 'UserCheck',
    translationKey: 'onboarding.roles.labourer',
  },
];

export const COUNTRY_CODE = '+91';
export const MOBILE_NUMBER_LENGTH = 10;
export const OTP_LENGTH = 6;

// Valid Indian mobile numbers start with 6, 7, 8, or 9 followed by 9 digits
export const INDIAN_MOBILE_REGEX = /^[6-9]\d{9}$/;

export const STORAGE_KEYS = {
  LANGUAGE: 'raitha_mitra_lang',
  ONBOARDING_STATE: 'raitha_mitra_onboarding_state',
} as const;
