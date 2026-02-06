// src/Components/admin/SuperAdminSelectBusiness.jsx
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../Header/Header';

const API_BASE = 'http://localhost:8080';

const SuperAdminSelectBusiness = ({ userData, setUserData, onLogout }) => {
    const navigate = useNavigate();
    const [businessName, setBusinessName] = useState('');
    const [error, setError] = useState('');
    const [saving, setSaving] = useState(false);

    useEffect(() => {
        if (!userData) return;
        if (userData.role !== 'SuperAdmin') {
            navigate('/dashboard');
        }
    }, [userData, navigate]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        if (!businessName.trim()) {
            setError('Please enter a business name.');
            return;
        }

        try {
            setSaving(true);
            const token = localStorage.getItem('authToken');

            // 1) switch business στο backend
            const res = await fetch(`${API_BASE}/api/super-admin/select-business`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: 'Bearer ' + token,
                },
                body: JSON.stringify({ businessName: businessName.trim() }),
            });

            if (!res.ok) {
                const txt = await res.text();
                console.error('Select business error:', txt);
                throw new Error('Failed to select business');
            }

            // 2) φέρε user/dashboard data
            const meRes = await fetch(`${API_BASE}/api/dashboard`, {
                method: 'GET',
                headers: {
                    Authorization: 'Bearer ' + token,
                },
            });

            if (!meRes.ok) {
                const txt = await meRes.text();
                console.error('Load dashboard data error:', txt);
                throw new Error('Failed to load dashboard data');
            }

            const dashData = await meRes.json();

            const updatedUser = {
                userId: dashData.userId,
                userName: userData.userName,        // κρατάμε το ίδιο name με το login
                role: dashData.employeeType,
                businessType: dashData.businessType,
                superAdmin: dashData.superAdmin,
                businessId: dashData.businessId,
            };

            setUserData(updatedUser);
            localStorage.setItem('user', JSON.stringify(updatedUser));
            navigate('/dashboard');

        } catch (err) {
            console.error(err);
            setError('Business not found or error selecting business.');
        } finally {
            setSaving(false);
        }
    };

    if (!userData) return null;

    return (
        <div className="superadmin-page">
            <Header userData={userData} onLogout={onLogout} />
            <div className="superadmin-card">
                <h2>Super Admin – Select Business</h2>
                <p>Enter the exact business name you want to access (e.g. "Pasta Palace").</p>

                {error && <div className="sa-error">{error}</div>}

                <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        placeholder="Business name"
                        value={businessName}
                        onChange={(e) => setBusinessName(e.target.value)}
                    />
                    <button type="submit" disabled={saving}>
                        {saving ? 'Switching...' : 'Switch business'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default SuperAdminSelectBusiness;
