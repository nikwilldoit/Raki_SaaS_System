import React, { useState, useEffect } from "react";
import "./ReservationEdit.css"; 

const ReservationEdit = ({ reservation, isOpen, onClose, onSave }) => {
  // Initialize form with reservation data
  const [formData, setFormData] = useState({
    customerName: "",
    customerPhone: "",
    status: "PENDING",
  });

  // Update form when reservation changes
  useEffect(() => {
    if (reservation) {
      setFormData({
        customerName: reservation.customerName || "",
        customerPhone: reservation.customerPhone || "",
        status: reservation.status || "PENDING",
      });
    }
  }, [reservation]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // Pass the ID and the new data back to the parent
    onSave(reservation.id, formData);
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <header className="modal-header">
          <h3>Edit Reservation</h3>
          <button className="close-btn" type="button" onClick={onClose}>×</button>
        </header>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Customer Name</label>
            <input
              type="text"
              name="customerName"
              value={formData.customerName}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Customer Phone</label>
            <input
              type="text"
              name="customerPhone"
              value={formData.customerPhone}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Status</label>
            <select name="status" value={formData.status} onChange={handleChange}>
              <option value="PENDING">Pending</option>
              <option value="CONFIRMED">Confirmed</option>
              <option value="COMPLETED">Completed</option>
              <option value="CANCELLED">Cancelled</option>
              <option value="NO_SHOW">No Show</option>
            </select>
          </div>

          <div className="modal-actions">
            <button type="button" className="ghost-button" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="primary-button">
              Save Changes
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ReservationEdit;