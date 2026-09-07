/**
 * i18next Configuration Initialization for Raitha Mitra.
 * 
 * @file src/locales/i18n.ts
 * @description Configures react-i18next with English, Kannada, and Hindi resource bundles,
 * namespace separation, fallback strategy, and localStorage language preference initialization.
 */

import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import { DEFAULT_LANGUAGE } from '../constants/app.constants';
import { getStoredLanguage } from '../utils/storage';

import commonEn from './en/common.json';
import errorsEn from './en/errors.json';
import machineryEn from './en/machinery.json';
import onboardingEn from './en/onboarding.json';
import validationEn from './en/validation.json';

import commonKn from './kn/common.json';
import errorsKn from './kn/errors.json';
import machineryKn from './kn/machinery.json';
import onboardingKn from './kn/onboarding.json';
import validationKn from './kn/validation.json';

import commonHi from './hi/common.json';
import errorsHi from './hi/errors.json';
import machineryHi from './hi/machinery.json';
import onboardingHi from './hi/onboarding.json';
import validationHi from './hi/validation.json';

const resources = {
  en: {
    common: commonEn,
    onboarding: onboardingEn,
    validation: validationEn,
    errors: errorsEn,
    machinery: machineryEn,
  },
  kn: {
    common: commonKn,
    onboarding: onboardingKn,
    validation: validationKn,
    errors: errorsKn,
    machinery: machineryKn,
  },
  hi: {
    common: commonHi,
    onboarding: onboardingHi,
    validation: validationHi,
    errors: errorsHi,
    machinery: machineryHi,
  },
};

const initialLanguage = getStoredLanguage();

i18n.use(initReactI18next).init({
  resources,
  lng: initialLanguage,
  fallbackLng: DEFAULT_LANGUAGE,
  defaultNS: 'common',
  ns: ['common', 'onboarding', 'validation', 'errors', 'machinery'],
  interpolation: {
    escapeValue: false, // React automatically escapes HTML string injection
  },
  react: {
    useSuspense: false,
  },
});

export default i18n;
