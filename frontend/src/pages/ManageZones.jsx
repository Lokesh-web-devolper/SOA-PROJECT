import React, { useState, useEffect } from 'react';
import parkingService from '../services/parkingService';
import Loader from '../components/Loader';
import ConfirmDialog from '../components/ConfirmDialog';
import { useNotification } from '../context/NotificationContext';
import { Layers, Plus, Edit, Trash2, X, Filter } from 'lucide-react';
import './ManageZones.css';

export default function ManageZones() {
  const { showSuccess, showError } = useNotification();
  const [zones, setZones] = useState([]);
  const [facilities, setFacilities] = useState([]);
  const [selectedFacility, setSelectedFacility] = useState('');
  const [loading, setLoading] = useState(true);

  // Modal
  const [modalOpen, setModalOpen] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [currentId, setCurrentId] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    facilityId: '',
    floorLevel: 1,
    zoneType: 'STANDARD',
    totalCapacity: 20,
    active: true
  });
  const [submitting, setSubmitting] = useState(false);
  const [deleteDialog, setDeleteDialog] = useState({ isOpen: false, id: null, name: '' });

  const loadData = async () => {
    try {
      setLoading(true);
      const [facRes, zoneRes] = await Promise.all([
        parkingService.getFacilities(false),
        parkingService.getZones(selectedFacility ? selectedFacility : null, false)
      ]);
      if (facRes.data) setFacilities(facRes.data);
      if (zoneRes.data) setZones(zoneRes.data);
    } catch (err) {
      showError('Failed to fetch zones data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [selectedFacility]);

  const openAddModal = () => {
    setIsEditing(false);
    setCurrentId(null);
    setFormData({
      name: '',
      facilityId: facilities.length > 0 ? facilities[0].id : '',
      floorLevel: 1,
      zoneType: 'STANDARD',
      totalCapacity: 20,
      active: true
    });
    setModalOpen(true);
  };

  const openEditModal = (z) => {
    setIsEditing(true);
    setCurrentId(z.id);
    setFormData({
      name: z.name,
      facilityId: z.facilityId,
      floorLevel: z.floorLevel,
      zoneType: z.zoneType || 'STANDARD',
      totalCapacity: z.totalCapacity,
      active: z.active
    });
    setModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setSubmitting(true);
      if (isEditing) {
        await parkingService.updateZone(currentId, formData);
        showSuccess('Zone updated successfully');
      } else {
        await parkingService.createZone(formData);
        showSuccess('Zone created successfully');
      }
      setModalOpen(false);
      loadData();
    } catch (err) {
      showError(err.response?.data?.message || 'Failed to save zone');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = (id, name) => {
    setDeleteDialog({ isOpen: true, id, name });
  };

  const confirmDelete = async () => {
    try {
      await parkingService.deleteZone(deleteDialog.id);
      showSuccess(`Zone "${deleteDialog.name}" deleted`);
      setDeleteDialog({ isOpen: false, id: null, name: '' });
      loadData();
    } catch (err) {
      showError(err.response?.data?.message || 'Failed to delete zone');
    }
  };

  return (
    <div className="manage-zones-container">
      <div className="manage-header">
        <div>
          <h1 className="manage-title">Parking Zones</h1>
          <p className="manage-subtitle">Structure floors, sections, and specialized zones (EV, VIP, Standard)</p>
        </div>
        <button className="btn-add-primary" onClick={openAddModal}>
          <Plus size={18} /> Add Zone
        </button>
      </div>

      <div className="filter-bar">
        <div className="filter-item">
          <Filter size={16} />
          <span>Facility:</span>
          <select
            value={selectedFacility}
            onChange={(e) => setSelectedFacility(e.target.value)}
          >
            <option value="">All Facilities</option>
            {facilities.map((f) => (
              <option key={f.id} value={f.id}>{f.name}</option>
            ))}
          </select>
        </div>
      </div>

      {loading ? (
        <Loader text="Loading parking zones..." />
      ) : (
        <div className="table-card">
          <div className="table-wrapper">
            <table className="manage-table">
              <thead>
                <tr>
                  <th>Zone Name</th>
                  <th>Facility</th>
                  <th>Floor Level</th>
                  <th>Zone Type</th>
                  <th>Capacity</th>
                  <th>Available</th>
                  <th>Status</th>
                  <th style={{ textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {zones.map((zone) => (
                  <tr key={zone.id}>
                    <td>
                      <div className="zone-name-cell">
                        <Layers size={18} className="zone-icon" />
                        <strong>{zone.name}</strong>
                      </div>
                    </td>
                    <td>{zone.facilityName || `Facility #${zone.facilityId}`}</td>
                    <td>Floor {zone.floorLevel}</td>
                    <td>
                      <span className="type-badge">{zone.zoneType || 'STANDARD'}</span>
                    </td>
                    <td><strong>{zone.totalCapacity}</strong> slots</td>
                    <td>
                      <span className={`avail-badge ${zone.availableSlots > 0 ? 'good' : 'full'}`}>
                        {zone.availableSlots} free
                      </span>
                    </td>
                    <td>
                      <span className={`status-pill ${zone.active ? 'active' : 'inactive'}`}>
                        {zone.active ? 'Active' : 'Disabled'}
                      </span>
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      <div className="action-buttons-group">
                        <button
                          className="btn-table-action edit"
                          onClick={() => openEditModal(zone)}
                          title="Edit Zone"
                        >
                          <Edit size={16} />
                        </button>
                        <button
                          className="btn-table-action delete"
                          onClick={() => handleDelete(zone.id, zone.name)}
                          title="Delete Zone"
                        >
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
                {zones.length === 0 && (
                  <tr>
                    <td colSpan={8} style={{ textAlign: 'center', padding: '3rem', color: '#64748b' }}>
                      No parking zones found. Click "Add Zone" to configure one.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Modal Form */}
      {modalOpen && (
        <div className="modal-backdrop" onClick={() => setModalOpen(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{isEditing ? 'Edit Zone' : 'Add Parking Zone'}</h2>
              <button className="btn-modal-close" onClick={() => setModalOpen(false)}>
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="modal-form">
              <div className="form-group">
                <label>Facility *</label>
                <select
                  required
                  value={formData.facilityId}
                  onChange={(e) => setFormData({ ...formData, facilityId: e.target.value })}
                >
                  {facilities.map((fac) => (
                    <option key={fac.id} value={fac.id}>{fac.name}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>Zone Name *</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Zone A - North Wing"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Floor Level *</label>
                  <input
                    type="number"
                    required
                    value={formData.floorLevel}
                    onChange={(e) => setFormData({ ...formData, floorLevel: parseInt(e.target.value) || 0 })}
                  />
                </div>

                <div className="form-group">
                  <label>Zone Type *</label>
                  <select
                    value={formData.zoneType}
                    onChange={(e) => setFormData({ ...formData, zoneType: e.target.value })}
                  >
                    <option value="STANDARD">STANDARD</option>
                    <option value="EV_CHARGING">EV_CHARGING</option>
                    <option value="VIP">VIP</option>
                    <option value="HANDICAP">HANDICAP</option>
                    <option value="COMPACT">COMPACT</option>
                  </select>
                </div>
              </div>

              <div className="form-group">
                <label>Total Capacity *</label>
                <input
                  type="number"
                  min="1"
                  required
                  value={formData.totalCapacity}
                  onChange={(e) => setFormData({ ...formData, totalCapacity: parseInt(e.target.value) || 0 })}
                />
              </div>

              <div className="form-checkbox-group">
                <label>
                  <input
                    type="checkbox"
                    checked={formData.active}
                    onChange={(e) => setFormData({ ...formData, active: e.target.checked })}
                  />
                  <span>Active & Open for Allocations</span>
                </label>
              </div>

              <div className="modal-footer">
                <button type="button" className="btn-cancel" onClick={() => setModalOpen(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn-submit" disabled={submitting}>
                  {submitting ? 'Saving...' : isEditing ? 'Update Zone' : 'Create Zone'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <ConfirmDialog
        isOpen={deleteDialog.isOpen}
        title="Delete Zone"
        message={`Are you sure you want to delete "${deleteDialog.name}"? All assigned slots will be deleted.`}
        isDestructive={true}
        onConfirm={confirmDelete}
        onCancel={() => setDeleteDialog({ isOpen: false, id: null, name: '' })}
      />
    </div>
  );
}
