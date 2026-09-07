import { apiFetch } from './client';
import type { PageResponse } from './machineryApi';
import type { MachineryCategory, MachineryDiscoveryItem, UpdateLocationPayload } from '../types/machinery';

/**
 * Frontend API client for Location & Geospatial Discovery Subsystem.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
export const locationApi = {
  /**
   * Search nearby agricultural machinery assets using Haversine radial distance filter.
   */
  getNearbyMachinery: (params: {
    lat: number;
    lon: number;
    radiusKm?: number;
    category?: MachineryCategory;
    availableOnly?: boolean;
    page?: number;
    size?: number;
  }) => {
    const query = new URLSearchParams();
    query.append('lat', String(params.lat));
    query.append('lon', String(params.lon));
    if (params.radiusKm !== undefined) query.append('radiusKm', String(params.radiusKm));
    if (params.category) query.append('category', params.category);
    if (params.availableOnly !== undefined) query.append('availableOnly', String(params.availableOnly));
    query.append('page', String(params.page || 0));
    query.append('size', String(params.size || 10));

    return apiFetch<PageResponse<MachineryDiscoveryItem>>(`/discovery/machinery/nearby?${query.toString()}`);
  },

  /**
   * Update operational physical location for a machinery asset with optimistic locking version control.
   */
  updateMachineryLocation: (id: string, payload: UpdateLocationPayload) =>
    apiFetch<MachineryDiscoveryItem>(`/machinery/${id}/location`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    }),
};
