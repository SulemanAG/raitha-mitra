import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { machineryApi } from '../api/machineryApi';
import type { RentalRequest } from '../types/machinery';

export const RentalRequestsPage: React.FC = () => {
  const { t } = useTranslation(['machinery', 'common']);

  const [activeTab, setActiveTab] = useState<'my' | 'owner'>('my');
  const [requests, setRequests] = useState<RentalRequest[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const fetchRequests = async () => {
    setLoading(true);
    setError(null);
    try {
      if (activeTab === 'my') {
        const res = await machineryApi.getMyRentalRequests();
        setRequests(res.content || []);
      } else {
        const res = await machineryApi.getOwnerRentalRequests();
        setRequests(res.content || []);
      }
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load rental requests');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRequests();
  }, [activeTab]);

  const handleAccept = async (id: string) => {
    setError(null);
    setSuccess(null);
    try {
      await machineryApi.acceptRentalRequest(id);
      setSuccess('Rental request accepted successfully!');
      fetchRequests();
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to accept rental request');
    }
  };

  const handleReject = async (id: string) => {
    setError(null);
    setSuccess(null);
    try {
      await machineryApi.rejectRentalRequest(id);
      setSuccess('Rental request declined.');
      fetchRequests();
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to decline rental request');
    }
  };

  const handleCancel = async (id: string) => {
    setError(null);
    setSuccess(null);
    try {
      await machineryApi.cancelRentalRequest(id);
      setSuccess('Rental request cancelled.');
      fetchRequests();
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to cancel rental request');
    }
  };

  return (
    <div style={{ maxWidth: '1000px', margin: '0 auto', padding: '24px 16px', fontFamily: 'sans-serif' }}>
      <header style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '24px', fontWeight: 'bold', color: '#1f2937' }}>{t('rentalRequests')}</h1>
        <p style={{ color: '#6b7280' }}>Track booking statuses, approvals, and request lifecycles</p>
      </header>

      {/* Tabs */}
      <div style={{ display: 'flex', borderBottom: '2px solid #e5e7eb', marginBottom: '24px' }}>
        <button
          onClick={() => setActiveTab('my')}
          style={{
            padding: '12px 24px',
            fontWeight: 'bold',
            border: 'none',
            background: 'none',
            borderBottom: activeTab === 'my' ? '3px solid #16a34a' : 'none',
            color: activeTab === 'my' ? '#16a34a' : '#6b7280',
            cursor: 'pointer',
          }}
        >
          My Rental Requests (Farmer)
        </button>
        <button
          onClick={() => setActiveTab('owner')}
          style={{
            padding: '12px 24px',
            fontWeight: 'bold',
            border: 'none',
            background: 'none',
            borderBottom: activeTab === 'owner' ? '3px solid #16a34a' : 'none',
            color: activeTab === 'owner' ? '#16a34a' : '#6b7280',
            cursor: 'pointer',
          }}
        >
          Received Requests (Owner)
        </button>
      </div>

      {success && <div style={{ padding: '12px 16px', backgroundColor: '#dcfce7', color: '#166534', borderRadius: '8px', marginBottom: '16px' }}>{success}</div>}
      {error && <div style={{ padding: '12px 16px', backgroundColor: '#fee2e2', color: '#991b1b', borderRadius: '8px', marginBottom: '16px' }}>{error}</div>}

      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#6b7280' }}>Loading rental requests...</div>
      ) : requests.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#6b7280' }}>No rental requests found in this tab.</div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {requests.map((req) => (
            <div
              key={req.id}
              style={{
                border: '1px solid #e5e7eb',
                borderRadius: '12px',
                padding: '20px',
                backgroundColor: '#fff',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                flexWrap: 'wrap',
                gap: '16px',
              }}
            >
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '6px' }}>
                  <h3 style={{ fontSize: '18px', fontWeight: 'bold', color: '#111827', margin: 0 }}>{req.machineryName}</h3>
                  <span
                    style={{
                      fontSize: '12px',
                      fontWeight: 'bold',
                      padding: '4px 10px',
                      borderRadius: '12px',
                      backgroundColor:
                        req.status === 'ACCEPTED'
                          ? '#dcfce7'
                          : req.status === 'REJECTED'
                          ? '#fee2e2'
                          : req.status === 'CANCELLED'
                          ? '#f3f4f6'
                          : '#fef3c7',
                      color:
                        req.status === 'ACCEPTED'
                          ? '#166534'
                          : req.status === 'REJECTED'
                          ? '#991b1b'
                          : req.status === 'CANCELLED'
                          ? '#4b5563'
                          : '#92400e',
                    }}
                  >
                    {req.status}
                  </span>
                </div>

                <p style={{ fontSize: '14px', color: '#4b5563', margin: '4px 0' }}>
                  📅 <strong>Period:</strong> {req.startDate} to {req.endDate} ({req.estimatedUnits} days)
                </p>
                <p style={{ fontSize: '14px', color: '#4b5563', margin: '4px 0' }}>
                  💰 <strong>Total Amount:</strong> ₹{req.totalAmount} (₹{req.ratePerUnit}/{req.rentalUnit.toLowerCase()})
                </p>

                {activeTab === 'owner' ? (
                  <p style={{ fontSize: '13px', color: '#6b7280', margin: '4px 0' }}>👤 Renter Contact: {req.renterMobile}</p>
                ) : (
                  <p style={{ fontSize: '13px', color: '#6b7280', margin: '4px 0' }}>👤 Owner Contact: {req.ownerMobile}</p>
                )}

                {req.renterNotes && <p style={{ fontSize: '13px', color: '#4b5563', fontStyle: 'italic', marginTop: '6px' }}>" {req.renterNotes} "</p>}
                {req.ownerNotes && <p style={{ fontSize: '13px', color: '#dc2626', fontStyle: 'italic', marginTop: '4px' }}>Owner Note: {req.ownerNotes}</p>}
              </div>

              {/* Action Buttons */}
              <div style={{ display: 'flex', gap: '10px' }}>
                {activeTab === 'owner' && req.status === 'PENDING' && (
                  <>
                    <button
                      onClick={() => handleAccept(req.id)}
                      style={{ padding: '8px 16px', backgroundColor: '#16a34a', color: '#fff', border: 'none', borderRadius: '6px', fontWeight: 'bold', cursor: 'pointer' }}
                    >
                      {t('accept')}
                    </button>
                    <button
                      onClick={() => handleReject(req.id)}
                      style={{ padding: '8px 16px', backgroundColor: '#dc2626', color: '#fff', border: 'none', borderRadius: '6px', fontWeight: 'bold', cursor: 'pointer' }}
                    >
                      {t('reject')}
                    </button>
                  </>
                )}

                {activeTab === 'my' && (req.status === 'PENDING' || req.status === 'ACCEPTED') && (
                  <button
                    onClick={() => handleCancel(req.id)}
                    style={{ padding: '8px 16px', backgroundColor: '#6b7280', color: '#fff', border: 'none', borderRadius: '6px', fontWeight: 'bold', cursor: 'pointer' }}
                  >
                    {t('cancel')}
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
