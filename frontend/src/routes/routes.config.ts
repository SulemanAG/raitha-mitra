/**
 * Centralized Route Constants for Raitha Mitra.
 * 
 * @file src/routes/routes.config.ts
 * @description Prevents route string duplication and typos across application components.
 */

export const ROUTES = {
  SPLASH: '/',
  LANGUAGE_SELECTION: '/language',
  ROLE_SELECTION: '/role-selection',
  MOBILE_LOGIN: '/mobile-login',
  OTP_VERIFICATION: '/otp-verification',
  DASHBOARD_PLACEHOLDER: '/dashboard-placeholder',
  MACHINERY_DISCOVERY: '/machinery',
  MY_MACHINERY: '/my-machinery',
  RENTAL_REQUESTS: '/rental-requests',
} as const;
