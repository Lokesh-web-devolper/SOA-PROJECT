import React from 'react';
import { Outlet, Link } from 'react-router-dom';
import { Sparkles } from 'lucide-react';
import './AuthLayout.css';

export default function AuthLayout() {
  return (
    <div className="auth-layout-wrapper">
      <header className="auth-minimal-nav">
        <Link to="/" className="auth-brand-logo">
          <div className="auth-brand-icon">
            <Sparkles size={18} />
          </div>
          <span className="auth-brand-name">ParkGrid<span className="accent-dot">.</span></span>
        </Link>
        <Link to="/" className="auth-back-link">
          ← Back to Home
        </Link>
      </header>

      <main className="auth-viewport">
        <Outlet />
      </main>

      <footer className="auth-minimal-footer">
        <p>© {new Date().getFullYear()} ParkGrid Solutions Inc. Enterprise Urban Parking Platform.</p>
      </footer>
    </div>
  );
}
