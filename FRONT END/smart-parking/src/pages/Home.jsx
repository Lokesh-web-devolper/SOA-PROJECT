import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  Car,
  MapPin,
  Clock,
  ShieldCheck,
  Zap,
  ArrowRight,
  CheckCircle2,
  TrendingUp,
  Cpu,
  Layers
} from 'lucide-react';
import parkingService from '../services/parkingService';
import { formatCurrency } from '../utils/formatters';
import Loader from '../components/Loader';
import './Home.css';

const Home = () => {
  const [facilities, setFacilities] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchPreview = async () => {
      try {
        const res = await parkingService.getFacilities(true);
        if (res && res.data) {
          setFacilities(res.data.slice(0, 3));
        }
      } catch (err) {
        console.error('Failed to load facility preview:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchPreview();
  }, []);

  return (
    <div className="home-page">
      {/* HERO SECTION */}
      <section className="hero-section">
        <div className="hero-container">
          <div className="hero-content">
            <div className="hero-pill">
              <Zap size={14} className="hero-pill-icon" />
              <span>Next-Generation Urban Mobility Architecture</span>
            </div>
            <h1 className="hero-title">
              Smart Parking. <br />
              <span className="hero-highlight">Smarter Cities.</span>
            </h1>
            <p className="hero-subtitle">
              ParkGrid Solutions orchestrates automated urban parking infrastructure across commercial centers, corporate parks, and transit hubs with real-time slot telemetry, anti-collision concurrency, and instant dynamic tariffs.
            </p>
            <div className="hero-cta-group">
              <Link to="/parking" className="btn-hero-primary">
                Find Parking Nearby
                <ArrowRight size={18} />
              </Link>
              <Link to="/register" className="btn-hero-secondary">
                Create Free Account
              </Link>
            </div>
            <div className="hero-stats-row">
              <div className="hero-stat">
                <span className="stat-number">99.9%</span>
                <span className="stat-label">Slot Telemetry Uptime</span>
              </div>
              <div className="hero-stat-divider"></div>
              <div className="hero-stat">
                <span className="stat-number">&lt; 150ms</span>
                <span className="stat-label">Reservation Latency</span>
              </div>
              <div className="hero-stat-divider"></div>
              <div className="hero-stat">
                <span className="stat-number">0%</span>
                <span className="stat-label">Double-Booking Rate</span>
              </div>
            </div>
          </div>

          <div className="hero-visual">
            <div className="hero-visual-card">
              <div className="visual-card-top">
                <div className="dot green"></div>
                <div className="dot yellow"></div>
                <div className="dot red"></div>
                <span className="visual-header-text">Live Sensor Grid Telemetry</span>
              </div>
              <div className="visual-slots-demo">
                <div className="demo-slot occupied">
                  <Car size={20} />
                  <span>A-101 (Occupied)</span>
                </div>
                <div className="demo-slot available pulse">
                  <Car size={20} />
                  <span>A-102 (Available)</span>
                </div>
                <div className="demo-slot occupied">
                  <Car size={20} />
                  <span>A-103 (Occupied)</span>
                </div>
                <div className="demo-slot ev available pulse">
                  <Zap size={20} />
                  <span>EV-201 (Fast Charge)</span>
                </div>
              </div>
              <div className="visual-card-footer">
                <div className="metric">
                  <span className="metric-title">Occupancy Rate</span>
                  <span className="metric-value">68.4%</span>
                </div>
                <div className="metric">
                  <span className="metric-title">Available Slots</span>
                  <span className="metric-value text-green">14 Slots Free</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* HOW IT WORKS */}
      <section className="section how-it-works-section">
        <div className="section-container">
          <div className="section-header">
            <span className="section-tag">EFFORTLESS JOURNEY</span>
            <h2 className="section-title">How ParkGrid Works</h2>
            <p className="section-subtitle">Reserve in seconds and park with guaranteed space waiting for your arrival.</p>
          </div>

          <div className="steps-grid">
            <div className="step-card">
              <div className="step-number">01</div>
              <div className="step-icon-box"><MapPin size={24} /></div>
              <h3 className="step-title">Locate Facility</h3>
              <p className="step-desc">Browse interactive urban facilities, filter by distance, pricing, or EV charging availability.</p>
            </div>

            <div className="step-card">
              <div className="step-number">02</div>
              <div className="step-icon-box"><Clock size={24} /></div>
              <h3 className="step-title">Reserve Slot</h3>
              <p className="step-desc">Pick your preferred slot with strict database-enforced concurrency locks to prevent double-booking.</p>
            </div>

            <div className="step-card">
              <div className="step-number">03</div>
              <div className="step-icon-box"><Car size={24} /></div>
              <h3 className="step-title">Check In & Park</h3>
              <p className="step-desc">Arrive and check in seamlessly. Live sensors confirm your presence and track actual parking duration.</p>
            </div>

            <div className="step-card">
              <div className="step-number">04</div>
              <div className="step-icon-box"><ShieldCheck size={24} /></div>
              <h3 className="step-title">Automated Billing</h3>
              <p className="step-desc">Check out instantly. The microservices billing engine calculates precise charges with a fair grace period.</p>
            </div>
          </div>
        </div>
      </section>

      {/* LIVE AVAILABILITY PREVIEW */}
      <section className="section live-preview-section">
        <div className="section-container">
          <div className="section-header">
            <span className="section-tag">REAL-TIME INVENTORY</span>
            <h2 className="section-title">Popular Parking Facilities</h2>
            <p className="section-subtitle">View real-time availability across our network of smart facilities.</p>
          </div>

          {loading ? (
            <Loader message="Loading live facility availability..." />
          ) : (
            <div className="facilities-preview-grid">
              {facilities.map((facility) => (
                <div key={facility.id} className="facility-preview-card">
                  <div className="facility-card-badge">
                    <span className="pulse-indicator"></span>
                    Live Availability
                  </div>
                  <h3 className="facility-name">{facility.name}</h3>
                  <p className="facility-address">
                    <MapPin size={14} />
                    {facility.address}
                  </p>
                  <div className="facility-metrics-row">
                    <div className="f-metric">
                      <span className="f-metric-val text-green">{facility.availableSlots}</span>
                      <span className="f-metric-label">Available Slots</span>
                    </div>
                    <div className="f-metric">
                      <span className="f-metric-val">{facility.totalSlots}</span>
                      <span className="f-metric-label">Total Capacity</span>
                    </div>
                    <div className="f-metric">
                      <span className="f-metric-val">{formatCurrency(facility.startingPrice)}/hr</span>
                      <span className="f-metric-label">From</span>
                    </div>
                  </div>
                  <div className="facility-card-action">
                    <Link to={`/parking/${facility.id}`} className="btn-view-slots">
                      View Available Slots
                      <ArrowRight size={15} />
                    </Link>
                  </div>
                </div>
              ))}
            </div>
          )}

          <div className="preview-all-action">
            <Link to="/parking" className="btn-explore-all">
              Explore All Parking Facilities
              <ArrowRight size={16} />
            </Link>
          </div>
        </div>
      </section>

      {/* ARCHITECTURE HIGHLIGHTS */}
      <section className="section tech-features-section">
        <div className="section-container">
          <div className="section-header">
            <span className="section-tag">ENTERPRISE-GRADE FOUNDATION</span>
            <h2 className="section-title">Engineered for Scalability & Integrity</h2>
            <p className="section-subtitle">Built on a resilient microservices architecture and atomic database transactions.</p>
          </div>

          <div className="features-grid">
            <div className="feature-item">
              <div className="feature-icon"><Layers size={22} /></div>
              <h4>Spring Cloud Microservices</h4>
              <p>Independent microservices for Auth, Parking, Booking, and Billing discovered dynamically via Netflix Eureka.</p>
            </div>
            <div className="feature-item">
              <div className="feature-icon"><ShieldCheck size={22} /></div>
              <h4>Double-Booking Elimination</h4>
              <p>Strict PostgreSQL transaction isolation and concurrency checks guarantee no two drivers reserve the same slot.</p>
            </div>
            <div className="feature-item">
              <div className="feature-icon"><Cpu size={22} /></div>
              <h4>Spring Cloud Gateway</h4>
              <p>Centralized entry point providing load balancing, routing, CORS management, and token security.</p>
            </div>
            <div className="feature-item">
              <div className="feature-icon"><TrendingUp size={22} /></div>
              <h4>Dynamic Billing & Invoicing</h4>
              <p>Duration-based tariffs, grace periods, automated calculation, and idempotent invoice generation.</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Home;
