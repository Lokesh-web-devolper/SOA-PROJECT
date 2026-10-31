import React, { useState, useEffect } from 'react';
import bookingService from '../services/bookingService';
import Loader from '../components/Loader';
import ConfirmDialog from '../components/ConfirmDialog';
import { useNotification } from '../context/NotificationContext';
import { formatDateTime, formatCurrency } from '../utils/formatters';
import { CalendarCheck, Search, Filter, RefreshCw, XCircle, LogIn, LogOut, Car } from 'lucide-react';
import './ManageBookings.css';

export default function ManageBookings() {
  const { showSuccess, showError } = useNotification();
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [dialogConfig, setDialogConfig] = useState({ isOpen: false, title: '', message: '', isDestructive: false, onConfirm: null });

  const loadBookings = async () => {
    try {
      setLoading(true);
      const res = await bookingService.getAllBookings();
      const list = Array.isArray(res?.data) ? res.data : (Array.isArray(res) ? res : []);
      setBookings(list);
    } catch (err) {
      console.error('Failed to load reservations:', err);
      showError('Failed to fetch reservations');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadBookings();
  }, []);

  const handleAdminCheckIn = (id) => {
    setDialogConfig({
      isOpen: true,
      title: 'Admin Override: Check In',
      message: `Manually mark Booking #${id} as active/checked in? Slot will be marked OCCUPIED.`,
      isDestructive: false,
      onConfirm: async () => {
        try {
          await bookingService.checkIn(id);
          showSuccess(`Booking #${id} checked in successfully`);
          loadBookings();
        } catch (err) {
          showError(err.response?.data?.message || 'Check-in override failed');
        }
      }
    });
  };

  const handleAdminCheckOut = (id) => {
    setDialogConfig({
      isOpen: true,
      title: 'Admin Override: Check Out',
      message: `Manually check out Booking #${id}? Billing service will generate an invoice and slot will be released.`,
      isDestructive: false,
      onConfirm: async () => {
        try {
          await bookingService.checkOut(id);
          showSuccess(`Booking #${id} checked out. Automated invoice generated.`);
          loadBookings();
        } catch (err) {
          showError(err.response?.data?.message || 'Check-out override failed');
        }
      }
    });
  };

  const handleAdminCancel = (id) => {
    setDialogConfig({
      isOpen: true,
      title: 'Admin Override: Cancel Booking',
      message: `Force cancel Booking #${id}? The slot will be released back to the inventory pool immediately.`,
      isDestructive: true,
      onConfirm: async () => {
        try {
          await bookingService.cancelBooking(id);
          showSuccess(`Booking #${id} cancelled and slot released`);
          loadBookings();
        } catch (err) {
          showError(err.response?.data?.message || 'Cancellation override failed');
        }
      }
    });
  };

  const filtered = bookings.filter((b) => {
    const status = b.bookingStatus || b.status;
    const matchesStatus = statusFilter === 'ALL' || status === statusFilter;
    const q = search.toLowerCase();
    const matchesSearch =
      b.vehicleNumber?.toLowerCase().includes(q) ||
      b.customerName?.toLowerCase().includes(q) ||
      b.userEmail?.toLowerCase().includes(q) ||
      b.bookingReference?.toLowerCase().includes(q) ||
      String(b.id).includes(q) ||
      String(b.slotId).includes(q);
    return matchesStatus && matchesSearch;
  });

  return (
    <div className="manage-bookings-container">
      <div className="manage-header">
        <div>
          <h1 className="manage-title">Master Reservations Ledger</h1>
          <p className="manage-subtitle">Inspect, audit, and administer all driver reservations across the entire facility network</p>
        </div>
        <button className="btn-refresh" onClick={loadBookings}>
          <RefreshCw size={16} /> Refresh
        </button>
      </div>

      <div className="manage-toolbar">
        <div className="search-box">
          <Search size={18} className="search-icon" />
          <input
            type="text"
            placeholder="Search by Customer, Plate, Reference, Slot..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        <div className="status-filter-pills">
          {['ALL', 'CONFIRMED', 'ACTIVE', 'COMPLETED', 'CANCELLED'].map((st) => (
            <button
              key={st}
              className={`pill-btn ${statusFilter === st ? 'active' : ''}`}
              onClick={() => setStatusFilter(st)}
            >
              {st}
            </button>
          ))}
        </div>
      </div>

      {loading ? (
        <Loader message="Auditing reservations ledger..." />
      ) : (
        <div className="table-card">
          <div className="table-wrapper">
            <table className="manage-table">
              <thead>
                <tr>
                  <th>Booking Ref</th>
                  <th>Customer Info</th>
                  <th>Vehicle Plate</th>
                  <th>Facility & Zone</th>
                  <th>Slot Ref</th>
                  <th>Time Window</th>
                  <th>Amount</th>
                  <th>Status</th>
                  <th style={{ textAlign: 'right' }}>Admin Overrides</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((b) => {
                  const status = b.bookingStatus || b.status;
                  return (
                    <tr key={b.id}>
                      <td className="font-mono">{b.bookingReference || `#${b.id}`}</td>
                      <td>
                        <strong>{b.customerName || 'Customer'}</strong>
                        <div style={{ fontSize: '11px', color: '#64748b' }}>{b.userEmail}</div>
                      </td>
                      <td><span className="license-plate">{b.vehicleNumber || 'N/A'}</span></td>
                      <td>
                        <strong>{b.facilityName || `Facility #${b.facilityId}`}</strong>
                        <div style={{ fontSize: '11px', color: '#64748b' }}>{b.zoneName || `Zone #${b.zoneId}`}</div>
                      </td>
                      <td><strong className="text-blue">Slot {b.slotNumber || b.slotId}</strong></td>
                      <td className="time-window">
                        <span>{formatDateTime(b.startTime)}</span>
                        <span className="arrow">→</span>
                        <span>{formatDateTime(b.expectedEndTime)}</span>
                      </td>
                      <td>
                        <strong>{formatCurrency(b.finalAmount != null ? b.finalAmount : b.estimatedAmount)}</strong>
                      </td>
                      <td>
                        <span className={`status-pill ${status?.toLowerCase()}`}>
                          {status}
                        </span>
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        <div className="action-buttons-group">
                          {status === 'CONFIRMED' && (
                            <button
                              className="btn-table-action checkin"
                              onClick={() => handleAdminCheckIn(b.id)}
                              title="Force Check-In"
                            >
                              <LogIn size={15} /> Check-In
                            </button>
                          )}
                          {status === 'ACTIVE' && (
                            <button
                              className="btn-table-action checkout"
                              onClick={() => handleAdminCheckOut(b.id)}
                              title="Force Check-Out"
                            >
                              <LogOut size={15} /> Check-Out
                            </button>
                          )}
                          {(status === 'CONFIRMED' || status === 'ACTIVE') && (
                            <button
                              className="btn-table-action delete"
                              onClick={() => handleAdminCancel(b.id)}
                              title="Cancel Reservation"
                            >
                              <XCircle size={15} />
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  );
                })}
                {filtered.length === 0 && (
                  <tr>
                    <td colSpan={9} style={{ textAlign: 'center', padding: '3rem', color: '#64748b' }}>
                      No reservations match your filter.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      <ConfirmDialog
        isOpen={dialogConfig.isOpen}
        title={dialogConfig.title}
        message={dialogConfig.message}
        isDestructive={dialogConfig.isDestructive}
        onConfirm={dialogConfig.onConfirm}
        onCancel={() => setDialogConfig((prev) => ({ ...prev, isOpen: false }))}
      />
    </div>
  );
}
