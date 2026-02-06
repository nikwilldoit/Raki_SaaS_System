import React, { useState, useEffect } from 'react';
import Header from '../Header/Header';
import './OrderList.css';
import { useNavigate } from 'react-router-dom';

const OrderList = ({ userData, onLogout }) => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const businessId = userData?.businessId;
    const navigate = useNavigate();

    useEffect(() => {
        if (!businessId) return;

        const token = localStorage.getItem('authToken');
        if (!token) return;

        fetch(`http://localhost:8080/api/orders/business/${businessId}`, {
            headers: { Authorization: 'Bearer ' + token },
        })
            .then((res) => {
                if (!res.ok) throw new Error(`HTTP ${res.status}`);
                return res.json();
            })
            .then((data) => {
                const formatted = Array.isArray(data)
                    ? data.map((o) => ({
                        id: o.id,
                        orderNumber: o.orderNumber,
                        status: o.status,
                        orderDate: o.orderDate
                            ? new Date(o.orderDate).toLocaleString()
                            : '—',
                    }))
                    : [];
                setOrders(formatted);
            })
            .catch((err) => {
                console.error('Failed to load orders', err);
                setError('Failed to load orders');
            })
            .finally(() => setLoading(false));
    }, [businessId]);

    const handleRefund = async (orderId) => {
        const token = localStorage.getItem('authToken');
        try {
            const res = await fetch(
                `http://localhost:8080/api/orders/${orderId}/refund`,
                {
                    method: 'PUT',
                    headers: { Authorization: 'Bearer ' + token },
                }
            );
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            setOrders((prev) =>
                prev.map((o) =>
                    o.id === orderId ? { ...o, status: 'REFUNDED' } : o
                )
            );
        } catch (err) {
            console.error('You Cannot Refund Open Order', err);
            alert('You Cannot Refund Open Order');
        }
    };

    const handlePay = (orderId) => {
        navigate(`/orders/${orderId}/payment`, {
            state: {
                // μπορείς να περάσεις businessId αν το χρειάζεσαι στο payment
                businessId,
                from: 'order-list',
            },
        });
    };

    if (loading) return <p>Loading orders...</p>;
    if (error) return <p>{error}</p>;

    return (
        <div className="orders-page">
            <Header userData={userData} onLogout={onLogout} />

            <div className="orders-container">
                <h2 className="orders-title">Orders List</h2>

                <table className="orders-table">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Order Number</th>
                        <th>Status</th>
                        <th>Date</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    {orders.length > 0 ? (
                        orders.map((o) => (
                            <tr key={o.id}>
                                <td>{o.id}</td>
                                <td>{o.orderNumber}</td>
                                <td className={`status ${o.status.toLowerCase()}`}>
                                    {o.status}
                                </td>
                                <td>{o.orderDate}</td>
                                <td>
                                    {o.status === 'OPEN' && (
                                        <button
                                            className="pay-btn"
                                            onClick={() => handlePay(o.id)}
                                        >
                                            Pay
                                        </button>
                                    )}

                                    {o.status !== 'REFUNDED' && o.status !== 'OPEN' && (
                                        <button
                                            className="refund-btn"
                                            onClick={() => handleRefund(o.id)}
                                        >
                                            Refund
                                        </button>
                                    )}
                                </td>

                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="5" className="no-orders">
                                No orders found
                            </td>
                        </tr>
                    )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default OrderList;
