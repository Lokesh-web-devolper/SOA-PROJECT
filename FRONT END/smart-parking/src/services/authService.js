import api from './api';

export const authService = {
  async login(email, password) {
    const response = await api.post('/auth/login', { email, password });
    if (response.data && response.data.data) {
      const { token, ...user } = response.data.data;
      localStorage.setItem('parkgrid_token', token);
      localStorage.setItem('parkgrid_user', JSON.stringify(user));
      return response.data.data;
    }
    return response.data;
  },

  async register(data) {
    const response = await api.post('/auth/register', data);
    if (response.data && response.data.data) {
      const { token, ...user } = response.data.data;
      localStorage.setItem('parkgrid_token', token);
      localStorage.setItem('parkgrid_user', JSON.stringify(user));
      return response.data.data;
    }
    return response.data;
  },

  async getCurrentUser() {
    const response = await api.get('/auth/me');
    return response.data;
  },

  async updateProfile(data) {
    const response = await api.put('/auth/profile', data);
    return response.data;
  },

  async getAllUsers() {
    const response = await api.get('/auth/users');
    return response.data;
  },

  logout() {
    localStorage.removeItem('parkgrid_token');
    localStorage.removeItem('parkgrid_user');
  },

  getStoredUser() {
    const userStr = localStorage.getItem('parkgrid_user');
    try {
      return userStr ? JSON.parse(userStr) : null;
    } catch {
      return null;
    }
  },

  getToken() {
    return localStorage.getItem('parkgrid_token');
  }
};

export default authService;
