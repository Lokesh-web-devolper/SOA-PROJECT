import React from 'react';
import './Loader.css';

const Loader = ({ message = 'Loading...', fullScreen = false }) => {
  if (fullScreen) {
    return (
      <div className="fullscreen-loader-container">
        <div className="spinner"></div>
        <p className="loader-text">{message}</p>
      </div>
    );
  }

  return (
    <div className="inline-loader-container">
      <div className="spinner"></div>
      <p className="loader-text">{message}</p>
    </div>
  );
};

export default Loader;
