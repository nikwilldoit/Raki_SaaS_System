import React, { useState } from 'react';
import './StaffForm.css';

const API_BASE = 'http://localhost:8080';

const StaffForm = ({ staffMember, onClose }) => {
    const [name, setName] = useState(staffMember.name || '');
    const [email, setEmail] = useState(staffMember.email || '');
    const [phone, setPhone] = useState(staffMember.phone || '');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState(staffMember.role || 'Employee');
    const [status, setStatus] = useState(staffMember.status || 'ACTIVE');

    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSaving(true);
        setError('');

        try {
            const token = localStorage.getItem('authToken');
            const payload = {
                name,
                email,
                phone,
                password: password || null,
                role,
                status,
            };

            const res = await fetch(`${API_BASE}/api/staff/${staffMember.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: 'Bearer ' + token,
                },
                body: JSON.stringify(payload),
            });

            if (!res.ok) {
                throw new Error('Save failed');
            }

            onClose(true);
        } catch (err) {
            console.error('Save staff error:', err);
            setError('Δεν ήταν δυνατή η αποθήκευση του εργαζομένου.');
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className="staff-form-backdrop">
            <div className="staff-form-modal">
                <h3>Edit Employee</h3>

                {error && <div className="staff-form-error">{error}</div>}

                <form onSubmit={handleSubmit} className="staff-form">
                    <label>
                        Name
                        <input
                            type="text"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                        />
                    </label>

                    <label>
                        Email
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                        />
                    </label>

                    <label>
                        Phone
                        <input
                            type="text"
                            value={phone}
                            onChange={(e) => setPhone(e.target.value)}
                        />
                    </label>

                    <label>
                        New Password
                        <input
                            type="password"
                            value={password}
                            placeholder="Leave empty to keep current"
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </label>

                    <label>
                        Role
                        <select value={role} onChange={(e) => setRole(e.target.value)}>
                            <option value="Owner">Owner</option>
                            <option value="Employee">Employee</option>
                        </select>
                    </label>

                    <label>
                        Status
                        <select value={status} onChange={(e) => setStatus(e.target.value)}>
                            <option value="ACTIVE">ACTIVE</option>
                            <option value="SUSPENDED">SUSPENDED</option>
                            <option value="FIRED">FIRED</option>
                        </select>
                    </label>

                    <div className="staff-form-actions">
                        <button type="button" onClick={() => onClose(false)}>
                            Cancel
                        </button>
                        <button type="submit" disabled={saving}>
                            {saving ? 'Saving...' : 'Save'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default StaffForm;
