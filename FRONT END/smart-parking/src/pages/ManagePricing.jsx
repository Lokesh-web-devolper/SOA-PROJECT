import React, { useState, useEffect } from 'react';
import parkingService from '../services/parkingService';
import billingService from '../services/billingService';
import Loader from '../components/Loader';
import { useNotification } from '../context/NotificationContext';
import { formatCurrency } from '../utils/formatters';
import { TrendingUp, DollarSign, Calculator, Clock, Save, Zap } from 'lucide-react';
import './ManagePricing.css';

export default function ManagePricing() {
  const { showSuccess, showError } = useNotification();
  const [facilities, setFacilities] = useState([]);
  const [loading, setLoading] = useState(true);

  // Rate edit states
  const [editingId, setEditingId] = useState(null);
  const [editRate, setEditRate] = useState('');
  const [saving, setSaving] = useState(false);

  // Simulation calculator
  const [simHours, setSimHours] = useState(3);
  const [simRate, setSimRate] = useState(5.0);
  const [simResult, setSimResult] = useState(null);
  const [calculating, setCalculating] = useState(false);

  const loadFacilities = async () => {
    try {
      setLoading(true);
      const res = await parkingService.getFacilities(false);
      if (res.data) {
        setFacilities(res.data);
      }
    } catch (err) {
      showError('Failed to fetch facility pricing');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadFacilities();
  }, []);

  const handleStartEdit = (fac) => {
    setEditingId(fac.id);
    setEditRate(fac.hourlyRate);
  };

  const handleSaveRate = async (fac) => {
    try {
      setSaving(true);
      await parkingService.updateFacility(fac.id, {
        ...fac,
        hourlyRate: parseFloat(editRate) || fac.hourlyRate
      });
      showSuccess(`Updated hourly rate for ${fac.name} to ${formatCurrency(editRate)}`);
      setEditingId(null);
      loadFacilities();
    } catch (err) {
      showError(err.response?.data?.message || 'Failed to update rate');
    } finally {
      setSaving(false);
    }
  };

  const handleRunSimulation = async (e) => {
    e.preventDefault();
    try {
      setCalculating(true);
      const now = new Date();
      const exitTime = new Date(now.getTime() + simHours * 60 * 60 * 1000);

      const res = await billingService.calculateFee({
        entryTime: now.toISOString(),
        exitTime: exitTime.toISOString(),
        hourlyRate: parseFloat(simRate) || 5.0
      });

      if (res.data) {
        setSimResult(res.data);
      }
    } catch (err) {
      showError('Simulation failed');
    } finally {
      setCalculating(false);
    }
  };

  return (
    <div className="manage-pricing-container">
      <div className="manage-header">
        <div>
          <h1 className="manage-title">Dynamic Tariff Engine</h1>
          <p className="manage-subtitle">Configure hourly parking rates, grace periods, and test automated duration billing</p>
        </div>
      </div>

      <div className="pricing-grid">
        <div className="pricing-left-col">
          <div className="table-card">
            <div className="card-header-bar">
              <div>
                <h3>Facility Tariff Rates</h3>
                <p>Live hourly base fees applied upon slot reservation check-out</p>
              </div>
            </div>

            {loading ? (
              <Loader text="Loading tariffs..." />
            ) : (
              <div className="table-wrapper">
                <table className="manage-table">
                  <thead>
                    <tr>
                      <th>Facility</th>
                      <th>Location</th>
                      <th>Current Rate / Hour</th>
                      <th style={{ textAlign: 'right' }}>Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {facilities.map((fac) => (
                      <tr key={fac.id}>
                        <td><strong>{fac.name}</strong></td>
                        <td style={{ color: '#64748b' }}>{fac.address}</td>
                        <td>
                          {editingId === fac.id ? (
                            <div className="inline-edit-input">
                              <span>$</span>
                              <input
                                type="number"
                                step="0.5"
                                min="0"
                                value={editRate}
                                onChange={(e) => setEditRate(e.target.value)}
                                autoFocus
                              />
                            </div>
                          ) : (
                            <span className="rate-badge">
                              {formatCurrency(fac.hourlyRate)} / hr
                            </span>
                          )}
                        </td>
                        <td style={{ textAlign: 'right' }}>
                          {editingId === fac.id ? (
                            <div className="btn-edit-actions">
                              <button
                                className="btn-save-inline"
                                onClick={() => handleSaveRate(fac)}
                                disabled={saving}
                              >
                                <Save size={14} /> Save
                              </button>
                              <button
                                className="btn-cancel-inline"
                                onClick={() => setEditingId(null)}
                              >
                                Cancel
                              </button>
                            </div>
                          ) : (
                            <button
                              className="btn-edit-rate"
                              onClick={() => handleStartEdit(fac)}
                            >
                              Edit Rate
                            </button>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>

          <div className="rules-card">
            <h3>Tariff Computation Rules & Policies</h3>
            <div className="rules-list">
              <div className="rule-item">
                <Clock className="rule-icon" size={20} />
                <div>
                  <strong>Grace Period Policy</strong>
                  <p>First 15 minutes of any parking session are complimentary for pick-up/drop-off transit.</p>
                </div>
              </div>
              <div className="rule-item">
                <TrendingUp className="rule-icon" size={20} />
                <div>
                  <strong>Hourly Rounding Increment</strong>
                  <p>Durations exceeding the grace period are charged in 1-hour increments rounded up automatically.</p>
                </div>
              </div>
              <div className="rule-item">
                <Zap className="rule-icon" size={20} />
                <div>
                  <strong>EV & High-Demand Surge Multiplier</strong>
                  <p>EV fast-charging stations include standard grid tariffs bundled into the flat hourly structure.</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="pricing-right-col">
          <div className="simulator-card">
            <div className="sim-header">
              <Calculator size={22} className="sim-icon" />
              <div>
                <h3>Tariff Test Simulator</h3>
                <p>Run simulated check-out calculations</p>
              </div>
            </div>

            <form onSubmit={handleRunSimulation} className="sim-form">
              <div className="form-group">
                <label>Hourly Rate ($/hr)</label>
                <input
                  type="number"
                  step="0.5"
                  min="0"
                  value={simRate}
                  onChange={(e) => setSimRate(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label>Simulated Duration (Hours)</label>
                <input
                  type="number"
                  step="0.5"
                  min="0.25"
                  value={simHours}
                  onChange={(e) => setSimHours(e.target.value)}
                  required
                />
              </div>

              <button type="submit" className="btn-sim-run" disabled={calculating}>
                {calculating ? 'Computing...' : 'Calculate Estimated Fee'}
              </button>
            </form>

            {simResult && (
              <div className="sim-result-box">
                <div className="res-row">
                  <span>Billable Duration:</span>
                  <strong>{simResult.durationMinutes} mins ({simResult.billableHours} hrs)</strong>
                </div>
                <div className="res-row">
                  <span>Applied Rate:</span>
                  <strong>{formatCurrency(simResult.hourlyRate)}/hr</strong>
                </div>
                <div className="res-row total">
                  <span>Estimated Total Fee:</span>
                  <span className="res-highlight">{formatCurrency(simResult.totalFee)}</span>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
