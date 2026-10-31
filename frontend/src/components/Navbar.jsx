import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Shield, User, LogOut, LayoutDashboard, MapPin, Calendar, CreditCard, Sparkles } from 'lucide-react';
import './Navbar.css';

const Navbar = () => {
  const { user, isAuthenticated, isAdmin, isCustomer, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <header className="site-navbar">
      <div className="navbar-container">
        <Link to="/" className="navbar-brand">
          <div className="brand-logo-icon">
            <Sparkles size={20} />
          </div>
          <span className="brand-name">ParkGrid<span className="brand-accent">.</span></span>
        </Link>

        <nav className="navbar-nav">
          <Link to="/" className="nav-link">Home</Link>
          <Link to="/parking" className="nav-link">Locations</Link>

          {isCustomer && (
            <>
              <Link to="/dashboard" className="nav-link">
                <LayoutDashboard size={16} />
                Dashboard
              </Link>
              <Link to="/my-bookings" className="nav-link">
                <Calendar size={16} />
                My Bookings
              </Link>
              <Link to="/billing" className="nav-link">
                <CreditCard size={16} />
                Billing
              </Link>
            </>
          )}

          {isAdmin && (
            <Link to="/admin/dashboard" className="nav-link admin-nav-link">
              <Shield size={16} />
              Admin Portal
            </Link>
          )}
        </nav>

        <div className="navbar-actions">
          {isAuthenticated ? (
            <div className="user-profile-menu">
              <Link to="/profile" className="profile-pill">
                <div className="user-avatar">
                  {user?.fullName?.charAt(0).toUpperCase() || 'U'}
                </div>
                <div className="user-info-text">
                  <span className="user-name">{user?.fullName}</span>
                  <span className={`user-role-badge ${isAdmin ? 'admin' : 'customer'}`}>
                    {isAdmin ? 'Admin' : 'Customer'}
                  </span>
                </div>
              </Link>
              <button className="logout-btn" onClick={logout} title="Sign Out">
                <LogOut size={18} />
              </button>
            </div>
          ) : (
            <div className="auth-buttons">
              <Link to="/login" className="btn-nav-login">Sign In</Link>
              <Link to="/register" className="btn-nav-register">Get Started</Link>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};

export default Navbar;
