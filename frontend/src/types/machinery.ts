/**
 * TypeScript interfaces and type definitions for Agricultural Machinery Management & Rental workflows.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */

export type MachineryCategory =
  | 'TRACTOR'
  | 'HARVESTER'
  | 'ROTAVATOR'
  | 'SEED_DRILL'
  | 'CULTIVATOR'
  | 'PUMP'
  | 'THRESHER'
  | 'SPRAYER'
  | 'OTHER';

export type OperationalStatus = 'ACTIVE' | 'INACTIVE' | 'UNDER_MAINTENANCE' | 'RETIRED';

export type RentalStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'CANCELLED' | 'COMPLETED';

export type RentalUnit = 'DAILY' | 'HOURLY';

export interface MachineryOwnerProfile {
  id: string;
  userId: string;
  fullName: string;
  address: string;
  contactNumber: string;
  createdAt: string;
  updatedAt: string;
}

export interface MachineryAsset {
  id: string;
  ownerUserId: string;
  ownerName: string;
  ownerMobile: string;
  name: string;
  modelNumber?: string;
  category: MachineryCategory;
  hpRating?: number;
  location: string;
  dailyRate: number;
  hourlyRate?: number;
  status: OperationalStatus;
  createdAt: string;
  updatedAt: string;
}

export interface RentalRequest {
  id: string;
  machineryId: string;
  machineryName: string;
  machineryCategory: MachineryCategory;
  ownerUserId: string;
  ownerName: string;
  ownerMobile: string;
  renterUserId: string;
  renterMobile: string;
  startDate: string;
  endDate: string;
  rentalUnit: RentalUnit;
  estimatedUnits: number;
  ratePerUnit: number;
  totalAmount: number;
  status: RentalStatus;
  renterNotes?: string;
  ownerNotes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateMachineryOwnerProfilePayload {
  fullName: string;
  address: string;
  contactNumber: string;
}

export interface CreateMachineryPayload {
  name: string;
  modelNumber?: string;
  category: MachineryCategory;
  hpRating?: number;
  location: string;
  dailyRate: number;
  hourlyRate?: number;
}

export interface UpdateMachineryPayload extends CreateMachineryPayload {
  status: OperationalStatus;
}

export interface CreateRentalRequestPayload {
  machineryId: string;
  startDate: string;
  endDate: string;
  rentalUnit: RentalUnit;
  estimatedUnits: number;
  renterNotes?: string;
}

export interface UpdateRentalStatusPayload {
  status: RentalStatus;
  ownerNotes?: string;
}

export interface MachineryDiscoveryItem {
  id: string;
  name: string;
  modelNumber?: string;
  category: MachineryCategory;
  hpRating?: number;
  dailyRate: number;
  hourlyRate?: number;
  status: OperationalStatus;
  village?: string;
  district?: string;
  approximateDistanceKm?: number;
  ownerName: string;
  available: boolean;
  version: number;
}

export interface UpdateLocationPayload {
  latitude?: number;
  longitude?: number;
  state?: string;
  district?: string;
  taluk?: string;
  village?: string;
  locationSource?: 'GPS_DEVICE' | 'MANUAL_PIN' | 'PROFILE_ADDRESS';
  version?: number;
}

