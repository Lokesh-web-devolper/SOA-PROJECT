import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useNotification } from '../context/NotificationContext';
import { Mail, Lock, Eye, EyeOff, Sparkles, ArrowRight } from 'lucide-react';
import './Login.css';

const Login = () => {
  const { login } = useAuth();
  const { showSuccess, showError, showInfo } = useNotification();
  const navigate = useNavigate();
  const location = useLocation();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(true);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const from = location.state?.from?.pathname || '/dashboard';

  const validate = () => {
    const errs = {};
    if (!email.trim()) {
      errs.email = 'Email address is required';
    } else if (!/\S+@\S+\.\S+/.test(email)) {
      errs.email = 'Please enter a valid email address';
    }

    if (!password) {
      errs.password = 'Password is required';
    }

    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setLoading(true);
    try {
      const user = await login(email.trim().toLowerCase(), password);
      showSuccess(`Welcome back, ${user.fullName || 'User'}!`);

      if (user.role === 'ROLE_ADMIN') {
        navigate('/admin/dashboard', { replace: true });
      } else {
        navigate(from === '/login' ? '/dashboard' : from, { replace: true });
      }
    } catch (err) {
      const msg = err.response?.data?.message || 'Invalid email or password. Please verify credentials.';
      showError(msg);
    } finally {
      setLoading(false);
    }
  };

  const handleForgotPassword = (e) => {
    e.preventDefault();
    showInfo('Self-service password reset is not yet configured. Contact support@parkgrid.com for assistance.');
  };

  return (
    <div className="auth-card">
      <div className="auth-header">
        <div className="auth-logo-badge">
          <Sparkles size={22} />
        </div>
        <h2 className="auth-title">Sign in to ParkGrid</h2>
        <p className="auth-subtitle">Enter your credentials to access your parking dashboard</p>
      </div>

      {/* Quick Demo Credentials Info */}
      <div className="demo-credentials-callout">
        <div className="demo-callout-title">Pre-Configured Demo Accounts:</div>
        <div className="demo-callout-grid">
          <div>
            <strong>Driver / Customer:</strong> customer@parkgrid.com / Customer@123
          </div>
          <div>
            <strong>Administrator:</strong> admin@parkgrid.com / Admin@123
          </div>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="auth-form" noValidate>
        <div className="form-group">
          <label htmlFor="email">Email Address</label>
          <div className={`input-with-icon ${errors.email ? 'has-error' : ''}`}>
            <Mail size={18} className="input-icon" />
            <input
              id="email"
              type="email"
              placeholder="name@example.com"
              value={email}
              onChange={(e) => {
                setEmail(e.target.value);
                if (errors.email) setErrors({ ...errors, email: null });
              }}
              disabled={loading}
              autoComplete="email"
            />
          </div>
          {errors.email && <span className="field-error">{errors.email}</span>}
        </div>

        <div className="form-group">
          <div className="password-label-row">
            <label htmlFor="password">Password</label>
            <a href="#forgot" className="forgot-link" onClick={handleForgotPassword}>
              Forgot password?
            </a>
          </div>
          <div className={`input-with-icon ${errors.password ? 'has-error' : ''}`}>
            <Lock size={18} className="input-icon" />
            <input
              id="password"
              type={showPassword ? 'text' : 'password'}
              placeholder="Enter your password"
              value={password}
              onChange={(e) => {
                setPassword(e.target.value);
                if (errors.password) setErrors({ ...errors, password: null });
              }}
              disabled={loading}
              autoComplete="current-password"
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

        <div className="remember-row">
          <label className="checkbox-label">
            <input
              type="checkbox"
              checked={rememberMe}
              onChange={(e) => setRememberMe(e.target.checked)}
            />
            <span>Remember me for 30 days</span>
          </label>
        </div>

        <button type="submit" className="btn-auth-submit" disabled={loading}>
          {loading ? (
            <span className="btn-spinner-wrap">
              <span className="button-spinner"></span>
              Signing in...
            </span>
          ) : (
            <>
              Sign In
              <ArrowRight size={18} />
            </>
          )}
        </button>
      </form>

      <div className="auth-footer-prompt">
        Don't have an account?{' '}
        <Link to="/register" className="auth-switch-link">
          Create an account
        </Link>
      </div>
    </div>
  );
};

export default Login;
