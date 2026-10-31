import React from 'react';
import { Outlet } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

export default function CustomerLayout() {
  return (
    <div className="layout-wrapper customer-layout">
      <Navbar />
      <main className="main-content customer-main">
        <Outlet />
      </main>
      <Footer />
    </div>
  );
}
