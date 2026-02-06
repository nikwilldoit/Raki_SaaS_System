import React, { useEffect, useState } from 'react';
import Header from '../Header/Header';
import './ManageStaff.css';
import StaffForm from './StaffForm';

const API_BASE = 'http://localhost:8080';

const ManageStaff = ({ userData, onLogout }) => {
    const [staff, setStaff] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [editing, setEditing] = useState(null); // staff member
    const [showForm, setShowForm] = useState(false);

    const fetchStaff = async () => {
        try {
            setLoading(true);
            setError('');
            const token = localStorage.getItem('authToken');
            const res = await fetch(`${API_BASE}/api/staff/me/business`, {
                headers: { Authorization: 'Bearer ' + token },
            });
            if (!res.ok) {
                throw new Error('Failed to load staff');
            }
            const data = await res.json();
            setStaff(data);
        } catch (e) {
            console.error('Fetch staff error:', e);
            setError('Error loading staff.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchStaff();
    }, []);

    const handleEdit = (member) => {
        setEditing(member);
        setShowForm(true);
    };

    const handleFormClose = (changed) => {
        setShowForm(false);
        setEditing(null);
        if (changed) {
            fetchStaff();
        }
    };

    return (
        <div className="staff-page">
            <Header userData={userData} onLogout={onLogout} />
            <div className="staff-content">
                <h2>Staff Management</h2>

                {error && <div className="staff-error">{error}</div>}

                {loading ? (
                    <div>Loading staff...</div>
                ) : staff.length === 0 ? (
                    <div>No employees found for this business.</div>
                ) : (
                    <table className="staff-table">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th />
                        </tr>
                        </thead>
                        <tbody>
                        {staff.map((m) => (
                            <tr key={m.id}>
                                <td>{m.name}</td>
                                <td>{m.email}</td>
                                <td>{m.phone}</td>
                                <td>{m.role}</td>
                                <td>{m.status}</td>
                                <td>
                                    <button
                                        className="staff-edit-btn"
                                        onClick={() => handleEdit(m)}
                                    >
                                        Edit
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}

                {showForm && editing && (
                    <StaffForm
                        userData={userData}
                        staffMember={editing}
                        onClose={handleFormClose}
                    />
                )}
            </div>
        </div>
    );
};

export default ManageStaff;
