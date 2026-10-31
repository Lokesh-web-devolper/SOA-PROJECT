import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  Building,
  Layers,
  SquareParking,
  CalendarCheck,
  Users,
  DollarSign
} from 'lucide-react';
import './Sidebar.css';

const Sidebar = () => {
  const adminLinks = [
    { to: '/admin/dashboard', icon: LayoutDashboard, label: 'Overview' },
    { to: '/admin/facilities', icon: Building, label: 'Facilities' },
    { to: '/admin/zones', icon: Layers, label: 'Zones' },
    { to: '/admin/slots', icon: SquareParking, label: 'Parking Slots' },
    { to: '/admin/bookings', icon: CalendarCheck, label: 'Reservations' },
    { to: '/admin/users', icon: Users, label: 'Registered Users' },
    { to: '/admin/pricing', icon: DollarSign, label: 'Pricing & Tariffs' }
  ];

  return (
    <aside className="admin-sidebar">
      <div className="sidebar-section-title">ADMIN MANAGEMENT</div>
      <nav className="sidebar-menu">
        {adminLinks.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}
            >
              <Icon size={18} className="sidebar-icon" />
              <span>{item.label}</span>
            </NavLink>
          );
        })}
      </nav>
    </aside>
  );
};

export default Sidebar;
