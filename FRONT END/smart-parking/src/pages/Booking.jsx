import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import { useNotification } from '../context/NotificationContext';
import { useAuth } from '../context/AuthContext';
import parkingService from '../services/parkingService';
import bookingService from '../services/bookingService';
import { formatCurrency, formatDateTime } from '../utils/formatters';
import Loader from '../components/Loader';
import { Calendar, Clock, MapPin, CheckCircle, ArrowRight, ShieldCheck, Car, AlertCircle, ArrowLeft } from 'lucide-react';
import './Booking.css';

const toInputFormat = (d) => {
  const pad = (n) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
};

const Booking = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const { showSuccess, showError } = useNotification();

  const paramFacilityId = searchParams.get('facilityId');
  const paramZoneId = searchParams.get('zoneId');
  const paramSlotId = searchParams.get('slotId');
  const paramStartTime = searchParams.get('startTime');
  const paramEndTime = searchParams.get('endTime');

  const [facilities, setFacilities] = useState([]);
  const [selectedFacilityId, setSelectedFacilityId] = useState(paramFacilityId || '');
  const [zones, setZones] = useState([]);
  const [selectedZoneId, setSelectedZoneId] = useState(paramZoneId || '');
  const [slots, setSlots] = useState([]);
  const [selectedSlotId, setSelectedSlotId] = useState(paramSlotId || '');

  // Default times or URL params
  const now = new Date();
  const defaultStart = new Date(now.getTime() + 30 * 60 * 1000);
  const defaultEnd = new Date(now.getTime() + 150 * 60 * 1000);

  const [startTime, setStartTime] = useState(paramStartTime || toInputFormat(defaultStart));
  const [expectedEndTime, setExpectedEndTime] = useState(paramEndTime || toInputFormat(defaultEnd));
  const [vehicleNumber, setVehicleNumber] = useState('');
  const [estimatedCost, setEstimatedCost] = useState(0);
  const [durationHours, setDurationHours] = useState(2);

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [confirmedBooking, setConfirmedBooking] = useState(null);

  // Load facilities
  useEffect(() => {
    const loadFacilities = async () => {
      try {
        setLoading(true);
        const res = await parkingService.getFacilities(true);
        if (res?.data) {
          setFacilities(res.data);
          if (!selectedFacilityId && res.data.length > 0) {
            setSelectedFacilityId(res.data[0].id.toString());
          }
        }
      } catch (err) {
        showError('Failed to load facilities list');
      } finally {
        setLoading(false);
      }
    };
    loadFacilities();
  }, []);

  // Load zones when facility changes
  useEffect(() => {
    if (!selectedFacilityId) {
      setZones([]);
      return;
    }
    const loadZones = async () => {
      try {
        const res = await parkingService.getZones(selectedFacilityId, true);
        if (res?.data) {
          setZones(res.data);
          if (res.data.length > 0 && (!selectedZoneId || !res.data.some(z => z.id.toString() === selectedZoneId))) {
            setSelectedZoneId(res.data[0].id.toString());
          }
        }
      } catch (err) {
        console.error('Failed to load zones:', err);
      }
    };
    loadZones();
  }, [selectedFacilityId]);

  // Load available slots when zone changes
  useEffect(() => {
    if (!selectedFacilityId || !selectedZoneId) {
      setSlots([]);
      return;
    }
    const loadSlots = async () => {
      try {
        const res = await parkingService.getSlots({
          facilityId: selectedFacilityId,
          zoneId: selectedZoneId
        });
        if (res?.data) {
          setSlots(res.data);
          if (res.data.length > 0 && (!selectedSlotId || !res.data.some(s => s.id.toString() === selectedSlotId))) {
            setSelectedSlotId(res.data[0].id.toString());
          }
        }
      } catch (err) {
        console.error('Failed to load slots:', err);
      }
    };
    loadSlots();
  }, [selectedFacilityId, selectedZoneId]);

  // Calculate estimated duration & price
  useEffect(() => {
    const selectedSlot = slots.find((s) => s.id.toString() === selectedSlotId);
    if (!selectedSlot || !startTime || !expectedEndTime) {
      setEstimatedCost(0);
      setDurationHours(0);
      return;
    }

    const start = new Date(startTime).getTime();
    const end = new Date(expectedEndTime).getTime();
    if (end > start) {
      const minutes = (end - start) / (1000 * 60);
      const hours = Math.max(1, Math.ceil(minutes / 60));
      const rate = selectedSlot.hourlyRate || 15;
      setDurationHours(hours);
      setEstimatedCost(hours * rate);
    } else {
      setEstimatedCost(0);
      setDurationHours(0);
    }
  }, [selectedSlotId, startTime, expectedEndTime, slots]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!selectedSlotId) {
      showError('Please select a parking slot.');
      return;
    }

    if (!vehicleNumber.trim()) {
      showError('Please enter your vehicle license plate number.');
      return;
    }

    if (new Date(expectedEndTime) <= new Date(startTime)) {
      showError('Expected departure time must be after the start time.');
      return;
    }

    if (new Date(startTime) < new Date(Date.now() - 5 * 60 * 1000)) {
      showError('Start time cannot be in the past.');
      return;
    }

    setSubmitting(true);
    try {
      const payload = {
        facilityId: parseInt(selectedFacilityId),
        zoneId: parseInt(selectedZoneId),
        slotId: parseInt(selectedSlotId),
        startTime: startTime + ':00',
        expectedEndTime: expectedEndTime + ':00',
        vehicleNumber: vehicleNumber.trim().toUpperCase(),
        customerName: user?.fullName || 'Customer'
      };

      const res = await bookingService.createBooking(payload);
      if (res && res.data) {
        setConfirmedBooking(res.data);
        showSuccess('Parking slot reserved successfully!');
      }
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to complete reservation. Slot may no longer be available for the requested time.';
      showError(msg);
    } finally {
      setSubmitting(false);
    }
  };

  const selectedSlot = slots.find((s) => s.id.toString() === selectedSlotId);
  const selectedFacility = facilities.find((f) => f.id.toString() === selectedFacilityId);
  const selectedZone = zones.find((z) => z.id.toString() === selectedZoneId);

  return (
    <div className="booking-page-container">
      {confirmedBooking ? (
        /* CONFIRMATION CARD */
        <div className="booking-confirmation-card">
          <div className="conf-icon-wrap">
            <CheckCircle size={44} />
          </div>
          <span className="conf-badge-confirmed">CONFIRMED RESERVATION</span>
          <h2 className="conf-title">Reservation Successfully Confirmed!</h2>
          <p className="conf-subtitle">
            Your parking slot is securely guaranteed in PostgreSQL. Present this reference upon arrival.
          </p>

          <div className="conf-reference-badge">
            <span className="conf-ref-label">Official Booking Reference</span>
            <span className="conf-ref-code">{confirmedBooking.bookingReference}</span>
          </div>

          <div className="conf-summary-grid">
            <div className="conf-item">
              <span className="conf-label">Customer Name</span>
              <span className="conf-val">{confirmedBooking.customerName || user?.fullName || 'Customer'}</span>
            </div>
            <div className="conf-item">
              <span className="conf-label">Vehicle License Plate</span>
              <span className="conf-val font-mono">{confirmedBooking.vehicleNumber}</span>
            </div>
            <div className="conf-item">
              <span className="conf-label">Facility</span>
              <span className="conf-val">{confirmedBooking.facilityName}</span>
            </div>
            <div className="conf-item">
              <span className="conf-label">Zone & Slot</span>
              <span className="conf-val">{confirmedBooking.zoneName} — <strong>Slot {confirmedBooking.slotNumber}</strong></span>
            </div>
            <div className="conf-item">
              <span className="conf-label">Reserved Window</span>
              <span className="conf-val">
                {formatDateTime(confirmedBooking.startTime)} → {formatDateTime(confirmedBooking.expectedEndTime)}
              </span>
            </div>
            <div className="conf-item">
              <span className="conf-label">Estimated Tariff</span>
              <span className="conf-val text-green font-bold">{formatCurrency(confirmedBooking.estimatedAmount)}</span>
            </div>
          </div>

          <div className="conf-actions">
            <button
              className="btn-conf-primary"
              onClick={() => navigate('/my-bookings')}
            >
              View in My Bookings
            </button>
            <button
              className="btn-conf-secondary"
              onClick={() => {
                setConfirmedBooking(null);
                navigate('/parking');
              }}
            >
              Find More Facilities
            </button>
          </div>
        </div>
      ) : (
        /* BOOKING FORM */
        <div className="booking-form-layout">
          <div className="booking-form-card">
            <div className="booking-header">
              <Link to="/parking" className="back-link-inline">
                <ArrowLeft size={16} /> Change Location
              </Link>
              <h1 className="booking-title">Reserve a Parking Slot</h1>
              <p className="booking-desc">
                Select your facility, schedule your duration, and reserve with guaranteed concurrency safety.
              </p>
            </div>

            {loading ? (
              <Loader message="Loading booking options..." />
            ) : (
              <form onSubmit={handleSubmit} className="reservation-form">
                {/* FACILITY SELECTION */}
                <div className="form-group">
                  <label htmlFor="select-facility">Parking Facility *</label>
                  <select
                    id="select-facility"
                    value={selectedFacilityId}
                    onChange={(e) => setSelectedFacilityId(e.target.value)}
                    required
                  >
                    {facilities.map((f) => (
                      <option key={f.id} value={f.id}>
                        {f.name} ({f.availableSlots} available) — {f.address}
                      </option>
                    ))}
                  </select>
                </div>

                {/* ZONE & SLOT SELECTION */}
                <div className="form-row-2">
                  <div className="form-group">
                    <label htmlFor="select-zone">Parking Zone *</label>
                    <select
                      id="select-zone"
                      value={selectedZoneId}
                      onChange={(e) => setSelectedZoneId(e.target.value)}
                      required
                      disabled={zones.length === 0}
                    >
                      {zones.map((z) => (
                        <option key={z.id} value={z.id}>
                          {z.name}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="form-group">
                    <label htmlFor="select-slot">Parking Slot *</label>
                    {slots.length === 0 ? (
                      <div className="no-slots-alert">
                        No slots in this zone.
                      </div>
                    ) : (
                      <select
                        id="select-slot"
                        value={selectedSlotId}
                        onChange={(e) => setSelectedSlotId(e.target.value)}
                        required
                      >
                        {slots.map((s) => (
                          <option key={s.id} value={s.id}>
                            Slot {s.slotNumber} ({s.slotType}) — {formatCurrency(s.hourlyRate || 15)}/hr
                          </option>
                        ))}
                      </select>
                    )}
                  </div>
                </div>

                {/* VEHICLE LICENSE PLATE */}
                <div className="form-group">
                  <label htmlFor="vehicle-plate">Vehicle License Plate Number *</label>
                  <div className="input-with-icon">
                    <Car size={18} className="input-icon" />
                    <input
                      id="vehicle-plate"
                      type="text"
                      placeholder="e.g. CA-7XYZ99, MH-02-CB-1234"
                      value={vehicleNumber}
                      onChange={(e) => setVehicleNumber(e.target.value.toUpperCase())}
                      required
                      autoComplete="off"
                    />
                  </div>
                </div>

                {/* DATE TIME SELECTION */}
                <div className="form-row-2">
                  <div className="form-group">
                    <label htmlFor="arrival-time">Arrival Date & Time *</label>
                    <input
                      id="arrival-time"
                      type="datetime-local"
                      value={startTime}
                      onChange={(e) => setStartTime(e.target.value)}
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="departure-time">Expected Departure Date & Time *</label>
                    <input
                      id="departure-time"
                      type="datetime-local"
                      value={expectedEndTime}
                      onChange={(e) => setExpectedEndTime(e.target.value)}
                      required
                    />
                  </div>
                </div>

                {/* PRICE ESTIMATE SUMMARY BOX */}
                <div className="estimate-summary-box">
                  <div className="estimate-row">
                    <span>Selected Facility & Zone:</span>
                    <strong>{selectedFacility?.name} ({selectedZone?.name})</strong>
                  </div>
                  <div className="estimate-row">
                    <span>Slot Identifier:</span>
                    <strong className="text-blue">Slot {selectedSlot?.slotNumber} ({selectedSlot?.slotType})</strong>
                  </div>
                  <div className="estimate-row">
                    <span>Hourly Rate:</span>
                    <strong>{formatCurrency(selectedSlot?.hourlyRate || selectedFacility?.startingPrice || 15)}/hr</strong>
                  </div>
                  <div className="estimate-row">
                    <span>Estimated Duration:</span>
                    <strong>{durationHours} billable hours</strong>
                  </div>
                  <div className="estimate-divider"></div>
                  <div className="estimate-total-row">
                    <span>Estimated Total Amount:</span>
                    <span className="estimate-total-amount">{formatCurrency(estimatedCost)}</span>
                  </div>
                  <span className="estimate-notice">
                    * Final charges are computed by the Billing Service based on actual exit time with a 15-minute grace period policy.
                  </span>
                </div>

                <button
                  type="submit"
                  className="btn-submit-booking"
                  disabled={submitting || slots.length === 0}
                >
                  {submitting ? 'Verifying & Confirming Slot...' : 'Confirm & Guarantee Slot'}
                  <ArrowRight size={18} />
                </button>
              </form>
            )}
          </div>

          {/* SIDE INFO CARD */}
          <div className="booking-info-sidebar">
            <div className="info-card">
              <div className="info-icon-badge">
                <ShieldCheck size={20} />
              </div>
              <h3>Pessimistic Double-Booking Shield</h3>
              <p>
                ParkGrid applies serializable transactions and database locking. Once confirmed, this slot cannot be double-booked by any other customer for overlapping time windows.
              </p>
            </div>

            <div className="info-card">
              <div className="info-icon-badge">
                <Clock size={20} />
              </div>
              <h3>Fair Grace Period</h3>
              <p>
                Visits within the configured 15-minute grace period incur zero charges. Overages are billed rounded to the next hourly tier.
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Booking;
