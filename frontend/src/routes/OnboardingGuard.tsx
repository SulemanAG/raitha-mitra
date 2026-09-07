/**
 * Onboarding Route Guard Component for Raitha Mitra.
 * 
 * @file src/routes/OnboardingGuard.tsx
 * @description Protects onboarding routes against direct invalid URL entry or missing state.
 * Gracefully redirects users to the appropriate preceding onboarding step.
 */

import React from 'react';
import { Navigate } from 'react-router-dom';
import { useOnboarding } from '../context/OnboardingContext';
import { ROUTES } from './routes.config';

export interface OnboardingGuardProps {
  children: React.ReactElement;
  requiredStep: 'role' | 'mobile' | 'otp' | 'completed';
}

export const OnboardingGuard: React.FC<OnboardingGuardProps> = ({
  children,
  requiredStep,
}) => {
  const { selectedRole, mobileNumber, isOtpVerified } = useOnboarding();

  switch (requiredStep) {
    case 'role':
      // Role selection requires no previous prerequisite step
      return children;

    case 'mobile':
      // Mobile login requires a role to have been chosen
      if (!selectedRole) {
        return <Navigate to={ROUTES.ROLE_SELECTION} replace />;
      }
      return children;

    case 'otp':
      // OTP verification requires mobile number and role
      if (!selectedRole) {
        return <Navigate to={ROUTES.ROLE_SELECTION} replace />;
      }
      if (!mobileNumber || mobileNumber.length !== 10) {
        return <Navigate to={ROUTES.MOBILE_LOGIN} replace />;
      }
      return children;

    case 'completed':
      // Dashboard placeholder requires OTP verification completion
      if (!isOtpVerified) {
        return <Navigate to={ROUTES.MOBILE_LOGIN} replace />;
      }
      return children;

    default:
      return children;
  }
};
