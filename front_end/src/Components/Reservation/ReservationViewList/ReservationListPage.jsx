import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../../Header/Header";
import ReservationEdit from "./ReservationEdit";
import "./ReservationListPage.css"; 

// --- HELPER FUNCTIONS ---
const formatDateTime = (dateStr) => {
  if (!dateStr) return "";
  try {
    const date = new Date(dateStr);
    return new Intl.DateTimeFormat("en-US", {
      month: "short",
      day: "numeric",
      hour: "numeric",
      minute: "2-digit",
    }).format(date);
  } catch {
    return dateStr;
  }
};

const ReservationListPage = ({ userData, onLogout }) => {
  const navigate = useNavigate();
  
  // State to store the list of reservations
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // --- NEW STATE FOR MODAL ---
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [editingReservation, setEditingReservation] = useState(null);

  // Helper to get token
  const getAuthToken = () =>
    localStorage.getItem("authToken") || userData?.token || "";

  // --- FETCH DATA ---
  useEffect(() => {
    const fetchReservations = async () => {
      const token = getAuthToken();
      if (!token) {
        setError("You are not logged in.");
        setLoading(false);
        return;
      }

      try {
        const params = new URLSearchParams({
          businessId: userData?.businessId || "",
        });

        const response = await fetch(
          `http://localhost:8080/api/reservations?${params.toString()}`,
          {
            method: "GET",
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
            },
          }
        );

        if (response.ok) {
          const data = await response.json();
          // Ensure we are setting an array (handle if backend wraps it in "data" object)
          const rawList = Array.isArray(data) ? data : data.data || [];

          // We loop through the list and make sure every item has an 'id'
          const list = rawList.map((item) => ({
            ...item,
            id: item.reservationId || item.id, // If reservationId exists, set it as id
          }));
          
          setReservations(list);
        } 
        else {
          setError("Failed to load reservations.");
        }
      } catch (err) {
        console.error("Error fetching list:", err);
        setError("Error connecting to server.");
      } finally {
        setLoading(false);
      }
    };

    fetchReservations();
  }, [userData]);

  // --- DELETE HANDLER ---
  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this reservation?")) return;

    try {
      const token = getAuthToken();
      // Pass businessId as param for security
      const response = await fetch(
        `http://localhost:8080/api/reservations/${id}?businessId=${userData.businessId}`,
        {
          method: "DELETE",
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      if (response.ok) {
        // Remove from list immediately (Optimistic UI update)
        setReservations((prev) => prev.filter((r) => r.id !== id));
      } else {
        alert("Failed to delete reservation.");
      }
    } catch (err) {
      console.error(err);
      alert("Error deleting reservation.");
    }
  };

  // --- EDIT HANDLERS ---
  const handleEditClick = (reservation) => {
    setEditingReservation(reservation);
    setIsEditModalOpen(true);
  };

  const handleSaveEdit = async (id, updatedData) => {
    try {
      const token = getAuthToken();
      const payload = {
        ...updatedData,
        businessId: userData.businessId,
      };

      const response = await fetch(`http://localhost:8080/api/reservations/${id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        // Update the list locally to reflect changes immediately
        setReservations((prev) =>
          prev.map((r) => (r.id === id ? { ...r, ...updatedData } : r))
        );
        setIsEditModalOpen(false);
      } else {
        alert("Failed to update reservation.");
      }
    } catch (err) {
      console.error(err);
      alert("Error updating reservation.");
    }
  };

  return (
    <div className="list-page">
      <Header userData={userData} onLogout={onLogout} />

      <main className="page-content">
        <div className="card">
          <div className="card-header-row">
            <h2>Reservations</h2>
            {/* FIXED: Changed path to singular '/reservation/new' 
              to match your App.js route 
            */}
            <button 
              className="primary-button small-btn"
              onClick={() => navigate("/reservation/new")}
            >
              + New Reservation
            </button>
          </div>

          {loading && <div className="status-text">Loading list...</div>}
          
          {error && <div className="error-text">{error}</div>}

          {!loading && !error && reservations.length === 0 && (
            <div className="empty-state">
              <p>No reservations found.</p>
            </div>
          )}

          {!loading && !error && reservations.length > 0 && (
            <div className="table-responsive">
              <table className="reservation-table">
                <thead>
                  <tr>
                    <th>Customer</th>
                    <th>Date & Time</th>
                    <th>Service</th>
                    <th>Employee</th>
                    <th>Status</th>
                    <th>Actions</th> {/* NEW COLUMN */}
                  </tr>
                </thead>
                <tbody>
                  {reservations.map((res) => (
                    <tr key={res.id || res.reservationId}>
                      <td>
                        <div className="customer-name">{res.customerName}</div>
                        <div className="customer-phone">{res.customerPhone}</div>
                      </td>
                      <td>{formatDateTime(res.date || res.appointmentTime)}</td>
                      <td>{res.serviceName || "Service"}</td>
                      <td>{res.employeeName || "Employee"}</td>
                      <td>
                        <span className={`badge ${res.status?.toLowerCase() || 'pending'}`}>
                          {res.status || "Pending"}
                        </span>
                      </td>
                      
                      {/* NEW BUTTONS */}
                      <td>
                        <div style={{ display: 'flex', gap: '10px' }}>
                          <button 
                            className="icon-button edit-btn"
                            onClick={() => handleEditClick(res)}
                            title="Edit"
                          >
                            ✎
                          </button>
                          <button 
                            className="icon-button delete-btn"
                            onClick={() => handleDelete(res.id)}
                            title="Delete"
                          >
                            🗑
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
        
        <div style={{ marginTop: "20px" }}>
            <button className="ghost-button" onClick={() => navigate("/dashboard")}>
                ← Back to Dashboard
            </button>
        </div>
      </main>

      {/* RENDER MODAL */}
      <ReservationEdit
        reservation={editingReservation}
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        onSave={handleSaveEdit}
      />
    </div>
  );
};

export default ReservationListPage;