import React, { useEffect, useState } from 'react';
import './ManageDiscounts.css';
import DiscountForm from "./DiscountForm";
import Header from "../Header/Header";

const ManageDiscounts = ({ userData, onLogout }) => {
    const [discounts, setDiscounts] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [showForm, setShowForm] = useState(false);
    const [editingDiscount, setEditingDiscount] = useState(null);

    const fetchDiscounts = async () => {
        try {
            setLoading(true);
            setError('');
            const token = localStorage.getItem('authToken');

            const res = await fetch(
                `http://localhost:8080/api/discounts?businessId=${userData.businessId}`,
                {
                    headers: {
                        Authorization: 'Bearer ' + token,
                    },
                }
            );

            if (!res.ok) {
                throw new Error('Failed to load discounts');
            }

            const data = await res.json();
            setDiscounts(data);
        } catch (err) {
            console.error('Fetch discounts error:', err);
            setError('error loading the discounts.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchDiscounts();
    }, []);

    const handleCreate = () => {
        setEditingDiscount(null);
        setShowForm(true);
    };

    const handleEdit = (discount) => {
        setEditingDiscount(discount);
        setShowForm(true);
    };

    const handleDelete = async (id) => {
        if (!window.confirm('Are you sure about the delete')) return;

        try {
            const token = localStorage.getItem('authToken');
            const res = await fetch(
                `http://localhost:8080/api/discounts/${id}?businessId=${userData.businessId}`,
                {
                    method: 'DELETE',
                    headers: {
                        Authorization: 'Bearer ' + token,
                    },
                }
            );
            if (!res.ok) throw new Error('Delete failed');
            await fetchDiscounts();
        } catch (err) {
            console.error('Delete discount error:', err);
            setError('the remove of this discount wasnt successful.');
        }
    };

    const handleFormClose = (changed) => {
        setShowForm(false);
        setEditingDiscount(null);
        if (changed) {
            fetchDiscounts();
        }
    };

    return (
        <div className="discounts-page">
            <Header userData={userData} onLogout={onLogout} />

            <div className="discounts-header">
                <h1>Discounts</h1>
                <button className="primary-btn" onClick={handleCreate}>
                    + Create Discount
                </button>
            </div>

            {/* Filters row */}
            <div className="discounts-filters">
                <input className="filter-input" placeholder="Discount Type" disabled />
                <input className="filter-input" placeholder="Status" disabled />
                <div className="filter-date-range">
                    <input className="filter-input" placeholder="Start Date" disabled />
                    <input className="filter-input" placeholder="End Date" disabled />
                </div>
                <input className="filter-input" placeholder="Search discounts..." />
            </div>

            {error && <div className="error-banner">{error}</div>}
            {loading ? (
                <div className="loading-row">Loading...</div>
            ) : (
                <table className="discounts-table">
                    <thead>
                    <tr>
                        <th>Discount</th>
                        <th>Type</th>
                        <th>Value</th>
                        <th>Time Period</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {discounts.length === 0 && (
                        <tr>
                            <td colSpan={6} className="empty-row">
                                There are no Discounts
                            </td>
                        </tr>
                    )}
                    {discounts.map((d) => (
                        <tr key={d.id}>
                            <td>{d.name}</td>
                            <td>Product Discount</td>
                            <td>
                                {d.discountType === 'PERCENT'
                                    ? `${d.discountValue}% off`
                                    : `${d.discountValue} off`}
                            </td>
                            <td>
                                {d.startDate
                                    ? `${d.startDate} - ${d.endDate || 'No expiration'}`
                                    : 'No period'}
                            </td>
                            <td>
                  <span className={`status-badge status-${d.status.toLowerCase()}`}>
                    {d.status}
                  </span>
                            </td>
                            <td>
                                <button
                                    className="icon-btn"
                                    onClick={() => handleEdit(d)}
                                    title="Edit"
                                >
                                    ✎
                                </button>
                                <button
                                    className="icon-btn danger"
                                    onClick={() => handleDelete(d.id)}
                                    title="Delete"
                                >
                                    🗑
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}

            {showForm && (
                <DiscountForm
                    userData={userData}
                    discount={editingDiscount}
                    onClose={handleFormClose}
                />
            )}
        </div>
    );
};

export default ManageDiscounts;
