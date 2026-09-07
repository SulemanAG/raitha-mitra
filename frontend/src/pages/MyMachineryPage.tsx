import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { machineryApi } from '../api/machineryApi';
import type { MachineryAsset, MachineryCategory, OperationalStatus } from '../types/machinery';

export const MyMachineryPage: React.FC = () => {
  const { t } = useTranslation(['machinery', 'common']);

  const [fleet, setFleet] = useState<MachineryAsset[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // Profile State
  const [fullName, setFullName] = useState('');
  const [address, setAddress] = useState('');
  const [contactNumber, setContactNumber] = useState('');
  const [hasProfile, setHasProfile] = useState(false);

  // New Machine Form State
  const [showAddModal, setShowAddModal] = useState(false);
  const [name, setName] = useState('');
  const [modelNumber, setModelNumber] = useState('');
  const [category, setCategory] = useState<MachineryCategory>('TRACTOR');
  const [hpRating, setHpRating] = useState<number | undefined>(undefined);
  const [location, setLocation] = useState('');
  const [dailyRate, setDailyRate] = useState<number>(1000);
  const [hourlyRate, setHourlyRate] = useState<number | undefined>(undefined);

  const fetchProfileAndFleet = async () => {
    setLoading(true);
    setError(null);
    try {
      try {
        const prof = await machineryApi.getMyOwnerProfile();
        setFullName(prof.fullName);
        setAddress(prof.address);
        setContactNumber(prof.contactNumber);
        setHasProfile(true);
      } catch {
        setHasProfile(false);
      }

      const res = await machineryApi.getMyMachinery();
      setFleet(res.content || []);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load fleet data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProfileAndFleet();
  }, []);

  const handleSaveProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    try {
      await machineryApi.createOrUpdateOwnerProfile({ fullName, address, contactNumber });
      setHasProfile(true);
      setSuccess('Machinery Owner Profile saved successfully!');
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to save owner profile');
    }
  };

  const handleRegisterMachine = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    try {
      await machineryApi.registerMachinery({
        name,
        modelNumber: modelNumber || undefined,
        category,
        hpRating: hpRating || undefined,
        location,
        dailyRate,
        hourlyRate: hourlyRate || undefined,
      });

      setSuccess(`Registered ${name} successfully!`);
      setShowAddModal(false);
      setName('');
      setModelNumber('');
      setLocation('');
      setHpRating(undefined);
      setHourlyRate(undefined);
      fetchProfileAndFleet();
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to register machinery');
    }
  };

  const handleToggleStatus = async (id: string, currentStatus: OperationalStatus) => {
    const nextStatus = currentStatus === 'ACTIVE' ? 'UNDER_MAINTENANCE' : 'ACTIVE';
    try {
      await machineryApi.toggleMachineryStatus(id, nextStatus);
      fetchProfileAndFleet();
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to update status');
    }
  };

  return (
    <div style={{ maxWidth: '1000px', margin: '0 auto', padding: '24px 16px', fontFamily: 'sans-serif' }}>
      <header style={{ marginBottom: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h1 style={{ fontSize: '24px', fontWeight: 'bold', color: '#1f2937' }}>{t('myMachinery')}</h1>
          <p style={{ color: '#6b7280' }}>Manage your registered equipment fleet and availability</p>
        </div>
        {hasProfile && (
          <button
            onClick={() => setShowAddModal(true)}
            style={{ padding: '10px 18px', backgroundColor: '#16a34a', color: '#fff', fontWeight: 'bold', borderRadius: '8px', border: 'none', cursor: 'pointer' }}
          >
            + {t('registerMachine')}
          </button>
        )}
      </header>

      {success && <div style={{ padding: '12px 16px', backgroundColor: '#dcfce7', color: '#166534', borderRadius: '8px', marginBottom: '16px' }}>{success}</div>}
      {error && <div style={{ padding: '12px 16px', backgroundColor: '#fee2e2', color: '#991b1b', borderRadius: '8px', marginBottom: '16px' }}>{error}</div>}

      {/* Owner Profile Setup */}
      {!hasProfile && (
        <div style={{ backgroundColor: '#fff', border: '1px solid #e5e7eb', borderRadius: '12px', padding: '24px', marginBottom: '32px' }}>
          <h2 style={{ fontSize: '18px', fontWeight: 'bold', marginBottom: '8px' }}>Setup Machinery Owner Profile</h2>
          <p style={{ fontSize: '14px', color: '#6b7280', marginBottom: '16px' }}>Please complete your owner details to start registering machinery assets.</p>

          <form onSubmit={handleSaveProfile} style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
            <div>
              <label style={{ display: 'block', fontSize: '13px', fontWeight: 'bold', marginBottom: '4px' }}>Owner Full Name</label>
              <input
                type="text"
                required
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                style={{ width: '100%', padding: '10px', border: '1px solid #d1d5db', borderRadius: '6px' }}
              />
            </div>
            <div>
              <label style={{ display: 'block', fontSize: '13px', fontWeight: 'bold', marginBottom: '4px' }}>Business / Garage Address</label>
              <input
                type="text"
                required
                value={address}
                onChange={(e) => setAddress(e.target.value)}
                style={{ width: '100%', padding: '10px', border: '1px solid #d1d5db', borderRadius: '6px' }}
              />
            </div>
            <div>
              <label style={{ display: 'block', fontSize: '13px', fontWeight: 'bold', marginBottom: '4px' }}>Contact Mobile Number</label>
              <input
                type="text"
                required
                value={contactNumber}
                onChange={(e) => setContactNumber(e.target.value)}
                style={{ width: '100%', padding: '10px', border: '1px solid #d1d5db', borderRadius: '6px' }}
              />
            </div>
            <button
              type="submit"
              style={{ padding: '12px', backgroundColor: '#16a34a', color: '#fff', fontWeight: 'bold', borderRadius: '8px', border: 'none', cursor: 'pointer', marginTop: '8px' }}
            >
              Save Profile & Enable Fleet Registration
            </button>
          </form>
        </div>
      )}

      {/* Fleet Cards */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#6b7280' }}>Loading your machinery fleet...</div>
      ) : fleet.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#6b7280' }}>No machinery registered yet. Click "Register New Machinery" to add equipment.</div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '20px' }}>
          {fleet.map((machine) => (
            <div key={machine.id} style={{ border: '1px solid #e5e7eb', borderRadius: '12px', padding: '16px', backgroundColor: '#fff' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                <span style={{ fontSize: '12px', fontWeight: 'bold', padding: '4px 8px', borderRadius: '4px', backgroundColor: '#dbeafe', color: '#1e40af' }}>
                  {machine.category}
                </span>
                <span style={{ fontSize: '12px', fontWeight: 'bold', color: machine.status === 'ACTIVE' ? '#16a34a' : '#dc2626' }}>
                  {machine.status}
                </span>
              </div>
              <h3 style={{ fontSize: '18px', fontWeight: 'bold', color: '#111827' }}>{machine.name}</h3>
              {machine.modelNumber && <p style={{ fontSize: '13px', color: '#6b7280' }}>Model: {machine.modelNumber}</p>}
              <p style={{ fontSize: '14px', color: '#4b5563', margin: '6px 0' }}>📍 {machine.location}</p>
              <div style={{ marginTop: '8px', fontSize: '16px', fontWeight: 'bold', color: '#16a34a' }}>
                ₹{machine.dailyRate}/day
              </div>

              <button
                onClick={() => handleToggleStatus(machine.id, machine.status)}
                style={{ marginTop: '14px', padding: '8px 12px', backgroundColor: '#f3f4f6', color: '#374151', border: '1px solid #d1d5db', borderRadius: '6px', fontSize: '13px', fontWeight: 'bold', cursor: 'pointer', width: '100%' }}
              >
                Toggle Status ({machine.status === 'ACTIVE' ? 'Set Maintenance' : 'Set Active'})
              </button>
            </div>
          ))}
        </div>
      )}

      {/* Registration Modal */}
      {showAddModal && (
        <div style={{ position: 'fixed', inset: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '16px', zIndex: 1000 }}>
          <div style={{ backgroundColor: '#fff', borderRadius: '12px', padding: '24px', maxWidth: '450px', width: '100%' }}>
            <h2 style={{ fontSize: '20px', fontWeight: 'bold', marginBottom: '16px' }}>Register New Machinery</h2>
            <form onSubmit={handleRegisterMachine} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <div>
                <label style={{ display: 'block', fontSize: '13px', fontWeight: 'bold', marginBottom: '4px' }}>Machine Name</label>
                <input type="text" required value={name} onChange={(e) => setName(e.target.value)} style={{ width: '100%', padding: '8px', border: '1px solid #d1d5db', borderRadius: '6px' }} />
              </div>
              <div>
                <label style={{ display: 'block', fontSize: '13px', fontWeight: 'bold', marginBottom: '4px' }}>Category</label>
                <select value={category} onChange={(e) => setCategory(e.target.value as MachineryCategory)} style={{ width: '100%', padding: '8px', border: '1px solid #d1d5db', borderRadius: '6px' }}>
                  <option value="TRACTOR">Tractor</option>
                  <option value="HARVESTER">Harvester</option>
                  <option value="ROTAVATOR">Rotavator</option>
                  <option value="SEED_DRILL">Seed Drill</option>
                  <option value="CULTIVATOR">Cultivator</option>
                  <option value="PUMP">Pump</option>
                  <option value="THRESHER">Thresher</option>
                  <option value="SPRAYER">Sprayer</option>
                  <option value="OTHER">Other</option>
                </select>
              </div>
              <div>
                <label style={{ display: 'block', fontSize: '13px', fontWeight: 'bold', marginBottom: '4px' }}>Horsepower Rating (HP)</label>
                <input type="number" value={hpRating || ''} onChange={(e) => setHpRating(parseInt(e.target.value) || undefined)} style={{ width: '100%', padding: '8px', border: '1px solid #d1d5db', borderRadius: '6px' }} />
              </div>
              <div>
                <label style={{ display: 'block', fontSize: '13px', fontWeight: 'bold', marginBottom: '4px' }}>Location (Town/Village)</label>
                <input type="text" required value={location} onChange={(e) => setLocation(e.target.value)} style={{ width: '100%', padding: '8px', border: '1px solid #d1d5db', borderRadius: '6px' }} />
              </div>
              <div>
                <label style={{ display: 'block', fontSize: '13px', fontWeight: 'bold', marginBottom: '4px' }}>Daily Rate (₹)</label>
                <input type="number" required min="1" value={dailyRate} onChange={(e) => setDailyRate(parseFloat(e.target.value) || 0)} style={{ width: '100%', padding: '8px', border: '1px solid #d1d5db', borderRadius: '6px' }} />
              </div>
              <div>
                <label style={{ display: 'block', fontSize: '13px', fontWeight: 'bold', marginBottom: '4px' }}>Hourly Rate (₹ - Optional)</label>
                <input type="number" value={hourlyRate || ''} onChange={(e) => setHourlyRate(parseFloat(e.target.value) || undefined)} style={{ width: '100%', padding: '8px', border: '1px solid #d1d5db', borderRadius: '6px' }} />
              </div>
              <div style={{ display: 'flex', gap: '10px', marginTop: '12px' }}>
                <button type="button" onClick={() => setShowAddModal(false)} style={{ flex: 1, padding: '10px', backgroundColor: '#e5e7eb', border: 'none', borderRadius: '6px', cursor: 'pointer' }}>Cancel</button>
                <button type="submit" style={{ flex: 1, padding: '10px', backgroundColor: '#16a34a', color: '#fff', border: 'none', borderRadius: '6px', fontWeight: 'bold', cursor: 'pointer' }}>Register</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
