import api from './api';

export const parkingService = {
  async getFacilities(activeOnly = false) {
    const response = await api.get(`/parking/facilities?activeOnly=${activeOnly}`);
    return response.data;
  },

  // Alias
  async getAllFacilities(activeOnly = false) {
    return this.getFacilities(activeOnly);
  },

  async getFacilityById(id) {
    const response = await api.get(`/parking/facilities/${id}`);
    return response.data;
  },

  async createFacility(data) {
    const response = await api.post('/parking/facilities', data);
    return response.data;
  },

  async updateFacility(id, data) {
    const response = await api.put(`/parking/facilities/${id}`, data);
    return response.data;
  },

  async deleteFacility(id) {
    const response = await api.delete(`/parking/facilities/${id}`);
    return response.data;
  },

  async getZones(facilityId = null, activeOnly = false) {
    let url = `/parking/zones?activeOnly=${activeOnly}`;
    if (facilityId) url += `&facilityId=${facilityId}`;
    const response = await api.get(url);
    return response.data;
  },

  async createZone(data) {
    const response = await api.post('/parking/zones', data);
    return response.data;
  },

  async updateZone(id, data) {
    const response = await api.put(`/parking/zones/${id}`, data);
    return response.data;
  },

  async deleteZone(id) {
    const response = await api.delete(`/parking/zones/${id}`);
    return response.data;
  },

  async getSlots(params = {}) {
    const query = new URLSearchParams();
    if (params.facilityId) query.append('facilityId', params.facilityId);
    if (params.zoneId) query.append('zoneId', params.zoneId);
    if (params.status) query.append('status', params.status);
    if (params.slotType) query.append('slotType', params.slotType);

    const response = await api.get(`/parking/slots?${query.toString()}`);
    return response.data;
  },

  async getSlotById(id) {
    const response = await api.get(`/parking/slots/${id}`);
    return response.data;
  },

  async createSlot(data) {
    const response = await api.post('/parking/slots', data);
    return response.data;
  },

  async updateSlot(id, data) {
    const response = await api.put(`/parking/slots/${id}`, data);
    return response.data;
  },

  async updateSlotStatus(id, status) {
    const response = await api.put(`/parking/slots/${id}/status`, { status });
    return response.data;
  },

  async deleteSlot(id) {
    const response = await api.delete(`/parking/slots/${id}`);
    return response.data;
  },

  async getLiveAvailability() {
    const response = await api.get('/parking/availability');
    return response.data;
  },

  async getStatistics() {
    const response = await api.get('/parking/statistics');
    return response.data;
  }
};

export default parkingService;
