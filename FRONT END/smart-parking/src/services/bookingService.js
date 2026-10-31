import api from './api';

export const bookingService = {
  async createBooking(data) {
    const response = await api.post('/bookings', data);
    return response.data;
  },

  async getMyBookings() {
    const response = await api.get('/bookings/my-bookings');
    return response.data;
  },

  // Alias for backward compatibility
  async getUserBookings(userId) {
    return this.getMyBookings();
  },

  async getBookingById(id) {
    const response = await api.get(`/bookings/${id}`);
    return response.data;
  },

  async getAllBookings(facilityId = null, status = null) {
    let url = '/bookings';
    const params = new URLSearchParams();
    if (facilityId) params.append('facilityId', facilityId);
    if (status) params.append('status', status);
    if (params.toString()) url += `?${params.toString()}`;
    const response = await api.get(url);
    return response.data;
  },

  async checkAvailability(facilityId, startTime, endTime) {
    const params = new URLSearchParams({
      facilityId,
      startTime,
      endTime
    });
    const response = await api.get(`/bookings/availability?${params.toString()}`);
    return response.data;
  },

  async checkIn(id) {
    const response = await api.put(`/bookings/${id}/check-in`);
    return response.data;
  },

  async checkOut(id) {
    const response = await api.put(`/bookings/${id}/check-out`);
    return response.data;
  },

  async cancelBooking(id) {
    const response = await api.put(`/bookings/${id}/cancel`);
    return response.data;
  }
};

export default bookingService;
