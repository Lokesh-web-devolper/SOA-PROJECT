import React, { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import authService from '../services/authService';
import { useNotification } from '../context/NotificationContext';
import { User, Mail, Phone, Shield, Calendar, Save, CheckCircle } from 'lucide-react';
import './Profile.css';

export default function Profile() {
  const { user, login } = useAuth();
  const { showSuccess, showError } = useNotification();

  const [formData, setFormData] = useState({
    name: user?.name || '',
    phone: user?.phone || '',
    email: user?.email || '',
  });

  const [saving, setSaving] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.name.trim()) {
      showError('Name cannot be empty');
      return;
    }

    try {
      setSaving(true);
      const res = await authService.updateProfile({
        name: formData.name,
        phone: formData.phone
      });

      if (res.data) {
        showSuccess('Profile updated successfully!');
        // Update user in local storage / context
        const updatedUser = { ...user, name: formData.name, phone: formData.phone };
        localStorage.setItem('parkgrid_user', JSON.stringify(updatedUser));
      }
    } catch (err) {
      showError(err.response?.data?.message || 'Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="profile-page-container">
      <div className="profile-page-header">
        <h1 className="profile-title">Account Profile</h1>
        <p className="profile-subtitle">Manage your personal credentials, contact info, and security preferences</p>
      </div>

      <div className="profile-grid">
        <div className="profile-card info-card">
          <div className="avatar-section">
            <div className="avatar-circle">
              {formData.name ? formData.name.charAt(0).toUpperCase() : 'U'}
            </div>
            <h2 className="user-display-name">{formData.name || 'User'}</h2>
            <span className="user-badge">
              <Shield size={14} /> {user?.role === 'ROLE_ADMIN' ? 'Platform Administrator' : 'Verified Driver'}
            </span>
          </div>

          <div className="user-meta-list">
            <div className="meta-item">
              <Mail size={16} className="meta-icon" />
              <div>
                <span className="meta-label">Email Address</span>
                <p className="meta-val">{user?.email}</p>
              </div>
            </div>

            <div className="meta-item">
              <Phone size={16} className="meta-icon" />
              <div>
                <span className="meta-label">Phone Number</span>
                <p className="meta-val">{user?.phone || 'Not provided'}</p>
              </div>
            </div>

            <div className="meta-item">
              <Calendar size={16} className="meta-icon" />
              <div>
                <span className="meta-label">Account Status</span>
                <p className="meta-val status-active">Active / Verified</p>
              </div>
            </div>
          </div>
        </div>

        <div className="profile-card form-card">
          <h3 className="section-title">Edit Details</h3>
          <p className="section-desc">Keep your contact information up-to-date for instant booking notifications</p>

          <form onSubmit={handleSubmit} className="profile-form">
            <div className="form-group">
              <label htmlFor="name">Full Name</label>
              <input
                id="name"
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                placeholder="Your full name"
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="email">Email Address (Read Only)</label>
              <input
                id="email"
                type="email"
                value={formData.email}
                disabled
                className="input-disabled"
              />
              <span className="field-hint">Email address cannot be modified once registered.</span>
            </div>

            <div className="form-group">
              <label htmlFor="phone">Phone Number</label>
              <input
                id="phone"
                type="tel"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                placeholder="+1 (555) 000-0000"
              />
            </div>

            <div className="form-actions">
              <button type="submit" className="btn-save-profile" disabled={saving}>
                <Save size={18} /> {saving ? 'Saving changes...' : 'Save Profile'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
