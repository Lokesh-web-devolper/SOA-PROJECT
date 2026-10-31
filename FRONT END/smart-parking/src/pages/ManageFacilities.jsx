import React, { useState, useEffect } from 'react';
import parkingService from '../services/parkingService';
import Loader from '../components/Loader';
import ConfirmDialog from '../components/ConfirmDialog';
import { useNotification } from '../context/NotificationContext';
import { formatCurrency } from '../utils/formatters';
import { Building2, Plus, Edit, Trash2, Check, X, MapPin, Search } from 'lucide-react';
import './ManageFacilities.css';

export default function ManageFacilities() {
  const { showSuccess, showError } = useNotification();
  const [facilities, setFacilities] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  
  // Modal states
  const [modalOpen, setModalOpen] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [currentId, setCurrentId] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    address: '',
    totalCapacity: 50,
    hourlyRate: 5.0,
    active: true
  });
  const [submitting, setSubmitting] = useState(false);
  const [deleteDialog, setDeleteDialog] = useState({ isOpen: false, id: null, name: '' });

  const loadFacilities = async () => {
    try {
      setLoading(true);
      const res = await parkingService.getFacilities(false);
      if (res.data) {
        setFacilities(res.data);
      }
    } catch (err) {
      showError('Failed to fetch facilities');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadFacilities();
  }, []);

  const openAddModal = () => {
    setIsEditing(false);
    setCurrentId(null);
    setFormData({
      name: '',
      address: '',
      totalCapacity: 50,
      hourlyRate: 5.0,
      active: true
    });
    setModalOpen(true);
  };

  const openEditModal = (fac) => {
    setIsEditing(true);
    setCurrentId(fac.id);
    setFormData({
      name: fac.name,
      address: fac.address,
      totalCapacity: fac.totalCapacity,
      hourlyRate: fac.hourlyRate,
      active: fac.active
    });
    setModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setSubmitting(true);
      if (isEditing) {
        await parkingService.updateFacility(currentId, formData);
        showSuccess('Facility updated successfully!');
      } else {
        await parkingService.createFacility(formData);
        showSuccess('Facility created successfully!');
      }
      setModalOpen(false);
      loadFacilities();
    } catch (err) {
      showError(err.response?.data?.message || 'Failed to save facility');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = (id, name) => {
    setDeleteDialog({
      isOpen: true,
      id,
      name
    });
  };

  const confirmDelete = async () => {
    try {
      await parkingService.deleteFacility(deleteDialog.id);
      showSuccess(`Facility "${deleteDialog.name}" deleted`);
      setDeleteDialog({ isOpen: false, id: null, name: '' });
      loadFacilities();
    } catch (err) {
      showError(err.response?.data?.message || 'Failed to delete facility');
    }
  };

  const filtered = facilities.filter(f =>
    f.name.toLowerCase().includes(search.toLowerCase()) ||
    f.address?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="manage-page-container">
      <div className="manage-header">
        <div>
          <h1 className="manage-title">Parking Facilities</h1>
          <p className="manage-subtitle">Add, inspect, configure, and maintain commercial parking locations</p>
        </div>
        <button className="btn-add-primary" onClick={openAddModal}>
          <Plus size={18} /> Add Facility
        </button>
      </div>

      <div className="manage-toolbar">
        <div className="search-box">
          <Search size={18} className="search-icon" />
          <input
            type="text"
            placeholder="Search facilities by name or address..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
      </div>

      {loading ? (
        <Loader text="Loading facilities..." />
      ) : (
        <div className="table-card">
          <div className="table-wrapper">
            <table className="manage-table">
              <thead>
                <tr>
                  <th>Facility Name</th>
                  <th>Location Address</th>
                  <th>Total Capacity</th>
                  <th>Available Slots</th>
                  <th>Base Rate</th>
                  <th>Status</th>
                  <th style={{ textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((fac) => (
                  <tr key={fac.id}>
                    <td>
                      <div className="fac-name-col">
                        <Building2 size={18} className="fac-icon" />
                        <strong>{fac.name}</strong>
                      </div>
                    </td>
                    <td>
                      <div className="fac-address-col">
                        <MapPin size={14} /> {fac.address}
                      </div>
                    </td>
                    <td><strong>{fac.totalCapacity}</strong> slots</td>
                    <td>
                      <span className={`avail-badge ${fac.availableSlots > 0 ? 'good' : 'full'}`}>
                        {fac.availableSlots} free
                      </span>
                    </td>
                    <td><strong>{formatCurrency(fac.hourlyRate)}/hr</strong></td>
                    <td>
                      <span className={`status-pill ${fac.active ? 'active' : 'inactive'}`}>
                        {fac.active ? 'Operational' : 'Disabled'}
                      </span>
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      <div className="action-buttons-group">
                        <button
                          className="btn-table-action edit"
                          onClick={() => openEditModal(fac)}
                          title="Edit Facility"
                        >
                          <Edit size={16} />
                        </button>
                        <button
                          className="btn-table-action delete"
                          onClick={() => handleDelete(fac.id, fac.name)}
                          title="Delete Facility"
                        >
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
                {filtered.length === 0 && (
                  <tr>
                    <td colSpan={7} style={{ textAlign: 'center', padding: '3rem', color: '#64748b' }}>
                      No parking facilities match your search.
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
              <h2>{isEditing ? 'Edit Facility' : 'Add New Parking Facility'}</h2>
              <button className="btn-modal-close" onClick={() => setModalOpen(false)}>
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="modal-form">
              <div className="form-group">
                <label>Facility Name *</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Downtown Central Plaza"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                />
              </div>

              <div className="form-group">
                <label>Address *</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. 100 Financial Blvd, Suite 2"
                  value={formData.address}
                  onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Total Slot Capacity *</label>
                  <input
                    type="number"
                    min="1"
                    required
                    value={formData.totalCapacity}
                    onChange={(e) => setFormData({ ...formData, totalCapacity: parseInt(e.target.value) || 0 })}
                  />
                </div>

                <div className="form-group">
                  <label>Base Hourly Rate ($) *</label>
                  <input
                    type="number"
                    step="0.5"
                    min="0"
                    required
                    value={formData.hourlyRate}
                    onChange={(e) => setFormData({ ...formData, hourlyRate: parseFloat(e.target.value) || 0 })}
                  />
                </div>
              </div>

              <div className="form-checkbox-group">
                <label>
                  <input
                    type="checkbox"
                    checked={formData.active}
                    onChange={(e) => setFormData({ ...formData, active: e.target.checked })}
                  />
                  <span>Active & Open to Public Bookings</span>
                </label>
              </div>

              <div className="modal-footer">
                <button type="button" className="btn-cancel" onClick={() => setModalOpen(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn-submit" disabled={submitting}>
                  {submitting ? 'Saving...' : isEditing ? 'Update Facility' : 'Create Facility'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <ConfirmDialog
        isOpen={deleteDialog.isOpen}
        title="Delete Facility"
        message={`Are you sure you want to delete "${deleteDialog.name}"? All associated zones and slots will be permanently removed.`}
        isDestructive={true}
        onConfirm={confirmDelete}
        onCancel={() => setDeleteDialog({ isOpen: false, id: null, name: '' })}
      />
    </div>
  );
}
