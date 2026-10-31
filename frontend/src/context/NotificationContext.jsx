import React, { createContext, useContext } from 'react';
import toast, { Toaster } from 'react-hot-toast';

const NotificationContext = createContext(null);

export const NotificationProvider = ({ children }) => {
  const showSuccess = (message) => {
    toast.success(message, {
      duration: 3500,
      position: 'top-right',
      style: {
        background: '#0F172A',
        color: '#F8FAFC',
        borderRadius: '10px',
        padding: '12px 18px',
        fontSize: '14px',
        boxShadow: '0 10px 25px -5px rgba(0, 0, 0, 0.2)'
      }
    });
  };

  const showError = (message) => {
    toast.error(message, {
      duration: 4500,
      position: 'top-right',
      style: {
        background: '#7F1D1D',
        color: '#FEF2F2',
        borderRadius: '10px',
        padding: '12px 18px',
        fontSize: '14px',
        boxShadow: '0 10px 25px -5px rgba(0, 0, 0, 0.2)'
      }
    });
  };

  const showInfo = (message) => {
    toast(message, {
      duration: 3000,
      position: 'top-right',
      style: {
        background: '#1E293B',
        color: '#E2E8F0',
        borderRadius: '10px',
        padding: '12px 18px',
        fontSize: '14px'
      }
    });
  };

  return (
    <NotificationContext.Provider value={{ showSuccess, showError, showInfo }}>
      {children}
      <Toaster />
    </NotificationContext.Provider>
  );
};

export const useNotification = () => {
  const context = useContext(NotificationContext);
  if (!context) {
    throw new Error('useNotification must be used within a NotificationProvider');
  }
  return context;
};

export default NotificationContext;
