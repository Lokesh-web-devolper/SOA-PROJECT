import React from 'react';
import { Calendar, Clock, MapPin, CheckCircle, XCircle, PlayCircle, StopCircle, FileText } from 'lucide-react';
import { formatCurrency, formatDateTime } from '../utils/formatters';
import './BookingCard.css';

const BookingCard = ({
  booking,
  onCheckIn,
  onCheckOut,
  onCancel,
  onViewInvoice,
  isAdmin = false
}) => {
  const getStatusBadge = (status) => {
    switch (status) {
      case 'CONFIRMED':
        return <span className="booking-badge badge-confirmed">Confirmed</span>;
      case 'ACTIVE':
        return <span className="booking-badge badge-active">Active (Parked)</span>;
      case 'COMPLETED':
        return <span className="booking-badge badge-completed">Completed</span>;
      case 'CANCELLED':
        return <span className="booking-badge badge-cancelled">Cancelled</span>;
      default:
        return <span className="booking-badge badge-pending">{status}</span>;
    }
  };

  return (
    <div className="booking-card">
      <div className="booking-card-header">
        <div>
          <span className="booking-ref">{booking.bookingReference}</span>
          <h4 className="booking-facility-name">{booking.facilityName}</h4>
        </div>
        {getStatusBadge(booking.bookingStatus)}
      </div>

      <div className="booking-details-grid">
        <div className="detail-item">
          <MapPin size={16} className="detail-icon" />
          <span>Zone: <strong>{booking.zoneName}</strong> (Slot: <strong>{booking.slotNumber}</strong>)</span>
        </div>
        <div className="detail-item">
          <Calendar size={16} className="detail-icon" />
          <span>Start: <strong>{formatDateTime(booking.startTime)}</strong></span>
        </div>
        <div className="detail-item">
          <Clock size={16} className="detail-icon" />
          <span>Expected End: <strong>{formatDateTime(booking.expectedEndTime)}</strong></span>
        </div>
        {booking.actualEntryTime && (
          <div className="detail-item">
            <PlayCircle size={16} className="detail-icon text-green" />
            <span>Checked In: <strong>{formatDateTime(booking.actualEntryTime)}</strong></span>
          </div>
        )}
        {booking.actualExitTime && (
          <div className="detail-item">
            <StopCircle size={16} className="detail-icon text-blue" />
            <span>Checked Out: <strong>{formatDateTime(booking.actualExitTime)}</strong></span>
          </div>
        )}
      </div>

      <div className="booking-card-footer">
        <div className="booking-price-info">
          <span className="price-label">
            {booking.bookingStatus === 'COMPLETED' ? 'Final Amount:' : 'Estimated Rate:'}
          </span>
          <span className="price-value">
            {formatCurrency(booking.finalAmount != null ? booking.finalAmount : booking.estimatedAmount)}
          </span>
        </div>

        <div className="booking-card-actions">
          {booking.bookingStatus === 'CONFIRMED' && onCheckIn && (
            <button className="btn-action checkin-btn" onClick={() => onCheckIn(booking)}>
              <CheckCircle size={15} />
              Check In
            </button>
          )}

          {booking.bookingStatus === 'ACTIVE' && onCheckOut && (
            <button className="btn-action checkout-btn" onClick={() => onCheckOut(booking)}>
              <StopCircle size={15} />
              Check Out
            </button>
          )}

          {(booking.bookingStatus === 'CONFIRMED' || booking.bookingStatus === 'PENDING') && onCancel && (
            <button className="btn-action cancel-btn" onClick={() => onCancel(booking)}>
              <XCircle size={15} />
              Cancel
            </button>
          )}

          {booking.bookingStatus === 'COMPLETED' && onViewInvoice && (
            <button className="btn-action invoice-btn" onClick={() => onViewInvoice(booking)}>
              <FileText size={15} />
              Invoice
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default BookingCard;
