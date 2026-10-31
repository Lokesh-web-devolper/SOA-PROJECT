import React, { createContext, useContext, useState, useEffect } from 'react';
import authService from '../services/authService';
import { USER_ROLES } from '../utils/constants';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initAuth = async () => {
      const token = authService.getToken();
      const storedUser = authService.getStoredUser();

      if (token && storedUser) {
        setUser(storedUser);
        try {
          // Verify & refresh profile with backend
          const res = await authService.getCurrentUser();
          if (res && res.data) {
            setUser(res.data);
            localStorage.setItem('parkgrid_user', JSON.stringify(res.data));
          }
        } catch {
          // If token expired, clear
          authService.logout();
          setUser(null);
        }
      }
      setLoading(false);
    };

    initAuth();
  }, []);

  const login = async (email, password) => {
    const data = await authService.login(email, password);
    setUser(data);
    return data;
  };

  const register = async (formData) => {
    const data = await authService.register(formData);
    setUser(data);
    return data;
  };

  const logout = () => {
    authService.logout();
    setUser(null);
    window.location.href = '/login';
  };

  const updateProfileState = (updatedUser) => {
    setUser(updatedUser);
    localStorage.setItem('parkgrid_user', JSON.stringify(updatedUser));
  };

  const isAdmin = user?.role === USER_ROLES.ADMIN;
  const isCustomer = user?.role === USER_ROLES.CUSTOMER;

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        login,
        register,
        logout,
        updateProfileState,
        isAuthenticated: !!user,
        isAdmin,
        isCustomer
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export default AuthContext;
