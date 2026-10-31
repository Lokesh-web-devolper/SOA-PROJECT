import api from './api';

export const billingService = {
  async calculateFee(data) {
    const response = await api.post('/billing/calculate', data);
    return response.data;
  },

  async createInvoice(data) {
    const response = await api.post('/billing/invoices', data);
    return response.data;
  },

  async getInvoiceById(id) {
    const response = await api.get(`/billing/invoices/${id}`);
    return response.data;
  },

  async getInvoiceByBookingId(bookingId) {
    const response = await api.get(`/billing/invoices/booking/${bookingId}`);
    return response.data;
  },

  async getMyInvoices() {
    const response = await api.get('/billing/my-invoices');
    return response.data;
  },

  async getAllInvoices() {
    const response = await api.get('/billing/invoices');
    return response.data;
  },

  async getRevenueStatistics() {
    const response = await api.get('/billing/revenue');
    return response.data;
  }
};

export default billingService;
