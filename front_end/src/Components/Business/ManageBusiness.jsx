// ManageBusiness.jsx
import React, { useEffect, useState } from 'react';
import './ManageBusiness.css';
import Header from '../Header/Header';

const API_BASE = 'http://localhost:8080';

const ManageBusiness = ({ userData, onLogout }) => {
    const [form, setForm] = useState({
        id: '',
        name: '',
        address: '',
        type: '',
        phone: '',
        isActive: 'OPEN',
    });

    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const token = userData?.token || localStorage.getItem('authToken') || null;

    useEffect(() => {
        if (!userData) {
            setError('No user');
            return;
        }
        if (!token) {
            setError('No token');
            return;
        }

        const loadBusiness = async () => {
            try {
                setError('');

                const res = await fetch(`${API_BASE}/api/businesses/me`, {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`,
                    },
                });

                if (!res.ok) {
                    const text = await res.text();
                    console.log('raw body =', text);
                    throw new Error(`Failed to load business: ${res.status}`);
                }

                const b = await res.json();
                setForm({
                    id: b.id ?? '',
                    name: b.name ?? '',
                    address: b.address ?? '',
                    type: b.type ?? '',
                    phone: b.phone ?? '',
                    isActive: b.isActive ?? 'OPEN',
                });
            } catch (e) {
                console.error(e);
                setError(e.message || 'Failed to load business');
            }
        };

        loadBusiness();
    }, [userData, token]);

    if (!userData) {
        return <div className="manage-business-page">No user</div>;
    }

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm((prev) => ({ ...prev, [name]: value }));
        setError('');
        setSuccess('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!token) {
            setError('No token');
            return;
        }

        setSaving(true);
        setError('');
        setSuccess('');

        try {
            const res = await fetch(`${API_BASE}/api/businesses/me`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify(form),
            });

            if (!res.ok) {
                const text = await res.text();
                console.log('raw body =', text);
                throw new Error(`Failed to save business: ${res.status}`);
            }

            const updated = await res.json();
            setForm((prev) => ({
                ...prev,
                ...updated,
            }));
            setSuccess('Business saved successfully');
        } catch (e) {
            console.error(e);
            setError(e.message || 'Failed to save business');
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className="manage-business-page">
            <Header userData={userData} onLogout={onLogout} />

            <div className="manage-business-content">
                <h2>Business Management</h2>
                <form className="manage-business-form" onSubmit={handleSubmit}>
                    {error && <div className="mb-error">{error}</div>}
                    {success && <div className="mb-success">{success}</div>}

                    <label>
                        Business Name
                        <input
                            type="text"
                            name="name"
                            value={form.name}
                            onChange={handleChange}
                        />
                    </label>

                    <label>
                        Business Address
                        <input
                            type="text"
                            name="address"
                            value={form.address}
                            onChange={handleChange}
                        />
                    </label>

                    {/* Business Type κρυφό, δεν το δείχνεις */}

                    <label>
                        Phone
                        <input
                            type="text"
                            name="phone"
                            value={form.phone}
                            onChange={handleChange}
                        />
                    </label>

                    <label>
                        Status
                        <select
                            name="isActive"
                            value={form.isActive}
                            onChange={handleChange}
                        >
                            <option value="OPEN">Open</option>
                            <option value="CLOSED">Closed</option>
                        </select>
                    </label>

                    <button type="submit" disabled={saving}>
                        {saving ? 'Saving...' : 'Save'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default ManageBusiness;
