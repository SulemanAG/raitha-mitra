/**
 * Application Routing Architecture for Raitha Mitra.
 * 
 * @file src/routes/AppRouter.tsx
 * @description React Router SPA routes with onboarding step guards and unknown route fallbacks.
 */

import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ROUTES } from './routes.config';
import { OnboardingGuard } from './OnboardingGuard';
import { SplashPage } from '../pages/SplashPage';
import { LanguageSelectionPage } from '../pages/LanguageSelectionPage';
import { RoleSelectionPage } from '../pages/RoleSelectionPage';
import { MobileLoginPage } from '../pages/MobileLoginPage';
import { OtpVerificationPage } from '../pages/OtpVerificationPage';
import { DashboardPlaceholderPage } from '../pages/DashboardPlaceholderPage';

export const AppRouter: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path={ROUTES.SPLASH} element={<SplashPage />} />
        <Route path={ROUTES.LANGUAGE_SELECTION} element={<LanguageSelectionPage />} />
        
        <Route
          path={ROUTES.ROLE_SELECTION}
          element={
            <OnboardingGuard requiredStep="role">
              <RoleSelectionPage />
            </OnboardingGuard>
          }
        />
        
        <Route
          path={ROUTES.MOBILE_LOGIN}
          element={
            <OnboardingGuard requiredStep="mobile">
              <MobileLoginPage />
            </OnboardingGuard>
          }
        />

        <Route
          path={ROUTES.OTP_VERIFICATION}
          element={
            <OnboardingGuard requiredStep="otp">
              <OtpVerificationPage />
            </OnboardingGuard>
          }
        />

        <Route
          path={ROUTES.DASHBOARD_PLACEHOLDER}
          element={
            <OnboardingGuard requiredStep="completed">
              <DashboardPlaceholderPage />
            </OnboardingGuard>
          }
        />

        {/* Fallback for unknown URLs */}
        <Route path="*" element={<Navigate to={ROUTES.SPLASH} replace />} />
      </Routes>
    </BrowserRouter>
  );
};
