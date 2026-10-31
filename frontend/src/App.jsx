import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { NotificationProvider } from './context/NotificationContext';

// Centralized Layout Architecture
import PublicLayout from './layouts/PublicLayout';
import CustomerLayout from './layouts/CustomerLayout';
import AdminLayout from './layouts/AdminLayout';
import AuthLayout from './layouts/AuthLayout';

// Route Guards
import ProtectedRoute from './components/ProtectedRoute';
import RoleBasedRoute from './components/RoleBasedRoute';

// Public Pages
import Home from './pages/Home';
import ParkingLocations from './pages/ParkingLocations';
import ParkingDetails from './pages/ParkingDetails';

// Authentication Pages
import Login from './pages/Login';
import Register from './pages/Register';

// Customer Protected Pages
import CustomerDashboard from './pages/CustomerDashboard';
import Booking from './pages/Booking';
import MyBookings from './pages/MyBookings';
import Billing from './pages/Billing';
import Profile from './pages/Profile';

// Admin Protected Pages
import AdminDashboard from './pages/AdminDashboard';
import ManageFacilities from './pages/ManageFacilities';
import ManageZones from './pages/ManageZones';
import ManageSlots from './pages/ManageSlots';
import ManageBookings from './pages/ManageBookings';
import ManageUsers from './pages/ManageUsers';
import ManagePricing from './pages/ManagePricing';

import './App.css';

function App() {
  return (
    <NotificationProvider>
      <AuthProvider>
        <Router>
          <Routes>
            {/* 1. PUBLIC ROUTES (Exactly one public Navbar & one Footer) */}
            <Route element={<PublicLayout />}>
              <Route path="/" element={<Home />} />
              <Route path="/parking" element={<ParkingLocations />} />
              <Route path="/locations" element={<ParkingLocations />} />
              <Route path="/parking/:id" element={<ParkingDetails />} />
            </Route>

            {/* 2. AUTHENTICATION ROUTES (Clean Auth layout, no public navbar/footer duplicates) */}
            <Route element={<AuthLayout />}>
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
            </Route>

            {/* 3. CUSTOMER PROTECTED ROUTES */}
            <Route
              element={
                <ProtectedRoute>
                  <CustomerLayout />
                </ProtectedRoute>
              }
            >
              <Route path="/dashboard" element={<CustomerDashboard />} />
              <Route path="/book/:slotId" element={<Booking />} />
              <Route path="/booking" element={<Booking />} />
              <Route path="/my-bookings" element={<MyBookings />} />
              <Route path="/billing" element={<Billing />} />
              <Route path="/profile" element={<Profile />} />
            </Route>

            {/* 4. ADMIN PROTECTED ROUTES (Dedicated Admin Topbar + Sidebar, no public navbar/footer duplicates) */}
            <Route
              path="/admin"
              element={
                <RoleBasedRoute allowedRoles={['ROLE_ADMIN']}>
                  <AdminLayout />
                </RoleBasedRoute>
              }
            >
              <Route index element={<Navigate to="/admin/dashboard" replace />} />
              <Route path="dashboard" element={<AdminDashboard />} />
              <Route path="facilities" element={<ManageFacilities />} />
              <Route path="zones" element={<ManageZones />} />
              <Route path="slots" element={<ManageSlots />} />
              <Route path="bookings" element={<ManageBookings />} />
              <Route path="users" element={<ManageUsers />} />
              <Route path="pricing" element={<ManagePricing />} />
            </Route>

            {/* CATCH-ALL REDIRECT */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </Router>
      </AuthProvider>
    </NotificationProvider>
  );
}

export default App;
