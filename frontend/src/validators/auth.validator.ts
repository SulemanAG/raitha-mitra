/**
 * Authentication & Onboarding Zod Validation Schemas.
 * 
 * @file src/validators/auth.validator.ts
 * @description Declarative Zod schemas for validating Indian mobile numbers (+91) and 6-digit OTP codes.
 */

import { z } from 'zod';
import { INDIAN_MOBILE_REGEX, MOBILE_NUMBER_LENGTH, OTP_LENGTH } from '../constants/app.constants';

export const createMobileNumberSchema = (t: (key: string) => string) => {
  return z.object({
    mobileNumber: z
      .string()
      .trim()
      .min(1, { message: t('validation:mobile.required') })
      .length(MOBILE_NUMBER_LENGTH, { message: t('validation:mobile.invalidLength') })
      .regex(/^\d+$/, { message: t('validation:mobile.numericOnly') })
      .regex(INDIAN_MOBILE_REGEX, { message: t('validation:mobile.invalidFormat') }),
  });
};

export const createOtpSchema = (t: (key: string) => string) => {
  return z.object({
    otp: z
      .string()
      .trim()
      .min(1, { message: t('validation:otp.required') })
      .length(OTP_LENGTH, { message: t('validation:otp.invalidLength') })
      .regex(/^\d+$/, { message: t('validation:otp.numericOnly') }),
  });
};

export type MobileNumberFormData = z.infer<ReturnType<typeof createMobileNumberSchema>>;
export type OtpFormData = z.infer<ReturnType<typeof createOtpSchema>>;
