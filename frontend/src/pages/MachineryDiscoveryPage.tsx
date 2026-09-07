import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { machineryApi } from '../api/machineryApi';
import type { MachineryAsset, MachineryCategory } from '../types/machinery';

export const MachineryDiscoveryPage: React.FC = () => {
  const { t } = useTranslation(['machinery', 'common']);

  const [location, setLocation] = useState('');
  const [category, setCategory] = useState<string>('');
  const [machines, setMachines] = useState<MachineryAsset[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Selected Machine for Rental Request modal
  const [selectedMachine, setSelectedMachine] = useState<MachineryAsset | null>(null);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [estimatedUnits, setEstimatedUnits] = useState(1);
  const [renterNotes, setRenterNotes] = useState('');
  const [requestSubmitting, setRequestSubmitting] = useState(false);
  const [requestSuccess, setRequestSuccess] = useState<string | null>(null);

  const fetchMachinery = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await machineryApi.searchMachinery({
        location,
        category: category || undefined,
        status: 'ACTIVE',
      });
      setMachines(res.content || []);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load machinery discovery data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMachinery();
  }, [category]);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    fetchMachinery();
  };

  const handleCreateRentalRequest = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedMachine || !startDate || !endDate) return;

    setRequestSubmitting(true);
    setError(null);
    setRequestSuccess(null);

    try {
      await machineryApi.createRentalRequest({
        machineryId: selectedMachine.id,
        startDate,
        endDate,
        rentalUnit: 'DAILY',
        estimatedUnits,
        renterNotes,
      });

      setRequestSuccess(`Rental request for ${selectedMachine.name} submitted successfully!`);
      setSelectedMachine(null);
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
        <p style={{ color: '#6b7280' }}>{t('subtitle')}</p>
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

      {/* Filter Form */}
      <form onSubmit={handleSearchSubmit} style={{ display: 'flex', gap: '12px', flexWrap: 'wrap', marginBottom: '24px' }}>
        <input
          type="text"
          placeholder={t('searchPlaceholder')}
          value={location}
          onChange={(e) => setLocation(e.target.value)}
          style={{ flex: '1', minWidth: '200px', padding: '10px 14px', border: '1px solid #d1d5db', borderRadius: '8px' }}
        />
        <select
          value={category}
          onChange={(e) => setCategory(e.target.value)}
          style={{ padding: '10px 14px', border: '1px solid #d1d5db', borderRadius: '8px', backgroundColor: '#fff' }}
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

      {/* Discovery Machinery Grid */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#6b7280' }}>Loading available machinery...</div>
      ) : machines.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#6b7280' }}>No machinery available matching your criteria.</div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '20px' }}>
          {machines.map((machine) => (
            <div
              key={machine.id}
              style={{ border: '1px solid #e5e7eb', borderRadius: '12px', padding: '16px', backgroundColor: '#fff', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}
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
                onClick={() => setSelectedMachine(machine)}
                style={{ marginTop: '16px', padding: '10px', backgroundColor: '#15803d', color: '#fff', fontWeight: 'bold', borderRadius: '8px', border: 'none', cursor: 'pointer', width: '100%' }}
              >
                {t('requestRental')}
              </button>
            </div>
          ))}
        </div>
      )}

      {/* Booking Modal */}
      {selectedMachine && (
        <div style={{ position: 'fixed', inset: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '16px', zIndex: 1000 }}>
          <div style={{ backgroundColor: '#fff', borderRadius: '12px', padding: '24px', maxWidth: '450px', width: '100%' }}>
            <h2 style={{ fontSize: '20px', fontWeight: 'bold', marginBottom: '8px' }}>Rent {selectedMachine.name}</h2>
            <p style={{ fontSize: '14px', color: '#6b7280', marginBottom: '16px' }}>Rate: ₹{selectedMachine.dailyRate}/day • Owner: {selectedMachine.ownerName}</p>

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
                  onClick={() => setSelectedMachine(null)}
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
