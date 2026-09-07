/**
 * Supported Language domain types for Raitha Mitra.
 * 
 * @file src/types/language.types.ts
 * @description Defines SupportedLanguage type, language codes, and language metadata contracts.
 */

export type SupportedLanguage = 'en' | 'kn' | 'hi';

export interface LanguageOption {
  code: SupportedLanguage;
  name: string;        // Native display name (e.g., "English", "ಕನ್ನಡ", "हिंदी")
  englishName: string; // English fallback name
  flagSymbol: string;  // Visual representation identifier
}
