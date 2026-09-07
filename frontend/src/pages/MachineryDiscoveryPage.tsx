import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { machineryApi } from '../api/machineryApi';
import { locationApi } from '../api/locationApi';
import type { MachineryAsset, MachineryCategory, MachineryDiscoveryItem } from '../types/machinery';

export const MachineryDiscoveryPage: React.FC = () => {
  const { t } = useTranslation(['machinery', 'common']);

  // Mode: 'GPS' (spatial discovery with coordinates & radius) or 'TEXT' (text location search)
  const [searchMode, setSearchMode] = useState<'GPS' | 'TEXT'>('GPS');
  const [locationText, setLocationText] = useState('');
  const [category, setCategory] = useState<string>('');
  const [radiusKm, setRadiusKm] = useState<number>(25);
  
  // GPS state
  const [coords, setCoords] = useState<{ lat: number; lon: number } | null>(null);
  const [gpsLoading, setGpsLoading] = useState(false);
  const [gpsError, setGpsError] = useState<string | null>(null);

  // Machinery items & UI state
  const [spatialMachines, setSpatialMachines] = useState<MachineryDiscoveryItem[]>([]);
  const [textMachines, setTextMachines] = useState<MachineryAsset[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Selected Machine for Rental Request modal
  const [selectedMachineId, setSelectedMachineId] = useState<string | null>(null);
  const [selectedMachineName, setSelectedMachineName] = useState<string>('');
  const [selectedMachineRate, setSelectedMachineRate] = useState<number>(0);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [estimatedUnits, setEstimatedUnits] = useState(1);
  const [renterNotes, setRenterNotes] = useState('');
  const [requestSubmitting, setRequestSubmitting] = useState(false);
  const [requestSuccess, setRequestSuccess] = useState<string | null>(null);

  const requestGpsLocation = () => {
    if (!navigator.geolocation) {
      setGpsError('Geolocation is not supported by your browser. Falling back to text search.');
      setSearchMode('TEXT');
      return;
    }

    setGpsLoading(true);
    setGpsError(null);

    navigator.geolocation.getCurrentPosition(
      (position) => {
        setCoords({
          lat: position.coords.latitude,
          lon: position.coords.longitude,
        });
        setGpsLoading(false);
        setSearchMode('GPS');
      },
      (err) => {
        setGpsLoading(false);
        setGpsError(`Location access denied or unavailable (${err.message}). Switched to text search.`);
        setSearchMode('TEXT');
      },
      { timeout: 10000, enableHighAccuracy: true }
    );
  };

  const fetchMachinery = async () => {
    setLoading(true);
    setError(null);

    try {
      if (searchMode === 'GPS' && coords) {
        const res = await locationApi.getNearbyMachinery({
          lat: coords.lat,
          lon: coords.lon,
          radiusKm,
          category: (category as MachineryCategory) || undefined,
          availableOnly: true,
        });
        setSpatialMachines(res.content || []);
      } else {
        const res = await machineryApi.searchMachinery({
          location: locationText,
          category: category || undefined,
          status: 'ACTIVE',
        });
        setTextMachines(res.content || []);
      }
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load machinery discovery data');
    } finally {
      setLoading(false);
    }
  };

  // Initial GPS fetch on mount
  useEffect(() => {
    requestGpsLocation();
  }, []);

  // Refetch when filters or coordinates change
  useEffect(() => {
    if (searchMode === 'GPS' && coords) {
      fetchMachinery();
    } else if (searchMode === 'TEXT') {
      fetchMachinery();
    }
  }, [coords, searchMode, radiusKm, category]);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    fetchMachinery();
  };

  const handleCreateRentalRequest = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedMachineId || !startDate || !endDate) return;

    setRequestSubmitting(true);
    setError(null);
    setRequestSuccess(null);

    try {
      await machineryApi.createRentalRequest({
        machineryId: selectedMachineId,
        startDate,
        endDate,
        rentalUnit: 'DAILY',
        estimatedUnits,
        renterNotes,
      });

      setRequestSuccess(`Rental request for ${selectedMachineName} submitted successfully!`);
      setSelectedMachineId(null);
      setStartDate('');
      setEndDate('');
      setRenterNotes('');
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to submit rental request');
    } finally {
      setRequestSubmitting(false);
    }
  };

  const categories: { label: string; value: MachineryCategory | '' }[] = [
    { label: t('allCategories'), value: '' },
    { label: 'Tractor', value: 'TRACTOR' },
    { label: 'Harvester', value: 'HARVESTER' },
    { label: 'Rotavator', value: 'ROTAVATOR' },
    { label: 'Seed Drill', value: 'SEED_DRILL' },
    { label: 'Cultivator', value: 'CULTIVATOR' },
    { label: 'Pump', value: 'PUMP' },
    { label: 'Thresher', value: 'THRESHER' },
    { label: 'Sprayer', value: 'SPRAYER' },
    { label: 'Other', value: 'OTHER' },
  ];

  return (
    <div style={{ maxWidth: '1000px', margin: '0 auto', padding: '24px 16px', fontFamily: 'sans-serif' }}>
      <header style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '24px', fontWeight: 'bold', color: '#1f2937' }}>{t('title')}</h1>
        <p style={{ color: '#6b7280' }}>Discover nearby agricultural machinery within your preferred radius.</p>
      </header>

      {requestSuccess && (
        <div style={{ padding: '12px 16px', backgroundColor: '#dcfce7', color: '#166534', borderRadius: '8px', marginBottom: '16px' }}>
          {requestSuccess}
        </div>
      )}

      {error && (
        <div style={{ padding: '12px 16px', backgroundColor: '#fee2e2', color: '#991b1b', borderRadius: '8px', marginBottom: '16px' }}>
          {error}
        </div>
      )}

      {gpsError && (
        <div style={{ padding: '12px 16px', backgroundColor: '#fef3c7', color: '#92400e', borderRadius: '8px', marginBottom: '16px', fontSize: '14px' }}>
          ⚠️ {gpsError}
        </div>
      )}

      {/* Geospatial Control Panel */}
      <div style={{ backgroundColor: '#f8fafc', padding: '16px', borderRadius: '12px', border: '1px solid #e2e8f0', marginBottom: '24px' }}>
        <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap', alignItems: 'center', marginBottom: '16px' }}>
          <button
            type="button"
            onClick={requestGpsLocation}
            disabled={gpsLoading}
            style={{
              padding: '10px 16px',
              backgroundColor: searchMode === 'GPS' ? '#16a34a' : '#e2e8f0',
              color: searchMode === 'GPS' ? '#fff' : '#334155',
              fontWeight: 'bold',
              borderRadius: '8px',
              border: 'none',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
            }}
          >
            🎯 {gpsLoading ? 'Detecting Location...' : 'Use Current Location (GPS)'}
          </button>

          <button
            type="button"
            onClick={() => setSearchMode('TEXT')}
            style={{
              padding: '10px 16px',
              backgroundColor: searchMode === 'TEXT' ? '#16a34a' : '#e2e8f0',
              color: searchMode === 'TEXT' ? '#fff' : '#334155',
              fontWeight: 'bold',
              borderRadius: '8px',
              border: 'none',
              cursor: 'pointer',
            }}
          >
            🔍 Search by Location Name
          </button>
        </div>

        {searchMode === 'GPS' ? (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px', flexWrap: 'wrap' }}>
              <label style={{ fontWeight: 'bold', color: '#334155', fontSize: '14px' }}>
                Distance Radius: <span style={{ color: '#16a34a' }}>{radiusKm} km</span>
              </label>
              <input
                type="range"
                min="5"
                max="100"
                step="5"
                value={radiusKm}
                onChange={(e) => setRadiusKm(Number(e.target.value))}
                style={{ flex: '1', minWidth: '150px', accentColor: '#16a34a' }}
              />
            </div>

            <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
              <select
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                style={{ padding: '10px 14px', border: '1px solid #cbd5e1', borderRadius: '8px', backgroundColor: '#fff', flex: '1', minWidth: '180px' }}
              >
                {categories.map((cat) => (
                  <option key={cat.value} value={cat.value}>
                    {cat.label}
                  </option>
                ))}
              </select>

              <button
                type="button"
                onClick={fetchMachinery}
                style={{ padding: '10px 20px', backgroundColor: '#15803d', color: '#fff', fontWeight: 'bold', borderRadius: '8px', border: 'none', cursor: 'pointer' }}
              >
                Refresh Radius Search
              </button>
            </div>
          </div>
        ) : (
          <form onSubmit={handleSearchSubmit} style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
            <input
              type="text"
              placeholder={t('searchPlaceholder')}
              value={locationText}
              onChange={(e) => setLocationText(e.target.value)}
              style={{ flex: '1', minWidth: '200px', padding: '10px 14px', border: '1px solid #cbd5e1', borderRadius: '8px' }}
            />
            <select
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              style={{ padding: '10px 14px', border: '1px solid #cbd5e1', borderRadius: '8px', backgroundColor: '#fff' }}
            >
              {categories.map((cat) => (
                <option key={cat.value} value={cat.value}>
                  {cat.label}
                </option>
              ))}
            </select>
            <button
              type="submit"
              style={{ padding: '10px 20px', backgroundColor: '#16a34a', color: '#fff', fontWeight: 'bold', borderRadius: '8px', border: 'none', cursor: 'pointer' }}
            >
              Search
            </button>
          </form>
        )}
      </div>

      {/* Discovery Grid */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#6b7280' }}>Searching for available machinery...</div>
      ) : searchMode === 'GPS' ? (
        spatialMachines.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '40px', color: '#6b7280' }}>No machinery available within {radiusKm} km radius.</div>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '20px' }}>
            {spatialMachines.map((machine) => (
              <div
                key={machine.id}
                style={{ border: '1px solid #e2e8f0', borderRadius: '12px', padding: '16px', backgroundColor: '#fff', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}
              >
                <div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                    <span style={{ fontSize: '12px', fontWeight: 'bold', padding: '4px 8px', borderRadius: '4px', backgroundColor: '#dbeafe', color: '#1e40af' }}>
                      {machine.category}
                    </span>
                    <span style={{ fontSize: '12px', color: '#16a34a', fontWeight: 'bold' }}>Available</span>
                  </div>
                  <h3 style={{ fontSize: '18px', fontWeight: 'bold', color: '#111827', margin: '4px 0' }}>{machine.name}</h3>
                  {machine.modelNumber && <p style={{ fontSize: '13px', color: '#6b7280', margin: '2px 0' }}>Model: {machine.modelNumber}</p>}
                  
                  {/* Distance badge - privacy safe (no exact lat/lon) */}
                  <div style={{ marginTop: '8px', padding: '6px 10px', backgroundColor: '#f0fdf4', color: '#166534', borderRadius: '6px', fontSize: '13px', fontWeight: '500' }}>
                    📍 {machine.approximateDistanceKm !== undefined ? `${machine.approximateDistanceKm} km away` : 'Location available'}
                    {machine.village ? ` • ${machine.village}` : ''}
                    {machine.district ? `, ${machine.district}` : ''}
                  </div>

                  {machine.hpRating && <p style={{ fontSize: '13px', color: '#4b5563', marginTop: '6px' }}>⚡ {machine.hpRating} HP</p>}
                  <p style={{ fontSize: '13px', color: '#6b7280', marginTop: '4px' }}>Owner: {machine.ownerName}</p>

                  <div style={{ marginTop: '12px', fontSize: '16px', fontWeight: 'bold', color: '#16a34a' }}>
                    ₹{machine.dailyRate}/day {machine.hourlyRate ? `(₹${machine.hourlyRate}/hr)` : ''}
                  </div>
                </div>

                <button
                  onClick={() => {
                    setSelectedMachineId(machine.id);
                    setSelectedMachineName(machine.name);
                    setSelectedMachineRate(machine.dailyRate);
                  }}
                  style={{ marginTop: '16px', padding: '10px', backgroundColor: '#15803d', color: '#fff', fontWeight: 'bold', borderRadius: '8px', border: 'none', cursor: 'pointer', width: '100%' }}
                >
                  {t('requestRental')}
                </button>
              </div>
            ))}
          </div>
        )
      ) : textMachines.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#6b7280' }}>No machinery available matching your criteria.</div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '20px' }}>
          {textMachines.map((machine) => (
            <div
              key={machine.id}
              style={{ border: '1px solid #e2e8f0', borderRadius: '12px', padding: '16px', backgroundColor: '#fff', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}
            >
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                  <span style={{ fontSize: '12px', fontWeight: 'bold', padding: '4px 8px', borderRadius: '4px', backgroundColor: '#dbeafe', color: '#1e40af' }}>
                    {machine.category}
                  </span>
                  <span style={{ fontSize: '12px', color: '#16a34a', fontWeight: 'bold' }}>{t('statusActive')}</span>
                </div>
                <h3 style={{ fontSize: '18px', fontWeight: 'bold', color: '#111827', margin: '4px 0' }}>{machine.name}</h3>
                {machine.modelNumber && <p style={{ fontSize: '13px', color: '#6b7280', margin: '2px 0' }}>Model: {machine.modelNumber}</p>}
                <p style={{ fontSize: '14px', color: '#4b5563', margin: '6px 0' }}>📍 {machine.location}</p>
                {machine.hpRating && <p style={{ fontSize: '13px', color: '#4b5563' }}>⚡ {machine.hpRating} HP</p>}
                <div style={{ marginTop: '12px', fontSize: '16px', fontWeight: 'bold', color: '#16a34a' }}>
                  ₹{machine.dailyRate}/day {machine.hourlyRate ? `(₹${machine.hourlyRate}/hr)` : ''}
                </div>
              </div>

              <button
                onClick={() => {
                  setSelectedMachineId(machine.id);
                  setSelectedMachineName(machine.name);
                  setSelectedMachineRate(machine.dailyRate);
                }}
                style={{ marginTop: '16px', padding: '10px', backgroundColor: '#15803d', color: '#fff', fontWeight: 'bold', borderRadius: '8px', border: 'none', cursor: 'pointer', width: '100%' }}
              >
                {t('requestRental')}
              </button>
            </div>
          ))}
        </div>
      )}

      {/* Booking Modal */}
      {selectedMachineId && (
        <div style={{ position: 'fixed', inset: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '16px', zIndex: 1000 }}>
          <div style={{ backgroundColor: '#fff', borderRadius: '12px', padding: '24px', maxWidth: '450px', width: '100%' }}>
            <h2 style={{ fontSize: '20px', fontWeight: 'bold', marginBottom: '8px' }}>Rent {selectedMachineName}</h2>
            <p style={{ fontSize: '14px', color: '#6b7280', marginBottom: '16px' }}>Rate: ₹{selectedMachineRate}/day</p>

            <form onSubmit={handleCreateRentalRequest} style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              <div>
                <label style={{ display: 'block', fontSize: '13px', fontWeight: 'bold', marginBottom: '4px' }}>Start Date</label>
                <input
                  type="date"
                  required
                  value={startDate}
                  onChange={(e) => setStartDate(e.target.value)}
                  style={{ width: '100%', padding: '10px', border: '1px solid #d1d5db', borderRadius: '6px' }}
                />
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '13px', fontWeight: 'bold', marginBottom: '4px' }}>End Date</label>
                <input
                  type="date"
                  required
                  value={endDate}
                  onChange={(e) => setEndDate(e.target.value)}
                  style={{ width: '100%', padding: '10px', border: '1px solid #d1d5db', borderRadius: '6px' }}
                />
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '13px', fontWeight: 'bold', marginBottom: '4px' }}>Estimated Days</label>
                <input
                  type="number"
                  min="1"
                  value={estimatedUnits}
                  onChange={(e) => setEstimatedUnits(parseInt(e.target.value) || 1)}
                  style={{ width: '100%', padding: '10px', border: '1px solid #d1d5db', borderRadius: '6px' }}
                />
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '13px', fontWeight: 'bold', marginBottom: '4px' }}>Notes / Agricultural Requirement</label>
                <textarea
                  rows={3}
                  value={renterNotes}
                  onChange={(e) => setRenterNotes(e.target.value)}
                  placeholder="e.g. Need tractor for 3 acres ploughing"
                  style={{ width: '100%', padding: '10px', border: '1px solid #d1d5db', borderRadius: '6px' }}
                />
              </div>

              <div style={{ display: 'flex', gap: '10px', marginTop: '12px' }}>
                <button
                  type="button"
                  onClick={() => setSelectedMachineId(null)}
                  style={{ flex: 1, padding: '10px', backgroundColor: '#e5e7eb', color: '#374151', border: 'none', borderRadius: '6px', fontWeight: 'bold', cursor: 'pointer' }}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={requestSubmitting}
                  style={{ flex: 1, padding: '10px', backgroundColor: requestSubmitting ? '#9ca3af' : '#16a34a', color: '#fff', border: 'none', borderRadius: '6px', fontWeight: 'bold', cursor: requestSubmitting ? 'not-allowed' : 'pointer' }}
                >
                  {requestSubmitting ? 'Submitting...' : 'Confirm Request'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
