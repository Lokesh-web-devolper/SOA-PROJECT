import React, { useState, useEffect } from 'react';
import parkingService from '../services/parkingService';
import Loader from '../components/Loader';
import ConfirmDialog from '../components/ConfirmDialog';
import { useNotification } from '../context/NotificationContext';
import { SquareParking, Plus, Trash2, X, Filter, Wrench, CheckCircle, AlertTriangle } from 'lucide-react';
import './ManageSlots.css';

export default function ManageSlots() {
  const { showSuccess, showError } = useNotification();
  const [slots, setSlots] = useState([]);
  const [facilities, setFacilities] = useState([]);
  const [zones, setZones] = useState([]);
  const [selectedFacility, setSelectedFacility] = useState('');
  const [selectedZone, setSelectedZone] = useState('');
  const [selectedStatus, setSelectedStatus] = useState('');
  const [loading, setLoading] = useState(true);

  // Add slot modal
  const [modalOpen, setModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    slotNumber: '',
    facilityId: '',
    zoneId: '',
    slotType: 'STANDARD',
    status: 'AVAILABLE'
  });
  const [submitting, setSubmitting] = useState(false);
  const [deleteDialog, setDeleteDialog] = useState({ isOpen: false, id: null, number: '' });

  const loadData = async () => {
    try {
      setLoading(true);
      const [facRes, zoneRes, slotRes] = await Promise.all([
        parkingService.getFacilities(false),
        parkingService.getZones(selectedFacility || null, false),
        parkingService.getSlots({
          facilityId: selectedFacility || undefined,
          zoneId: selectedZone || undefined,
          status: selectedStatus || undefined
        })
      ]);

      if (facRes.data) setFacilities(facRes.data);
      if (zoneRes.data) setZones(zoneRes.data);
      if (slotRes.data) setSlots(slotRes.data);
    } catch (err) {
      showError('Failed to fetch slot inventory');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [selectedFacility, selectedZone, selectedStatus]);

  const openAddModal = () => {
    const firstFac = facilities[0]?.id || '';
    const firstZone = zones[0]?.id || '';
    setFormData({
      slotNumber: `SLOT-${Math.floor(100 + Math.random() * 900)}`,
      facilityId: selectedFacility || firstFac,
      zoneId: selectedZone || firstZone,
      slotType: 'STANDARD',
      status: 'AVAILABLE'
    });
    setModalOpen(true);
  };

  const handleStatusChange = async (slotId, newStatus) => {
    try {
      await parkingService.updateSlotStatus(slotId, newStatus);
      showSuccess(`Slot status changed to ${newStatus}`);
      loadData();
    } catch (err) {
      showError(err.response?.data?.message || 'Failed to update slot status');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setSubmitting(true);
      await parkingService.createSlot(formData);
      showSuccess(`Slot ${formData.slotNumber} created!`);
      setModalOpen(false);
      loadData();
    } catch (err) {
      showError(err.response?.data?.message || 'Failed to create slot');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = (id, number) => {
    setDeleteDialog({ isOpen: true, id, number });
  };

  const confirmDelete = async () => {
    try {
      await parkingService.deleteSlot(deleteDialog.id);
      showSuccess(`Slot ${deleteDialog.number} removed`);
      setDeleteDialog({ isOpen: false, id: null, number: '' });
      loadData();
    } catch (err) {
      showError(err.response?.data?.message || 'Failed to delete slot');
    }
  };

  return (
    <div className="manage-slots-container">
      <div className="manage-header">
        <div>
          <h1 className="manage-title">Parking Slots</h1>
          <p className="manage-subtitle">Real-time slot states, EV stations, maintenance toggling, and physical capacity</p>
        </div>
        <button className="btn-add-primary" onClick={openAddModal}>
          <Plus size={18} /> Add Slot
        </button>
      </div>

      <div className="filter-bar">
        <div className="filter-item">
          <span>Facility:</span>
          <select
            value={selectedFacility}
            onChange={(e) => {
              setSelectedFacility(e.target.value);
              setSelectedZone('');
            }}
          >
            <option value="">All Facilities</option>
            {facilities.map((f) => (
              <option key={f.id} value={f.id}>{f.name}</option>
            ))}
          </select>
        </div>

        <div className="filter-item">
          <span>Zone:</span>
          <select
            value={selectedZone}
            onChange={(e) => setSelectedZone(e.target.value)}
          >
            <option value="">All Zones</option>
            {zones.map((z) => (
              <option key={z.id} value={z.id}>{z.name}</option>
            ))}
          </select>
        </div>

        <div className="filter-item">
          <span>Status:</span>
          <select
            value={selectedStatus}
            onChange={(e) => setSelectedStatus(e.target.value)}
          >
            <option value="">All Statuses</option>
            <option value="AVAILABLE">AVAILABLE</option>
            <option value="OCCUPIED">OCCUPIED</option>
            <option value="RESERVED">RESERVED</option>
            <option value="MAINTENANCE">MAINTENANCE</option>
          </select>
        </div>
      </div>

      {loading ? (
        <Loader text="Loading slots..." />
      ) : (
        <div className="table-card">
          <div className="table-wrapper">
            <table className="manage-table">
              <thead>
                <tr>
                  <th>Slot Number</th>
                  <th>Facility</th>
                  <th>Zone</th>
                  <th>Slot Type</th>
                  <th>Live Status</th>
                  <th>Maintenance Override</th>
                  <th style={{ textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {slots.map((slot) => (
                  <tr key={slot.id}>
                    <td>
                      <div className="slot-num-cell">
                        <SquareParking size={18} className="slot-icon" />
                        <strong>{slot.slotNumber}</strong>
                      </div>
                    </td>
                    <td>{slot.facilityName || `Facility #${slot.facilityId}`}</td>
                    <td>{slot.zoneName || `Zone #${slot.zoneId}`}</td>
                    <td>
                      <span className="type-badge">{slot.slotType || 'STANDARD'}</span>
                    </td>
                    <td>
                      <span className={`status-pill ${slot.status?.toLowerCase()}`}>
                        {slot.status}
                      </span>
                    </td>
                    <td>
                      <div className="status-toggle-group">
                        {slot.status === 'MAINTENANCE' ? (
                          <button
                            className="btn-toggle-status enable"
                            onClick={() => handleStatusChange(slot.id, 'AVAILABLE')}
                            title="Restore to Available"
                          >
                            <CheckCircle size={14} /> Restore
                          </button>
                        ) : (
                          <button
                            className="btn-toggle-status disable"
                            onClick={() => handleStatusChange(slot.id, 'MAINTENANCE')}
                            title="Put into Maintenance"
                          >
                            <Wrench size={14} /> Set Maintenance
                          </button>
                        )}
                      </div>
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      <button
                        className="btn-table-action delete"
                        onClick={() => handleDelete(slot.id, slot.slotNumber)}
                        title="Delete Slot"
                      >
                        <Trash2 size={16} />
                      </button>
                    </td>
                  </tr>
                ))}
                {slots.length === 0 && (
                  <tr>
                    <td colSpan={7} style={{ textAlign: 'center', padding: '3rem', color: '#64748b' }}>
                      No parking slots match the selected criteria.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Add Slot Modal */}
      {modalOpen && (
        <div className="modal-backdrop" onClick={() => setModalOpen(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Add New Parking Slot</h2>
              <button className="btn-modal-close" onClick={() => setModalOpen(false)}>
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="modal-form">
              <div className="form-group">
                <label>Slot Number / Identifier *</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. A-101, EV-05"
                  value={formData.slotNumber}
                  onChange={(e) => setFormData({ ...formData, slotNumber: e.target.value })}
                />
              </div>

              <div className="form-group">
                <label>Facility *</label>
                <select
                  required
                  value={formData.facilityId}
                  onChange={(e) => setFormData({ ...formData, facilityId: e.target.value })}
                >
                  <option value="">Select Facility</option>
                  {facilities.map((fac) => (
                    <option key={fac.id} value={fac.id}>{fac.name}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>Zone *</label>
                <select
                  required
                  value={formData.zoneId}
                  onChange={(e) => setFormData({ ...formData, zoneId: e.target.value })}
                >
                  <option value="">Select Zone</option>
                  {zones.map((z) => (
                    <option key={z.id} value={z.id}>{z.name}</option>
                  ))}
                </select>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Slot Type</label>
                  <select
                    value={formData.slotType}
                    onChange={(e) => setFormData({ ...formData, slotType: e.target.value })}
                  >
                    <option value="STANDARD">STANDARD</option>
                    <option value="EV_CHARGING">EV_CHARGING</option>
                    <option value="HANDICAP">HANDICAP</option>
                    <option value="VIP">VIP</option>
                  </select>
                </div>

                <div className="form-group">
                  <label>Initial Status</label>
                  <select
                    value={formData.status}
                    onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                  >
                    <option value="AVAILABLE">AVAILABLE</option>
                    <option value="MAINTENANCE">MAINTENANCE</option>
                  </select>
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" className="btn-cancel" onClick={() => setModalOpen(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn-submit" disabled={submitting}>
                  {submitting ? 'Creating...' : 'Create Slot'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <ConfirmDialog
        isOpen={deleteDialog.isOpen}
        title="Delete Slot"
        message={`Are you sure you want to permanently delete slot "${deleteDialog.number}"?`}
        isDestructive={true}
        onConfirm={confirmDelete}
        onCancel={() => setDeleteDialog({ isOpen: false, id: null, number: '' })}
      />
    </div>
  );
}
