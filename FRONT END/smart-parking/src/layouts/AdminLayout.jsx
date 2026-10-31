import React from 'react';
import { Outlet, Link, useNavigate } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import { useAuth } from '../context/AuthContext';
import { Shield, Sparkles, LogOut, ArrowLeft } from 'lucide-react';
import './AdminLayout.css';

export default function AdminLayout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <div className="admin-app-container">
      {/* Admin Topbar */}
      <header className="admin-topbar">
        <div className="admin-topbar-left">
          <Link to="/admin/dashboard" className="admin-brand">
            <div className="admin-brand-icon">
              <Sparkles size={18} />
            </div>
            <span className="admin-brand-text">ParkGrid <span className="admin-tag">ADMIN</span></span>
          </Link>
          <Link to="/" className="btn-back-public">
            <ArrowLeft size={15} /> Public Site
          </Link>
        </div>

        <div className="admin-topbar-right">
          <div className="admin-user-pill">
            <div className="admin-avatar">
              {user?.fullName?.charAt(0).toUpperCase() || 'A'}
            </div>
            <div className="admin-user-info">
              <span className="admin-user-name">{user?.fullName || 'Administrator'}</span>
              <span className="admin-role-label">System Admin</span>
            </div>
          </div>
          <button className="btn-admin-logout" onClick={logout} title="Sign Out">
            <LogOut size={16} />
          </button>
        </div>
      </header>

      {/* Admin Body with Sidebar & Content */}
      <div className="admin-body-layout">
        <Sidebar />
        <main className="admin-main-viewport">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
