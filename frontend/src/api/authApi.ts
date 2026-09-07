import { apiFetch } from './client';

export interface RequestOtpResponse {
  message: string;
  otpRequired?: boolean;
}

export interface AuthUserResponse {
  id: string;
  mobileNumber: string;
  primaryRole: string;
  accountStatus: string;
  roles: string[];
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface AuthSessionResponse {
  accessToken: string;
  expiresInSeconds: number;
  user: AuthUserResponse;
  isNewUser: boolean;
}

export interface AuthConfigResponse {
  otpEnabled: boolean;
  mode: string;
}

export const authApi = {
  getAuthConfig: () => apiFetch<AuthConfigResponse>('/auth/config'),

  requestOtp: (mobileNumber: string) =>
    apiFetch<RequestOtpResponse>('/auth/request-otp', {
      method: 'POST',
      body: JSON.stringify({ mobileNumber }),
    }),

  verifyOtp: (mobileNumber: string, otp: string) =>
    apiFetch<AuthSessionResponse>('/auth/verify-otp', {
      method: 'POST',
      body: JSON.stringify({ mobileNumber, otp }),
    }),

  getCurrentUser: () => apiFetch<AuthUserResponse>('/auth/me'),

  logout: () =>
    apiFetch<{ message: string }>('/auth/logout', {
      method: 'POST',
    }),
};
