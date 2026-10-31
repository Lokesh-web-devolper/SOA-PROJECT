import React, { useState, useEffect } from 'react';
import authService from '../services/authService';
import Loader from '../components/Loader';
import { useNotification } from '../context/NotificationContext';
import { formatDateTime } from '../utils/formatters';
import { Users, Search, ShieldCheck, UserCheck, Mail, Phone, Calendar } from 'lucide-react';
import './ManageUsers.css';

export default function ManageUsers() {
  const { showError } = useNotification();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [roleFilter, setRoleFilter] = useState('ALL');

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      setLoading(true);
      const res = await authService.getAllUsers();
      if (res.data) {
        setUsers(res.data);
      }
    } catch (err) {
      showError('Failed to fetch user accounts');
    } finally {
      setLoading(false);
    }
  };

  const filtered = users.filter((u) => {
    const matchesRole = roleFilter === 'ALL' || u.role === roleFilter;
    const matchesSearch =
      u.name?.toLowerCase().includes(search.toLowerCase()) ||
      u.email?.toLowerCase().includes(search.toLowerCase()) ||
      u.phone?.toLowerCase().includes(search.toLowerCase()) ||
      String(u.id).includes(search);
    return matchesRole && matchesSearch;
  });

  return (
    <div className="manage-users-container">
      <div className="manage-header">
        <div>
          <h1 className="manage-title">User Accounts & Roles</h1>
          <p className="manage-subtitle">Inspect registered driver profiles, administrator credentials, and access tiers</p>
        </div>
      </div>

      <div className="manage-toolbar">
        <div className="search-box">
          <Search size={18} className="search-icon" />
          <input
            type="text"
            placeholder="Search by name, email, or phone..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        <div className="status-filter-pills">
          {['ALL', 'ROLE_CUSTOMER', 'ROLE_ADMIN'].map((rf) => (
            <button
              key={rf}
              className={`pill-btn ${roleFilter === rf ? 'active' : ''}`}
              onClick={() => setRoleFilter(rf)}
            >
              {rf === 'ALL' ? 'All Roles' : rf === 'ROLE_ADMIN' ? 'Administrators' : 'Customers / Drivers'}
            </button>
          ))}
        </div>
      </div>

      {loading ? (
        <Loader text="Loading registered accounts..." />
      ) : (
        <div className="table-card">
          <div className="table-wrapper">
            <table className="manage-table">
              <thead>
                <tr>
                  <th>User ID</th>
                  <th>Full Name</th>
                  <th>Email Address</th>
                  <th>Phone Number</th>
                  <th>Role / Authority</th>
                  <th>Registered At</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((u) => (
                  <tr key={u.id}>
                    <td className="font-mono">#{u.id}</td>
                    <td>
                      <div className="user-name-cell">
                        <div className="avatar-sm">
                          {u.name ? u.name.charAt(0).toUpperCase() : 'U'}
                        </div>
                        <strong>{u.name}</strong>
                      </div>
                    </td>
                    <td>
                      <div className="flex-center-gap">
                        <Mail size={14} className="cell-icon" /> {u.email}
                      </div>
                    </td>
                    <td>
                      <div className="flex-center-gap">
                        <Phone size={14} className="cell-icon" /> {u.phone || 'N/A'}
                      </div>
                    </td>
                    <td>
                      <span className={`role-pill ${u.role === 'ROLE_ADMIN' ? 'admin' : 'customer'}`}>
                        {u.role === 'ROLE_ADMIN' ? <ShieldCheck size={14} /> : <UserCheck size={14} />}
                        {u.role === 'ROLE_ADMIN' ? 'Administrator' : 'Customer'}
                      </span>
                    </td>
                    <td>
                      <div className="flex-center-gap">
                        <Calendar size={14} className="cell-icon" /> {formatDateTime(u.createdAt)}
                      </div>
                    </td>
                    <td>
                      <span className="status-pill active">Active</span>
                    </td>
                  </tr>
                ))}
                {filtered.length === 0 && (
                  <tr>
                    <td colSpan={7} style={{ textAlign: 'center', padding: '3rem', color: '#64748b' }}>
                      No user accounts found matching your query.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
