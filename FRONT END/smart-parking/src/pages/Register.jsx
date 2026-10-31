import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useNotification } from '../context/NotificationContext';
import { User, Mail, Phone, Lock, Eye, EyeOff, Sparkles, ArrowRight, ShieldCheck } from 'lucide-react';
import './Register.css';

const Register = () => {
  const { register } = useAuth();
  const { showSuccess, showError } = useNotification();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: '',
    agreeTerms: false
  });

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: null }));
    }
  };

  const validate = () => {
    const errs = {};
    if (!formData.fullName.trim()) {
      errs.fullName = 'Full name is required';
    }

    if (!formData.email.trim()) {
      errs.email = 'Email address is required';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      errs.email = 'Please enter a valid email address';
    }

    if (!formData.phone.trim()) {
      errs.phone = 'Phone number is required';
    }

    if (!formData.password) {
      errs.password = 'Password is required';
    } else if (formData.password.length < 6) {
      errs.password = 'Password must be at least 6 characters long';
    }

    if (formData.password !== formData.confirmPassword) {
      errs.confirmPassword = 'Passwords do not match';
    }

    if (!formData.agreeTerms) {
      errs.agreeTerms = 'You must agree to the Terms of Service';
    }

    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setLoading(true);
    try {
      await register({
        fullName: formData.fullName.trim(),
        email: formData.email.trim().toLowerCase(),
        phone: formData.phone.trim(),
        password: formData.password
      });

      showSuccess('Account created successfully! Welcome to ParkGrid.');
      navigate('/dashboard', { replace: true });
    } catch (err) {
      const msg = err.response?.data?.message || 'Registration failed. Email may already be in use.';
      showError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-card register-card">
      <div className="auth-header">
        <div className="auth-logo-badge">
          <Sparkles size={22} />
        </div>
        <h2 className="auth-title">Create your ParkGrid account</h2>
        <p className="auth-subtitle">Join thousands of drivers enjoying automated urban parking</p>
      </div>

      <form onSubmit={handleSubmit} className="auth-form" noValidate>
        <div className="form-group">
          <label htmlFor="fullName">Full Name</label>
          <div className={`input-with-icon ${errors.fullName ? 'has-error' : ''}`}>
            <User size={18} className="input-icon" />
            <input
              id="fullName"
              name="fullName"
              type="text"
              placeholder="e.g. Alex Rivera"
              value={formData.fullName}
              onChange={handleChange}
              disabled={loading}
              autoComplete="name"
            />
          </div>
          {errors.fullName && <span className="field-error">{errors.fullName}</span>}
        </div>

        <div className="form-row-2">
          <div className="form-group">
            <label htmlFor="email">Email Address</label>
            <div className={`input-with-icon ${errors.email ? 'has-error' : ''}`}>
              <Mail size={18} className="input-icon" />
              <input
                id="email"
                name="email"
                type="email"
                placeholder="name@example.com"
                value={formData.email}
                onChange={handleChange}
                disabled={loading}
                autoComplete="email"
              />
            </div>
            {errors.email && <span className="field-error">{errors.email}</span>}
          </div>

          <div className="form-group">
            <label htmlFor="phone">Phone Number</label>
            <div className={`input-with-icon ${errors.phone ? 'has-error' : ''}`}>
              <Phone size={18} className="input-icon" />
              <input
                id="phone"
                name="phone"
                type="tel"
                placeholder="+1 555-0123"
                value={formData.phone}
                onChange={handleChange}
                disabled={loading}
                autoComplete="tel"
              />
            </div>
            {errors.phone && <span className="field-error">{errors.phone}</span>}
          </div>
        </div>

        <div className="form-row-2">
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <div className={`input-with-icon ${errors.password ? 'has-error' : ''}`}>
              <Lock size={18} className="input-icon" />
              <input
                id="password"
                name="password"
                type={showPassword ? 'text' : 'password'}
                placeholder="Min 6 characters"
                value={formData.password}
                onChange={handleChange}
                disabled={loading}
                autoComplete="new-password"
              />
              <button
                type="button"
                className="btn-toggle-pw"
                onClick={() => setShowPassword(!showPassword)}
                tabIndex="-1"
                aria-label={showPassword ? 'Hide password' : 'Show password'}
              >
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
            {errors.password && <span className="field-error">{errors.password}</span>}
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Confirm Password</label>
            <div className={`input-with-icon ${errors.confirmPassword ? 'has-error' : ''}`}>
              <Lock size={18} className="input-icon" />
              <input
                id="confirmPassword"
                name="confirmPassword"
                type={showConfirmPassword ? 'text' : 'password'}
                placeholder="Re-enter password"
                value={formData.confirmPassword}
                onChange={handleChange}
                disabled={loading}
                autoComplete="new-password"
              />
              <button
                type="button"
                className="btn-toggle-pw"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                tabIndex="-1"
                aria-label={showConfirmPassword ? 'Hide password' : 'Show password'}
              >
                {showConfirmPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
            {errors.confirmPassword && <span className="field-error">{errors.confirmPassword}</span>}
          </div>
        </div>

        <div className="terms-row">
          <label className="checkbox-label">
            <input
              type="checkbox"
              name="agreeTerms"
              checked={formData.agreeTerms}
              onChange={handleChange}
            />
            <span>
              I agree to the <a href="#terms" className="auth-inline-link">Terms of Service</a> and <a href="#privacy" className="auth-inline-link">Privacy Policy</a>
            </span>
          </label>
          {errors.agreeTerms && <span className="field-error">{errors.agreeTerms}</span>}
        </div>

        <button type="submit" className="btn-auth-submit" disabled={loading}>
          {loading ? (
            <span className="btn-spinner-wrap">
              <span className="button-spinner"></span>
              Creating Account...
            </span>
          ) : (
            <>
              Create Customer Account
              <ArrowRight size={18} />
            </>
          )}
        </button>
      </form>

      <div className="auth-footer-prompt">
        Already have an account?{' '}
        <Link to="/login" className="auth-switch-link">
          Sign In
        </Link>
      </div>
    </div>
  );
};

export default Register;
