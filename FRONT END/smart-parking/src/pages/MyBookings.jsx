import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import bookingService from '../services/bookingService';
import BookingCard from '../components/BookingCard';
import Loader from '../components/Loader';
import EmptyState from '../components/EmptyState';
import ConfirmDialog from '../components/ConfirmDialog';
import { useNotification } from '../context/NotificationContext';
import { Bookmark, Filter, RefreshCw, Car } from 'lucide-react';
import './MyBookings.css';

export default function MyBookings() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const { showSuccess, showError } = useNotification();
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('ALL');
  const [dialogConfig, setDialogConfig] = useState({ isOpen: false, title: '', message: '', isDestructive: false, onConfirm: null });

  const fetchBookings = async () => {
    try {
      setLoading(true);
      const res = await bookingService.getMyBookings();
      const list = Array.isArray(res?.data) ? res.data : (Array.isArray(res) ? res : []);
      setBookings(list);
    } catch (err) {
      console.error('Failed to load bookings:', err);
      showError('Failed to load your bookings');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBookings();
  }, []);

  const handleCheckIn = (bookingOrId) => {
    const bookingId = typeof bookingOrId === 'object' ? bookingOrId.id : bookingOrId;
    setDialogConfig({
      isOpen: true,
      title: 'Confirm Check-In',
      message: 'Are you at the parking facility ready to occupy your reserved slot?',
      isDestructive: false,
      onConfirm: async () => {
        try {
          await bookingService.checkIn(bookingId);
          showSuccess('Checked in successfully! Slot is now occupied.');
          fetchBookings();
        } catch (err) {
          showError(err.response?.data?.message || 'Check-in failed');
        }
      }
    });
  };

  const handleCheckOut = (bookingOrId) => {
    const bookingId = typeof bookingOrId === 'object' ? bookingOrId.id : bookingOrId;
    setDialogConfig({
      isOpen: true,
      title: 'Confirm Check-Out',
      message: 'Are you leaving the parking slot? An automated invoice will be generated based on your actual parking duration.',
      isDestructive: false,
      onConfirm: async () => {
        try {
          await bookingService.checkOut(bookingId);
          showSuccess('Checked out successfully! Invoice generated.');
          fetchBookings();
        } catch (err) {
          showError(err.response?.data?.message || 'Check-out failed');
        }
      }
    });
  };

  const handleCancel = (bookingOrId) => {
    const bookingId = typeof bookingOrId === 'object' ? bookingOrId.id : bookingOrId;
    setDialogConfig({
      isOpen: true,
      title: 'Cancel Reservation',
      message: 'Are you sure you want to cancel this booking? The slot will be released back to other drivers immediately.',
      isDestructive: true,
      onConfirm: async () => {
        try {
          await bookingService.cancelBooking(bookingId);
          showSuccess('Booking cancelled successfully');
          fetchBookings();
        } catch (err) {
          showError(err.response?.data?.message || 'Cancellation failed');
        }
      }
    });
  };

  const handleViewInvoice = (booking) => {
    navigate('/billing');
  };

  const filteredBookings = bookings.filter((b) => {
    const status = b.bookingStatus || b.status;
    if (activeTab === 'ALL') return true;
    return status === activeTab;
  });

  return (
    <div className="my-bookings-container">
      <div className="my-bookings-header">
        <div>
          <h1 className="my-bookings-title">My Parking Reservations</h1>
          <p className="my-bookings-subtitle">Track, check-in, check-out, and view invoices for all your reserved parking slots</p>
        </div>
        <button className="btn-refresh" onClick={fetchBookings} title="Refresh bookings">
          <RefreshCw size={18} /> Refresh
        </button>
      </div>

      <div className="filter-tabs">
        {['ALL', 'CONFIRMED', 'ACTIVE', 'COMPLETED', 'CANCELLED'].map((tab) => {
          const count = tab === 'ALL'
            ? bookings.length
            : bookings.filter((b) => (b.bookingStatus || b.status) === tab).length;
          return (
            <button
              key={tab}
              className={`tab-btn ${activeTab === tab ? 'active' : ''}`}
              onClick={() => setActiveTab(tab)}
            >
              {tab.charAt(0) + tab.slice(1).toLowerCase()}
              <span className="tab-count">{count}</span>
            </button>
          );
        })}
      </div>

      {loading ? (
        <Loader message="Loading your bookings from ledger..." />
      ) : filteredBookings.length === 0 ? (
        <EmptyState
          icon={Bookmark}
          title="No reservations found"
          description={
            activeTab === 'ALL'
              ? "You haven't reserved any parking slots yet. Explore available locations to book your spot."
              : `You have no ${activeTab.toLowerCase()} reservations at the moment.`
          }
          actionLabel={activeTab === 'ALL' ? 'Find Parking' : null}
          onAction={activeTab === 'ALL' ? () => navigate('/parking') : null}
        />
      ) : (
        <div className="bookings-grid">
          {filteredBookings.map((booking) => (
            <BookingCard
              key={booking.id}
              booking={{
                ...booking,
                bookingStatus: booking.bookingStatus || booking.status
              }}
              onCheckIn={handleCheckIn}
              onCheckOut={handleCheckOut}
              onCancel={handleCancel}
              onViewInvoice={handleViewInvoice}
            />
          ))}
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
