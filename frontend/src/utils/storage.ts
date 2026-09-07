/**
 * Safe LocalStorage Manager for Raitha Mitra.
 * 
 * @file src/utils/storage.ts
 * @description Provides type-safe, fault-tolerant interactions with browser localStorage.
 * Defends against corrupt data, quota errors, disabled storage, and unexpected values.
 */

import { DEFAULT_LANGUAGE, STORAGE_KEYS, SUPPORTED_LANGUAGES } from '../constants/app.constants';
import type { SupportedLanguage } from '../types/language.types';
import type { OnboardingState } from '../types/onboarding.types';

/**
 * Checks if a language string is a valid supported language code.
 */
export function isValidLanguage(lang: unknown): lang is SupportedLanguage {
  return typeof lang === 'string' && SUPPORTED_LANGUAGES.some((item) => item.code === lang);
}

/**
 * Safely retrieves stored language preference.
 * Falls back to DEFAULT_LANGUAGE ('en') if localStorage is empty, corrupt, or throws an exception.
 */
export function getStoredLanguage(): SupportedLanguage {
  try {
    const raw = localStorage.getItem(STORAGE_KEYS.LANGUAGE);
    if (isValidLanguage(raw)) {
      return raw;
    }
  } catch {
    // localStorage unavailable (e.g. strict privacy settings or disabled storage)
  }
  return DEFAULT_LANGUAGE;
}

/**
 * Safely persists language preference to localStorage.
 */
export function setStoredLanguage(lang: SupportedLanguage): void {
  try {
    if (isValidLanguage(lang)) {
      localStorage.setItem(STORAGE_KEYS.LANGUAGE, lang);
    }
  } catch (error) {
    console.warn('Unable to persist language preference to localStorage:', error);
  }
}

/**
 * Safely retrieves stored onboarding state.
 */
export function getStoredOnboardingState(): Partial<OnboardingState> | null {
  try {
    const raw = localStorage.getItem(STORAGE_KEYS.ONBOARDING_STATE);
    if (raw) {
      const parsed = JSON.parse(raw);
      if (typeof parsed === 'object' && parsed !== null) {
        return parsed as Partial<OnboardingState>;
      }
    }
  } catch {
    // Ignore JSON parse errors or storage access failures
  }
  return null;
}

/**
 * Safely persists onboarding state to localStorage.
 */
export function setStoredOnboardingState(state: Partial<OnboardingState>): void {
  try {
    localStorage.setItem(STORAGE_KEYS.ONBOARDING_STATE, JSON.stringify(state));
  } catch (error) {
    console.warn('Unable to persist onboarding state to localStorage:', error);
  }
}

/**
 * Clears stored onboarding state.
 */
export function clearStoredOnboardingState(): void {
  try {
    localStorage.removeItem(STORAGE_KEYS.ONBOARDING_STATE);
  } catch {
    // Ignore storage access errors
  }
}
