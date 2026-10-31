import React from 'react';
import './StatCard.css';

const StatCard = ({ title, value, subtitle, icon: Icon, color = 'blue' }) => {
  return (
    <div className={`stat-card ${color}`}>
      <div className="stat-card-header">
        <span className="stat-card-title">{title}</span>
        {Icon && (
          <div className={`stat-card-icon-box ${color}`}>
            <Icon size={20} />
          </div>
        )}
      </div>
      <div className="stat-card-value">{value}</div>
      {subtitle && <div className="stat-card-subtitle">{subtitle}</div>}
    </div>
  );
};

export default StatCard;
