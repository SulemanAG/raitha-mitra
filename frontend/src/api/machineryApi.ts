import { apiFetch } from './client';
import type {
  CreateMachineryOwnerProfilePayload,
  CreateMachineryPayload,
  CreateRentalRequestPayload,
  MachineryAsset,
  MachineryOwnerProfile,
  RentalRequest,
  UpdateMachineryPayload,
  UpdateRentalStatusPayload,
} from '../types/machinery';

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export const machineryApi = {
  // Owner Profile
  createOrUpdateOwnerProfile: (payload: CreateMachineryOwnerProfilePayload) =>
    apiFetch<MachineryOwnerProfile>('/machinery-owner-profile', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),

  getMyOwnerProfile: () => apiFetch<MachineryOwnerProfile>('/machinery-owner-profile/me'),

  // Machinery Asset Management
  registerMachinery: (payload: CreateMachineryPayload) =>
    apiFetch<MachineryAsset>('/machinery', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),

  getMyMachinery: (page = 0, size = 10) =>
    apiFetch<PageResponse<MachineryAsset>>(`/machinery/me?page=${page}&size=${size}`),

  searchMachinery: (params: { location?: string; category?: string; status?: string; page?: number; size?: number }) => {
    const searchParams = new URLSearchParams();
    if (params.location) searchParams.append('location', params.location);
    if (params.category) searchParams.append('category', params.category);
    if (params.status) searchParams.append('status', params.status);
    searchParams.append('page', String(params.page || 0));
    searchParams.append('size', String(params.size || 10));

    return apiFetch<PageResponse<MachineryAsset>>(`/machinery/search?${searchParams.toString()}`);
  },

  getMachineryById: (id: string) => apiFetch<MachineryAsset>(`/machinery/${id}`),

  updateMachinery: (id: string, payload: UpdateMachineryPayload) =>
    apiFetch<MachineryAsset>(`/machinery/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    }),

  toggleMachineryStatus: (id: string, status: string) =>
    apiFetch<MachineryAsset>(`/machinery/${id}/status`, {
      method: 'PATCH',
      body: JSON.stringify({ status }),
    }),

  deleteMachinery: (id: string) =>
    apiFetch<void>(`/machinery/${id}`, {
      method: 'DELETE',
    }),

  // Rental Requests & Booking Management
  createRentalRequest: (payload: CreateRentalRequestPayload) =>
    apiFetch<RentalRequest>('/machinery-rentals', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),

  getMyRentalRequests: (page = 0, size = 10) =>
    apiFetch<PageResponse<RentalRequest>>(`/machinery-rentals/my-requests?page=${page}&size=${size}`),

  getOwnerRentalRequests: (page = 0, size = 10) =>
    apiFetch<PageResponse<RentalRequest>>(`/machinery-rentals/owner-requests?page=${page}&size=${size}`),

  getRentalRequestById: (id: string) => apiFetch<RentalRequest>(`/machinery-rentals/${id}`),

  acceptRentalRequest: (id: string, payload?: UpdateRentalStatusPayload) =>
    apiFetch<RentalRequest>(`/machinery-rentals/${id}/accept`, {
      method: 'POST',
      body: JSON.stringify(payload || { status: 'ACCEPTED' }),
    }),

  rejectRentalRequest: (id: string, payload?: UpdateRentalStatusPayload) =>
    apiFetch<RentalRequest>(`/machinery-rentals/${id}/reject`, {
      method: 'POST',
      body: JSON.stringify(payload || { status: 'REJECTED' }),
    }),

  cancelRentalRequest: (id: string) =>
    apiFetch<RentalRequest>(`/machinery-rentals/${id}/cancel`, {
      method: 'POST',
    }),
};
