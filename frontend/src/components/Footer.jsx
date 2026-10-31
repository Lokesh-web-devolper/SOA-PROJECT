import React from 'react';
import { Sparkles, Shield, Mail, Phone } from 'lucide-react';
import './Footer.css';

const Footer = () => {
  return (
    <footer className="site-footer">
      <div className="footer-container">
        <div className="footer-brand-column">
          <div className="footer-logo">
            <Sparkles size={20} className="footer-logo-icon" />
            <span className="footer-brand-name">ParkGrid Solutions</span>
          </div>
          <p className="footer-desc">
            Automated urban parking infrastructure management platform for modern smart cities, corporate campuses, and commercial hubs.
          </p>
          <div className="footer-badges">
            <span className="secure-badge"><Shield size={14} /> Microservices Architecture</span>
          </div>
        </div>

        <div className="footer-links-column">
          <h5 className="footer-heading">Platform</h5>
          <ul className="footer-links">
            <li><a href="/parking">Facilities & Zones</a></li>
            <li><a href="/booking">Real-Time Booking</a></li>
            <li><a href="/billing">Dynamic Billing</a></li>
          </ul>
        </div>

        <div className="footer-links-column">
          <h5 className="footer-heading">Developer API</h5>
          <ul className="footer-links">
            <li><a href="http://localhost:8080/swagger-ui.html" target="_blank" rel="noreferrer">API Gateway</a></li>
            <li><a href="http://localhost:8761" target="_blank" rel="noreferrer">Eureka Registry</a></li>
            <li><a href="http://localhost:8081/swagger-ui.html" target="_blank" rel="noreferrer">Auth Service Docs</a></li>
          </ul>
        </div>

        <div className="footer-links-column">
          <h5 className="footer-heading">Support</h5>
          <div className="contact-item">
            <Mail size={15} />
            <span>support@parkgrid.com</span>
          </div>
          <div className="contact-item">
            <Phone size={15} />
            <span>+1 (800) 555-PARK</span>
          </div>
        </div>
      </div>

      <div className="footer-bottom">
        <div className="footer-bottom-container">
          <p>© {new Date().getFullYear()} ParkGrid Solutions. All rights reserved.</p>
          <p className="academic-badge">Academic Project demonstration build</p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
