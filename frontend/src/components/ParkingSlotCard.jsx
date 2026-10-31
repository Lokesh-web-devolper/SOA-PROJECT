import React from 'react';
import { Zap, Accessibility, Car } from 'lucide-react';
import { formatCurrency } from '../utils/formatters';
import './ParkingSlotCard.css';

const ParkingSlotCard = ({ slot, isSelected, onSelect }) => {
  const getStatusColor = () => {
    switch (slot.status) {
      case 'AVAILABLE': return 'status-available';
      case 'OCCUPIED': return 'status-occupied';
      case 'RESERVED': return 'status-reserved';
      case 'MAINTENANCE': return 'status-maintenance';
      default: return 'status-inactive';
    }
  };

  const getSlotIcon = () => {
    switch (slot.slotType) {
      case 'EV':
      case 'EV_CHARGING':
        return <Zap size={15} className="type-icon ev" />;
      case 'ACCESSIBLE':
      case 'HANDICAP':
        return <Accessibility size={15} className="type-icon accessible" />;
      default:
        return <Car size={15} className="type-icon standard" />;
    }
  };

  const isClickable = slot.status === 'AVAILABLE' && slot.active !== false;

  return (
    <div
      className={`slot-card ${getStatusColor()} ${isSelected ? 'selected' : ''} ${!isClickable ? 'disabled' : ''}`}
      onClick={() => isClickable && onSelect && onSelect(slot)}
      title={
        !isClickable
          ? `Slot ${slot.slotNumber} is currently ${slot.status.toLowerCase()}`
          : `Click to select slot ${slot.slotNumber}`
      }
    >
      <div className="slot-card-top">
        <span className="slot-number">{slot.slotNumber}</span>
        <div className="slot-type-badge">
          {getSlotIcon()}
          <span>{slot.slotType}</span>
        </div>
      </div>

      <div className="slot-rate">
        {formatCurrency(slot.hourlyRate || 15)}/hr
      </div>

      <div className="slot-status-label">
        <span className="status-dot"></span>
        {slot.status}
      </div>
    </div>
  );
};

export default ParkingSlotCard;
