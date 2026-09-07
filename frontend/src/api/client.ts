/**
 * API Client Configuration Placeholder for Raitha Mitra.
 * 
 * @file src/api/client.ts
 * @description Centralized HTTP client abstraction establishing the future Spring Boot REST API
 * base URL configuration point without hardcoding endpoints across components.
 */

// Controlled configuration point for Phase 2 API Base URL
export const API_CONFIG = {
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1',
  timeout: 10000,
};

/**
 * Placeholder HTTP fetch wrapper for future Phase 2 backend REST API integration.
 */
export async function apiFetch<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<T> {
  const url = `${API_CONFIG.baseURL}${endpoint}`;
  
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  };

  const response = await fetch(url, { ...options, headers });

  if (!response.ok) {
    throw new Error(`API Request failed with status ${response.status}`);
  }

  return response.json() as Promise<T>;
}
