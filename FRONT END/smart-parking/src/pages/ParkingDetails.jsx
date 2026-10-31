import React, { useState, useEffect, useCallback } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import parkingService from '../services/parkingService';
import bookingService from '../services/bookingService';
import { formatCurrency } from '../utils/formatters';
import ParkingSlotCard from '../components/ParkingSlotCard';
import Loader from '../components/Loader';
import EmptyState from '../components/EmptyState';
import { MapPin, Navigation, ArrowLeft, ArrowRight, Zap, Accessibility, Car, Clock, Calendar, RefreshCw } from 'lucide-react';
import './ParkingDetails.css';

const toInputFormat = (d) => {
  const pad = (n) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
};

const ParkingDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [facility, setFacility] = useState(null);
  const [zones, setZones] = useState([]);
  const [selectedZoneId, setSelectedZoneId] = useState(null);
  const [rawSlots, setRawSlots] = useState([]);
  const [unavailableSlotIds, setUnavailableSlotIds] = useState([]);
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [loading, setLoading] = useState(true);
  const [checkingAvailability, setCheckingAvailability] = useState(false);

  // Time window states (Default: start in 30 mins, duration 2 hours)
  const now = new Date();
  const defaultStart = new Date(now.getTime() + 30 * 60 * 1000);
  const defaultEnd = new Date(now.getTime() + 150 * 60 * 1000);

  const [startTime, setStartTime] = useState(toInputFormat(defaultStart));
  const [expectedEndTime, setExpectedEndTime] = useState(toInputFormat(defaultEnd));

  // Initial facility load
  useEffect(() => {
    const fetchFacilityData = async () => {
      try {
        setLoading(true);
        const [facRes, zonesRes] = await Promise.all([
          parkingService.getFacilityById(id),
          parkingService.getZones(id, true)
        ]);

        if (facRes?.data) setFacility(facRes.data);
        if (zonesRes?.data && zonesRes.data.length > 0) {
          setZones(zonesRes.data);
          setSelectedZoneId(zonesRes.data[0].id);
        }
      } catch (err) {
        console.error('Failed to load facility details:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchFacilityData();
  }, [id]);

  // Load raw slots for the selected zone
  const fetchZoneSlots = useCallback(async () => {
    if (!id) return;
    try {
      const slotsRes = await parkingService.getSlots({
        facilityId: id,
        zoneId: selectedZoneId || undefined
      });
      if (slotsRes?.data) {
        setRawSlots(slotsRes.data);
      }
    } catch (err) {
      console.error('Failed to load slots:', err);
    }
  }, [id, selectedZoneId]);

  useEffect(() => {
    fetchZoneSlots();
  }, [fetchZoneSlots]);

  // Check overlapping reservations for the requested time window
  const checkTimeAvailability = useCallback(async () => {
    if (!id || !startTime || !expectedEndTime) return;
    try {
      setCheckingAvailability(true);
      const res = await bookingService.checkAvailability(
        id,
        startTime + ':00',
        expectedEndTime + ':00'
      );
      if (res?.data?.unavailableSlotIds) {
        setUnavailableSlotIds(res.data.unavailableSlotIds);
      } else {
        setUnavailableSlotIds([]);
      }
    } catch (err) {
      console.warn('Could not query time availability from booking service:', err);
      setUnavailableSlotIds([]);
    } finally {
      setCheckingAvailability(false);
    }
  }, [id, startTime, expectedEndTime]);

  useEffect(() => {
    checkTimeAvailability();
  }, [checkTimeAvailability]);

  // Periodic polling every 12 seconds to ensure multi-customer consistency
  useEffect(() => {
    const interval = setInterval(() => {
      checkTimeAvailability();
      fetchZoneSlots();
    }, 12000);
    return () => clearInterval(interval);
  }, [checkTimeAvailability, fetchZoneSlots]);

  // Merge slot physical status with active booking reservations
  const computedSlots = rawSlots.map((slot) => {
    let effectiveStatus = slot.status || 'AVAILABLE';

    if (slot.active === false || slot.status === 'INACTIVE') {
      effectiveStatus = 'INACTIVE';
    } else if (slot.status === 'MAINTENANCE') {
      effectiveStatus = 'MAINTENANCE';
    } else if (slot.status === 'OCCUPIED') {
      effectiveStatus = 'OCCUPIED';
    } else if (unavailableSlotIds.includes(slot.id)) {
      effectiveStatus = 'RESERVED';
    } else {
      effectiveStatus = 'AVAILABLE';
    }

    return {
      ...slot,
      status: effectiveStatus,
      hourlyRate: slot.hourlyRate || facility?.startingPrice || 15
    };
  });

  const handleSlotSelect = (slot) => {
    if (slot.status === 'AVAILABLE') {
      setSelectedSlot(slot);
    }
  };

  const handleProceedToBooking = () => {
    if (!selectedSlot) return;
    navigate(`/book/${selectedSlot.id}?facilityId=${facility.id}&zoneId=${selectedSlot.zoneId}&slotId=${selectedSlot.id}&startTime=${startTime}&endTime=${expectedEndTime}`);
  };

  // Compute live counts
  const availableCount = computedSlots.filter((s) => s.status === 'AVAILABLE').length;
  const reservedCount = computedSlots.filter((s) => s.status === 'RESERVED').length;
  const occupiedCount = computedSlots.filter((s) => s.status === 'OCCUPIED').length;

  if (loading) {
    return <Loader message="Loading facility slot telemetry and live reservations..." fullScreen />;
  }

  if (!facility) {
    return (
      <div className="not-found-container">
        <EmptyState
          title="Facility not found"
          description="The requested parking facility does not exist or has been deactivated."
          actionLabel="Back to Locations"
          onAction={() => navigate('/parking')}
        />
      </div>
    );
  }

  return (
    <div className="details-container">
      <Link to="/parking" className="back-link">
        <ArrowLeft size={16} /> Back to Locations
      </Link>

      {/* FACILITY HEADER CARD */}
      <div className="facility-overview-card">
        <div className="facility-overview-main">
          <span className="overview-tag">PARKING FACILITY OVERVIEW</span>
          <h1 className="facility-header-title">{facility.name}</h1>
          <p className="facility-header-address">
            <MapPin size={16} />
            {facility.address}
            {facility.latitude && facility.longitude && (
              <span className="coords-badge">
                <Navigation size={12} /> {facility.latitude}, {facility.longitude}
              </span>
            )}
          </p>
          {facility.description && (
            <p className="facility-header-desc">{facility.description}</p>
          )}
        </div>

        <div className="facility-stats-panel">
          <div className="f-stat-box">
            <span className="stat-box-num text-green">{availableCount}</span>
            <span className="stat-box-lbl">Available Now</span>
          </div>
          <div className="f-stat-box">
            <span className="stat-box-num text-orange">{reservedCount}</span>
            <span className="stat-box-lbl">Reserved</span>
          </div>
          <div className="f-stat-box">
            <span className="stat-box-num">{facility.totalSlots}</span>
            <span className="stat-box-lbl">Total Capacity</span>
          </div>
          <div className="f-stat-box">
            <span className="stat-box-num">{formatCurrency(facility.startingPrice || 15)}/hr</span>
            <span className="stat-box-lbl">Tariff Rate</span>
          </div>
        </div>
      </div>

      {/* TIME WINDOW CONTROLLER BAR */}
      <div className="time-window-card">
        <div className="time-window-header">
          <div className="time-window-title">
            <Clock size={18} className="time-icon" />
            <span>Select Reservation Window</span>
            {checkingAvailability && <span className="sync-pill">Checking Live Availability...</span>}
          </div>
          <button className="btn-sync-refresh" onClick={() => { checkTimeAvailability(); fetchZoneSlots(); }} title="Refresh live availability">
            <RefreshCw size={14} /> Refresh
          </button>
        </div>

        <div className="time-window-controls">
          <div className="time-input-group">
            <label htmlFor="slot-start-time">Entry Date & Time</label>
            <input
              id="slot-start-time"
              type="datetime-local"
              value={startTime}
              onChange={(e) => setStartTime(e.target.value)}
            />
          </div>

          <div className="time-input-group">
            <label htmlFor="slot-end-time">Expected Exit Date & Time</label>
            <input
              id="slot-end-time"
              type="datetime-local"
              value={expectedEndTime}
              onChange={(e) => setExpectedEndTime(e.target.value)}
            />
          </div>
        </div>
      </div>

      {/* ZONES NAVIGATION */}
      <div className="zones-tabs-bar">
        <span className="zones-bar-label">Zones:</span>
        {zones.map((zone) => (
          <button
            key={zone.id}
            className={`zone-tab-btn ${selectedZoneId === zone.id ? 'active' : ''}`}
            onClick={() => {
              setSelectedZoneId(zone.id);
              setSelectedSlot(null);
            }}
          >
            {zone.name}
          </button>
        ))}
      </div>

      {/* LEGEND BAR */}
      <div className="slots-legend-bar">
        <div className="legend-items-group">
          <div className="legend-item"><span className="legend-dot green"></span> AVAILABLE (Green)</div>
          <div className="legend-item"><span className="legend-dot orange"></span> RESERVED (Orange)</div>
          <div className="legend-item"><span className="legend-dot red"></span> OCCUPIED (Red)</div>
          <div className="legend-item"><span className="legend-dot grey"></span> MAINTENANCE (Grey)</div>
        </div>
        <div className="legend-items-group">
          <div className="legend-type-item"><Car size={14} /> Standard</div>
          <div className="legend-type-item"><Zap size={14} className="text-green" /> EV Station</div>
          <div className="legend-type-item"><Accessibility size={14} className="text-blue" /> Priority Accessible</div>
        </div>
      </div>

      {/* SLOTS GRID */}
      {computedSlots.length === 0 ? (
        <EmptyState
          title="No slots registered"
          description="No parking slots are configured in this zone yet."
        />
      ) : (
        <div className="slots-interactive-grid">
          {computedSlots.map((slot) => (
            <ParkingSlotCard
              key={slot.id}
              slot={slot}
              isSelected={selectedSlot?.id === slot.id}
              onSelect={handleSlotSelect}
            />
          ))}
        </div>
      )}

      {/* SELECTED SLOT ACTION DRAWER */}
      {selectedSlot && (
        <div className="selected-slot-banner">
          <div className="selected-slot-info">
            <span className="slot-badge-selected">Selected: Slot {selectedSlot.slotNumber}</span>
            <span className="selected-slot-details">
              Zone: <strong>{selectedSlot.zoneName || 'Zone A'}</strong> • Type: <strong>{selectedSlot.slotType}</strong> • Rate: <strong>{formatCurrency(selectedSlot.hourlyRate)}/hr</strong>
            </span>
          </div>
          <button className="btn-confirm-reserve" onClick={handleProceedToBooking}>
            Proceed to Book Slot {selectedSlot.slotNumber}
            <ArrowRight size={16} />
          </button>
        </div>
      )}
    </div>
  );
};

export default ParkingDetails;
