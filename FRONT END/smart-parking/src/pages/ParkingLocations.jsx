import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import parkingService from '../services/parkingService';
import { formatCurrency } from '../utils/formatters';
import Loader from '../components/Loader';
import EmptyState from '../components/EmptyState';
import { MapPin, Search, Filter, Car, ArrowRight, ShieldCheck } from 'lucide-react';
import './ParkingLocations.css';

const ParkingLocations = () => {
  const navigate = useNavigate();
  const [facilities, setFacilities] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterOnlyAvailable, setFilterOnlyAvailable] = useState(false);
  const [maxPrice, setMaxPrice] = useState('');

  useEffect(() => {
    const fetchFacilities = async () => {
      try {
        setLoading(true);
        const res = await parkingService.getFacilities(true);
        if (res && res.data) {
          setFacilities(res.data);
        }
      } catch (err) {
        console.error('Failed to load facilities:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchFacilities();
  }, []);

  const filtered = facilities.filter((f) => {
    const matchesSearch =
      f.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      f.address.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesAvail = filterOnlyAvailable ? f.availableSlots > 0 : true;

    const matchesPrice = maxPrice ? (f.startingPrice || 0) <= parseFloat(maxPrice) : true;

    return matchesSearch && matchesAvail && matchesPrice;
  });

  return (
    <div className="locations-page">
      <main className="locations-container">
        {/* PAGE HEADER */}
        <div className="page-header">
          <div>
            <h1 className="page-title">Parking Facilities & Network</h1>
            <p className="page-subtitle">
              Locate automated parking garages equipped with real-time occupancy sensors and EV charging stations.
            </p>
          </div>
        </div>

        {/* SEARCH & FILTERS BAR */}
        <div className="filter-controls-bar">
          <div className="search-input-group">
            <Search size={18} className="search-icon" />
            <input
              type="text"
              placeholder="Search by facility name or street address..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          <div className="filter-actions-group">
            <label className="checkbox-filter">
              <input
                type="checkbox"
                checked={filterOnlyAvailable}
                onChange={(e) => setFilterOnlyAvailable(e.target.checked)}
              />
              <span>Available Slots Only</span>
            </label>

            <div className="price-filter-input">
              <span>Max $/hr:</span>
              <input
                type="number"
                placeholder="Any"
                value={maxPrice}
                onChange={(e) => setMaxPrice(e.target.value)}
                min="0"
                step="1"
              />
            </div>
          </div>
        </div>

        {/* FACILITIES GRID */}
        {loading ? (
          <Loader message="Querying live parking facilities..." />
        ) : filtered.length === 0 ? (
          <EmptyState
            title="No facilities found"
            description="No parking facilities match your search criteria. Try adjusting your filters."
            actionLabel="Reset Filters"
            onAction={() => {
              setSearchTerm('');
              setFilterOnlyAvailable(false);
              setMaxPrice('');
            }}
          />
        ) : (
          <div className="facilities-grid">
            {filtered.map((facility) => {
              const hasFreeSlots = facility.availableSlots > 0;
              return (
                <div key={facility.id} className="facility-card">
                  <div className="f-card-header">
                    <span className={`status-pill ${hasFreeSlots ? 'available' : 'full'}`}>
                      {hasFreeSlots ? `${facility.availableSlots} Slots Free` : 'Full / Occupied'}
                    </span>
                    <span className="rate-tag">{formatCurrency(facility.startingPrice)}/hr</span>
                  </div>

                  <h3 className="f-title">{facility.name}</h3>
                  <p className="f-address">
                    <MapPin size={15} />
                    {facility.address}
                  </p>

                  {facility.description && (
                    <p className="f-desc">{facility.description}</p>
                  )}

                  <div className="capacity-bar-wrapper">
                    <div className="capacity-labels">
                      <span>Occupancy: {facility.occupiedSlots} / {facility.totalSlots}</span>
                      <span>
                        {facility.totalSlots > 0
                          ? Math.round((facility.occupiedSlots / facility.totalSlots) * 100)
                          : 0}
                        %
                      </span>
                    </div>
                    <div className="progress-track">
                      <div
                        className="progress-fill"
                        style={{
                          width: `${
                            facility.totalSlots > 0
                              ? (facility.occupiedSlots / facility.totalSlots) * 100
                              : 0
                          }%`
                        }}
                      ></div>
                    </div>
                  </div>

                  <div className="f-card-actions">
                    <Link to={`/parking/${facility.id}`} className="btn-details">
                      View Slot Map
                    </Link>
                    <Link
                      to={`/booking?facilityId=${facility.id}`}
                      className="btn-reserve"
                    >
                      Reserve Now
                      <ArrowRight size={15} />
                    </Link>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </main>
    </div>
  );
};

export default ParkingLocations;
