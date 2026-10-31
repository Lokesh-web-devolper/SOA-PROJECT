import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useNotification } from '../context/NotificationContext';
import bookingService from '../services/bookingService';
import parkingService from '../services/parkingService';
import StatCard from '../components/StatCard';
import BookingCard from '../components/BookingCard';
import Loader from '../components/Loader';
import EmptyState from '../components/EmptyState';
import ConfirmDialog from '../components/ConfirmDialog';
import {
  CalendarCheck,
  PlayCircle,
  CheckCircle2,
  Building,
  PlusCircle,
  MapPin,
  ArrowRight
} from 'lucide-react';
import './CustomerDashboard.css';

const CustomerDashboard = () => {
  const { user } = useAuth();
  const { showSuccess, showError } = useNotification();
  const navigate = useNavigate();

  const [bookings, setBookings] = useState([]);
  const [facilities, setFacilities] = useState([]);
  const [loading, setLoading] = useState(true);

  // Dialog state
  const [dialogConfig, setDialogConfig] = useState({
    isOpen: false,
    title: '',
    message: '',
    confirmText: '',
    isDanger: false,
    action: null
  });

  const fetchData = async () => {
    try {
      setLoading(true);
      const [bookingsRes, facilitiesRes] = await Promise.all([
        bookingService.getMyBookings(),
        parkingService.getFacilities(true)
      ]);
      if (bookingsRes?.data) setBookings(bookingsRes.data);
      if (facilitiesRes?.data) setFacilities(facilitiesRes.data);
    } catch (err) {
      console.error('Failed to load customer dashboard data:', err);
      showError('Failed to refresh dashboard data.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleCheckIn = (booking) => {
    setDialogConfig({
      isOpen: true,
      title: 'Confirm Check-In',
      message: `Are you arriving at ${booking.facilityName}, Slot ${booking.slotNumber}?`,
      confirmText: 'Check In Now',
      isDanger: false,
      action: async () => {
        try {
          await bookingService.checkIn(booking.id);
          showSuccess('Checked in successfully! Slot is now occupied.');
          fetchData();
        } catch (err) {
          showError(err.response?.data?.message || 'Check-in failed');
        }
      }
    });
  };

  const handleCheckOut = (booking) => {
    setDialogConfig({
      isOpen: true,
      title: 'Confirm Check-Out',
      message: `Are you departing from ${booking.facilityName}? The final billing fee will be computed automatically.`,
      confirmText: 'Check Out & Generate Bill',
      isDanger: false,
      action: async () => {
        try {
          await bookingService.checkOut(booking.id);
          showSuccess('Checked out successfully! Final bill generated.');
          fetchData();
          navigate('/billing');
        } catch (err) {
          showError(err.response?.data?.message || 'Check-out failed');
        }
      }
    });
  };

  const handleCancel = (booking) => {
    setDialogConfig({
      isOpen: true,
      title: 'Cancel Reservation',
      message: `Are you sure you want to cancel booking ${booking.bookingReference}? This action cannot be undone.`,
      confirmText: 'Yes, Cancel Booking',
      isDanger: true,
      action: async () => {
        try {
          await bookingService.cancelBooking(booking.id);
          showSuccess('Booking cancelled successfully.');
          fetchData();
        } catch (err) {
          showError(err.response?.data?.message || 'Cancellation failed');
        }
      }
    });
  };

  const handleViewInvoice = (booking) => {
    navigate('/billing');
  };

  const totalBookings = bookings.length;
  const activeBookings = bookings.filter((b) => b.bookingStatus === 'ACTIVE').length;
  const completedBookings = bookings.filter((b) => b.bookingStatus === 'COMPLETED').length;
  const upcomingBookings = bookings.filter((b) => b.bookingStatus === 'CONFIRMED').length;

  return (
    <div className="customer-dashboard-view">
      <main className="dashboard-content-container">
        {/* WELCOME BANNER */}
        <div className="welcome-banner">
          <div className="welcome-text">
            <h1 className="welcome-title">Welcome back, {user?.fullName}!</h1>
            <p className="welcome-subtitle">
              Manage your active reservations, find available parking slots, and view invoices.
            </p>
          </div>
          <Link to="/booking" className="btn-quick-reserve">
            <PlusCircle size={18} />
            Reserve a Slot
          </Link>
        </div>

        {/* METRIC STATS */}
        <div className="stats-cards-grid">
          <StatCard
            title="Total Bookings"
            value={totalBookings}
            subtitle="Lifetime reservations"
            icon={CalendarCheck}
            color="blue"
          />
          <StatCard
            title="Active (Parked)"
            value={activeBookings}
            subtitle="Vehicles currently parked"
            icon={PlayCircle}
            color="green"
          />
          <StatCard
            title="Completed"
            value={completedBookings}
            subtitle="Successfully billed visits"
            icon={CheckCircle2}
            color="purple"
          />
          <StatCard
            title="Facilities Online"
            value={facilities.length}
            subtitle="Live parking locations"
            icon={Building}
            color="amber"
          />
        </div>

        {/* RECENT BOOKINGS & NEARBY FACILITIES */}
        <div className="dashboard-two-col">
          {/* Recent Bookings Section */}
          <div className="dashboard-col-main">
            <div className="card-section-header">
              <h2 className="section-heading">Recent Bookings</h2>
              <Link to="/my-bookings" className="link-view-all">
                View all bookings <ArrowRight size={14} />
              </Link>
            </div>

            {loading ? (
              <Loader message="Loading reservations..." />
            ) : bookings.length === 0 ? (
              <EmptyState
                title="No bookings yet"
                description="You don't have any reservations. Find a nearby location and book a parking slot in advance."
                actionLabel="Book a Slot Now"
                onAction={() => navigate('/booking')}
              />
            ) : (
              <div className="bookings-list">
                {bookings.slice(0, 3).map((booking) => (
                  <BookingCard
                    key={booking.id}
                    booking={booking}
                    onCheckIn={handleCheckIn}
                    onCheckOut={handleCheckOut}
                    onCancel={handleCancel}
                    onViewInvoice={handleViewInvoice}
                  />
                ))}
              </div>
            )}
          </div>

          {/* Available Nearby Facilities */}
          <div className="dashboard-col-side">
            <div className="card-section-header">
              <h2 className="section-heading">Live Locations</h2>
              <Link to="/parking" className="link-view-all">
                Browse <ArrowRight size={14} />
              </Link>
            </div>

            <div className="side-facilities-list">
              {facilities.slice(0, 4).map((f) => (
                <div key={f.id} className="side-facility-item">
                  <div className="side-facility-header">
                    <h4 className="side-f-name">{f.name}</h4>
                    <span className="side-f-badge">
                      {f.availableSlots} free
                    </span>
                  </div>
                  <p className="side-f-address">
                    <MapPin size={13} />
                    {f.address}
                  </p>
                  <Link to={`/parking/${f.id}`} className="side-f-link">
                    Select Slot & Reserve →
                  </Link>
                </div>
              ))}
            </div>
          </div>
        </div>
      </main>

      <ConfirmDialog
        isOpen={dialogConfig.isOpen}
        title={dialogConfig.title}
        message={dialogConfig.message}
        confirmText={dialogConfig.confirmText}
        isDanger={dialogConfig.isDanger}
        onConfirm={() => {
          if (dialogConfig.action) dialogConfig.action();
          setDialogConfig((prev) => ({ ...prev, isOpen: false }));
        }}
        onCancel={() => setDialogConfig((prev) => ({ ...prev, isOpen: false }))}
      />
    </div>
  );
};

export default CustomerDashboard;
