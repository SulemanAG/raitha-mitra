/**
 * Root Application Component for Raitha Mitra.
 * 
 * @file src/App.tsx
 * @description Composes global context providers (OnboardingProvider) and router.
 */

import React from 'react';
import { OnboardingProvider } from './context/OnboardingContext';
import { AppRouter } from './routes/AppRouter';
import './locales/i18n'; // Import i18n initialization side effect

export const App: React.FC = () => {
  return (
    <OnboardingProvider>
      <AppRouter />
    </OnboardingProvider>
  );
};

export default App;
