import React, { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';
import billingService from '../services/billingService';
import Loader from '../components/Loader';
import EmptyState from '../components/EmptyState';
import { useNotification } from '../context/NotificationContext';
import { formatCurrency, formatDateTime } from '../utils/formatters';
import { Receipt, CreditCard, CheckCircle2, Clock, Download, Printer, X, Eye } from 'lucide-react';
import './Billing.css';

export default function Billing() {
  const { user } = useAuth();
  const { showSuccess, showError } = useNotification();
  const [invoices, setInvoices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedInvoice, setSelectedInvoice] = useState(null);
  const [paying, setPaying] = useState(false);

  const fetchInvoices = async () => {
    try {
      setLoading(true);
      const res = await billingService.getMyInvoices();
      if (res.data) {
        setInvoices(res.data);
      }
    } catch (err) {
      showError('Failed to load invoices');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchInvoices();
  }, []);

  const totalSpent = invoices.reduce((sum, inv) => sum + (Number(inv.totalAmount) || 0), 0);
  const paidCount = invoices.filter(i => i.paymentStatus === 'PAID').length;
  const pendingCount = invoices.length - paidCount;

  const handleSimulatePayment = (invoice) => {
    setPaying(true);
    setTimeout(() => {
      setPaying(false);
      showSuccess(`Payment of ${formatCurrency(invoice.totalAmount)} processed successfully!`);
      // Update local state
      setInvoices(prev => prev.map(inv => inv.id === invoice.id ? { ...inv, paymentStatus: 'PAID' } : inv));
      if (selectedInvoice?.id === invoice.id) {
        setSelectedInvoice({ ...selectedInvoice, paymentStatus: 'PAID' });
      }
    }, 1200);
  };

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="billing-page-container">
      <div className="billing-page-header">
        <div>
          <h1 className="billing-title">Billing & Invoices</h1>
          <p className="billing-subtitle">Manage payment statements, parking invoices, and transaction histories</p>
        </div>
      </div>

      <div className="billing-summary-grid">
        <div className="billing-stat-card">
          <div className="stat-icon-wrapper blue">
            <Receipt size={24} />
          </div>
          <div>
            <span className="stat-label">Total Invoices</span>
            <h3 className="stat-value">{invoices.length}</h3>
          </div>
        </div>

        <div className="billing-stat-card">
          <div className="stat-icon-wrapper green">
            <CreditCard size={24} />
          </div>
          <div>
            <span className="stat-label">Total Amount Spent</span>
            <h3 className="stat-value">{formatCurrency(totalSpent)}</h3>
          </div>
        </div>

        <div className="billing-stat-card">
          <div className="stat-icon-wrapper amber">
            <Clock size={24} />
          </div>
          <div>
            <span className="stat-label">Pending Payments</span>
            <h3 className="stat-value">{pendingCount}</h3>
          </div>
        </div>
      </div>

      {loading ? (
        <Loader text="Loading your invoices..." />
      ) : invoices.length === 0 ? (
        <EmptyState
          icon={Receipt}
          title="No Invoices Yet"
          description="Invoices are generated automatically once you check out from a parking slot."
          actionText="View My Bookings"
          actionLink="/my-bookings"
        />
      ) : (
        <div className="invoices-table-card">
          <div className="table-header-title">
            <h3>Recent Invoices</h3>
            <span className="table-subtitle">Showing {invoices.length} records</span>
          </div>

          <div className="table-responsive">
            <table className="invoices-table">
              <thead>
                <tr>
                  <th>Invoice No.</th>
                  <th>Booking Reference</th>
                  <th>Duration</th>
                  <th>Rate</th>
                  <th>Total Amount</th>
                  <th>Status</th>
                  <th>Generated At</th>
                  <th style={{ textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {invoices.map((inv) => (
                  <tr key={inv.id}>
                    <td className="inv-num-cell">
                      <strong>{inv.invoiceNumber || `INV-#${inv.id}`}</strong>
                    </td>
                    <td>Booking #{inv.bookingId}</td>
                    <td>{inv.durationMinutes || 0} mins</td>
                    <td>{formatCurrency(inv.hourlyRate || 0)}/hr</td>
                    <td className="amount-cell">{formatCurrency(inv.totalAmount || 0)}</td>
                    <td>
                      <span className={`status-pill ${inv.paymentStatus === 'PAID' ? 'paid' : 'pending'}`}>
                        {inv.paymentStatus || 'PENDING'}
                      </span>
                    </td>
                    <td>{formatDateTime(inv.createdAt)}</td>
                    <td style={{ textAlign: 'right' }}>
                      <button
                        className="btn-action-view"
                        onClick={() => setSelectedInvoice(inv)}
                        title="View Details"
                      >
                        <Eye size={16} /> View
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Invoice Modal */}
      {selectedInvoice && (
        <div className="invoice-modal-backdrop" onClick={() => setSelectedInvoice(null)}>
          <div className="invoice-modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <div className="modal-brand">
                <span className="brand-dot"></span>
                <h2>ParkGrid Solutions — Official Invoice</h2>
              </div>
              <button className="btn-close" onClick={() => setSelectedInvoice(null)}>
                <X size={20} />
              </button>
            </div>

            <div className="invoice-body printable-area">
              <div className="invoice-meta-row">
                <div>
                  <p className="invoice-meta-label">INVOICE NUMBER</p>
                  <p className="invoice-meta-val">{selectedInvoice.invoiceNumber || `INV-#${selectedInvoice.id}`}</p>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <p className="invoice-meta-label">ISSUE DATE</p>
                  <p className="invoice-meta-val">{formatDateTime(selectedInvoice.createdAt)}</p>
                </div>
              </div>

              <div className="invoice-details-box">
                <div className="details-col">
                  <span className="details-label">Billed To</span>
                  <span className="details-value">{user?.name || 'Customer'}</span>
                  <span className="details-sub">{user?.email}</span>
                </div>
                <div className="details-col">
                  <span className="details-label">Booking Reference</span>
                  <span className="details-value">Booking ID #{selectedInvoice.bookingId}</span>
                </div>
                <div className="details-col">
                  <span className="details-label">Payment Status</span>
                  <span className={`status-pill large ${selectedInvoice.paymentStatus === 'PAID' ? 'paid' : 'pending'}`}>
                    {selectedInvoice.paymentStatus || 'PENDING'}
                  </span>
                </div>
              </div>

              <table className="breakdown-table">
                <thead>
                  <tr>
                    <th>Description</th>
                    <th>Duration</th>
                    <th>Rate</th>
                    <th style={{ textAlign: 'right' }}>Amount</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>Automated Parking Slot Allocation & Services</td>
                    <td>{selectedInvoice.durationMinutes} minutes</td>
                    <td>{formatCurrency(selectedInvoice.hourlyRate)} / hour</td>
                    <td style={{ textAlign: 'right' }}>{formatCurrency(selectedInvoice.totalAmount)}</td>
                  </tr>
                </tbody>
              </table>

              <div className="invoice-totals">
                <div className="total-row">
                  <span>Subtotal:</span>
                  <span>{formatCurrency(selectedInvoice.totalAmount)}</span>
                </div>
                <div className="total-row">
                  <span>Taxes (0% Academic Tier):</span>
                  <span>{formatCurrency(0)}</span>
                </div>
                <div className="total-row grand">
                  <span>Grand Total:</span>
                  <span>{formatCurrency(selectedInvoice.totalAmount)}</span>
                </div>
              </div>

              {selectedInvoice.paymentStatus === 'PAID' ? (
                <div className="paid-stamp">
                  <CheckCircle2 size={18} /> PAID IN FULL
                </div>
              ) : (
                <div className="payment-action-prompt">
                  <p>This invoice has a balance due.</p>
                  <button
                    className="btn-pay-now"
                    onClick={() => handleSimulatePayment(selectedInvoice)}
                    disabled={paying}
                  >
                    <CreditCard size={18} /> {paying ? 'Processing Payment...' : `Pay ${formatCurrency(selectedInvoice.totalAmount)} Now`}
                  </button>
                </div>
              )}
            </div>

            <div className="modal-footer">
              <button className="btn-secondary" onClick={handlePrint}>
                <Printer size={16} /> Print Receipt
              </button>
              <button className="btn-secondary" onClick={() => setSelectedInvoice(null)}>
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
