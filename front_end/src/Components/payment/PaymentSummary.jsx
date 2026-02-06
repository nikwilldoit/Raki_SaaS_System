// src/Components/payment/PaymentSummary.jsx
import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import './Payment.css';

const PaymentSummary = () => {
    const { orderId } = useParams();
    const location = useLocation();
    const navigate = useNavigate();

    const [tip, setTip] = useState(0);
    const [paymentData, setPaymentData] = useState(null);
    const [loading, setLoading] = useState(true);

    const safe = (v) => Number(v ?? 0);

    useEffect(() => {
        const fetchOrder = async () => {
            try {
                setLoading(true);
                const token = localStorage.getItem('authToken');
                if (!token) {
                    alert('No auth token');
                    return;
                }

                const res = await fetch(`http://localhost:8080/api/orders/${orderId}`, {
                    headers: { Authorization: 'Bearer ' + token },
                });

                if (!res.ok) {
                    console.error('Failed to load order', res.status);
                    alert('Failed to load order');
                    return;
                }

                const body = await res.json(); // { order, items }
                const items = (body.items || []).map((it) => ({
                    ...it,
                    unitPrice: Number(it.unitPrice ?? 0),
                    quantity: Number(it.quantity ?? 1),
                }));

                const subtotal = items.reduce(
                    (sum, it) => sum + it.unitPrice * it.quantity,
                    0
                );

                // Αν έρχεσαι από createOrder μπορείς να σεβαστείς τα discount από location.state
                const loc = location.state || {};
                const discountPercent = loc.discountPercent ?? 0;
                const discountAmount = loc.discountAmount ?? 0;
                const total = subtotal - discountAmount;

                setPaymentData({
                    orderId: Number(orderId),
                    businessId: body.order?.businessId,
                    subtotal,
                    discountPercent,
                    discountAmount,
                    total,
                    items,
                });
            } catch (e) {
                console.error('Error loading order', e);
                alert('Error loading order');
            } finally {
                setLoading(false);
            }
        };

        fetchOrder();
    }, [orderId, location.state]);

    if (loading || !paymentData) {
        return <div>Loading order...</div>;
    }

    const {
        subtotal,
        discountPercent,
        discountAmount,
        total,
        items,
        businessId,
    } = paymentData;

    const displayItems = items.map((it) => {
        const unitPrice = safe(it.unitPrice);
        return {
            ...it,
            name: it.name || `Item #${it.productId}`,
            unitPrice,
        };
    });

    const goToSplit = (mode) => {
        navigate(`/orders/${orderId}/payment/split`, {
            state: {
                mode,
                orderId,
                businessId,
                subtotal,
                discountPercent,
                discountAmount,
                total,
                items,
            },
        });
    };

    const handlePayAll = async () => {
        const token = localStorage.getItem('authToken');
        if (!token) {
            alert('No auth token');
            return;
        }

        const totalWithTip = safe(total) + safe(tip);

        const payload = {
            businessId,
            orderId: Number(orderId),
            totalAmount: totalWithTip,
            totalTip: safe(tip),
            paymentMethod: 'CASH',
            split: false,
            splits: [],
            discountPercent,
            discountAmount,
        };

        try {
            const res = await fetch('http://localhost:8080/api/payments', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: 'Bearer ' + token,
                },
                body: JSON.stringify(payload),
            });

            if (!res.ok) {
                console.error(await res.text());
                alert('Payment failed');
                return;
            }

            const body = await res.json();

            if (body.orderCompleted) {
                navigate('/dashboard');
            } else {
                navigate(`/orders/${orderId}/payment`, {
                    state: {
                        orderId: Number(orderId),
                        businessId,
                        subtotal: body.subtotal,
                        discountPercent: body.discountPercent,
                        discountAmount: body.discountAmount,
                        total: body.total,
                        items: body.remainingItems,
                    },
                });
            }
        } catch (e) {
            console.error(e);
            alert('Network error while processing payment');
        }
    };

    const leftToPay = safe(total) + safe(tip);

    return (
        <div className="pay-page">
            <header className="pay-header">
                <h1>Order Summary</h1>
                <div className="pay-subtitle">Order #{orderId}</div>
            </header>

            <section className="pay-bill">
                <div className="pay-row">
                    <span>Your bill</span>
                    <span>{safe(total).toFixed(2)} €</span>
                </div>

                <div className="pay-row">
                    <span>Tip</span>
                    <input
                        type="number"
                        min="0"
                        step="0.01"
                        value={tip}
                        onChange={(e) => setTip(Number(e.target.value) || 0)}
                        className="pay-tip-input"
                    />
                </div>

                <div className="pay-row">
                    <span>Left to pay</span>
                    <span>{leftToPay.toFixed(2)} €</span>
                </div>
            </section>

            <section className="pay-cart">
                <div className="pay-cart-header">Order Cart</div>
                {displayItems.map((item) => (
                    <div key={item.orderItemId} className="pay-cart-line">
                        <div className="pay-qty-box">{item.quantity}</div>
                        <div className="pay-line-main">
                            <div className="pay-product-name">{item.name}</div>
                            <div className="pay-product-desc">
                                {safe(item.unitPrice).toFixed(2)}€ per item
                            </div>
                        </div>
                        <div className="pay-line-center">Line Item</div>
                        <div className="pay-line-right">
                            <div className="pay-price">
                                {(safe(item.unitPrice) * safe(item.quantity)).toFixed(2)} €
                            </div>
                        </div>
                    </div>
                ))}
            </section>

            <footer className="pay-footer dual">
                <button className="btn-light" onClick={() => goToSplit('SPLIT')}>
                    Split
                </button>
                <button className="btn-dark" onClick={handlePayAll}>
                    Pay all
                </button>
            </footer>
        </div>
    );
};

export default PaymentSummary;
