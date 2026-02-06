// src/Components/payment/PaymentSplit.jsx
import React, { useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import './Payment.css';

const PaymentSplit = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const { orderId } = useParams();

    const {
        mode,
        items,       // backend items: {orderItemId, productId, quantity, ...}
        total,
        businessId,
        subtotal,
        discountPercent,
        discountAmount,
    } = location.state || {};

    const safe = (v) => Number(v ?? 0);

    const unpaidSource = Array.isArray(items)
        ? items.filter((it) => it.paymentStatus !== 'PAID')
        : [];

    const discountFactor = 1 - safe(discountPercent) / 100;

    const initialUnpaid = unpaidSource.length
        ? unpaidSource.map((it) => {
            const qty = safe(it.quantity);
            const unit = safe(it.unitPrice);
            const baseLineTotal =
                it.totalPrice != null ? safe(it.totalPrice) : unit * qty;

            const discountedLineTotal = Number(
                (baseLineTotal * discountFactor).toFixed(2)
            );

            return {
                ...it,
                quantity: qty,
                productName: `Item #${it.productId}`,
                description: `Qty: ${qty}`,
                totalPrice: discountedLineTotal,           // ήδη με έκπτωση
                discountText:
                    safe(discountPercent) > 0
                        ? `${discountPercent}% off`
                        : '',
                taxText: '',
            };
        })
        : [];

    const [unpaidItems, setUnpaidItems] = useState(initialUnpaid);
    const [paidItems, setPaidItems] = useState([]);
    const [payerName, setPayerName] = useState('');
    const [tip, setTip] = useState(0);
    const [method, setMethod] = useState('CASH');
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');

    if (!unpaidSource.length) {
        return <div>Nothing left to pay for this order.</div>;
    }

    const moveItem = (item, from, toSetter, fromSetter) => {
        fromSetter(from.filter((x) => x.orderItemId !== item.orderItemId));
        toSetter((prev) => [...prev, item]);
    };

    const paidAmount = paidItems.reduce(
        (s, it) => s + safe(it.totalPrice),
        0
    );
    const totalThisPayment = paidAmount + safe(tip);

    const handlePay = async () => {
        try {
            setSaving(true);
            setError('');

            const token = localStorage.getItem('authToken');
            if (!token) {
                setError('No auth token');
                return;
            }

            const payload = {
                businessId,
                orderId: Number(orderId),
                totalAmount: totalThisPayment,        // ΠΡΟΪΟΝΤΑ (με έκπτωση) + TIP
                totalTip: safe(tip),
                paymentMethod: method,
                split: mode === 'SPLIT',
                splits: [
                    {
                        payerName,
                        amount: totalThisPayment,
                        tipAmount: safe(tip),
                        paymentMethod: method,
                        items: paidItems.map((it) => ({
                            orderItemId: it.orderItemId,
                            amount: safe(it.totalPrice), // ήδη discounted
                        })),
                    },
                ],
                discountPercent,
                discountAmount,
            };

            const res = await fetch('http://localhost:8080/api/payments', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: 'Bearer ' + token,
                },
                body: JSON.stringify(payload),
            });

            if (!res.ok) throw new Error('Payment failed');

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
        } catch (err) {
            console.error(err);
            setError('Something went wrong while processing payment.');
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className="pay-page">
            <header className="pay-header">
                <h1>Select Items to Pay</h1>
                <div className="pay-subtitle">Cart</div>
            </header>

            <section className="split-section">
                <div className="split-block">
                    <div className="split-title">Unpaid items</div>
                    {unpaidItems.map((it) => (
                        <div
                            key={it.orderItemId}
                            className="split-line"
                            onClick={() =>
                                moveItem(it, unpaidItems, setPaidItems, setUnpaidItems)
                            }
                        >
                            <div className="qty-box blue">{it.quantity}</div>
                            <div className="split-main">
                                <div className="split-name">{it.productName}</div>
                                <div className="split-desc">{it.description}</div>
                            </div>
                            <div className="split-center">Line Item</div>
                            <div className="split-right">
                                <div className="split-price">
                                    {safe(it.totalPrice).toFixed(2)} €
                                </div>
                                <div className="split-meta">
                                    Discount: {it.discountText}
                                </div>
                                <div className="split-meta">Tax: {it.taxText}</div>
                            </div>
                        </div>
                    ))}
                </div>

                <div className="split-block">
                    <div className="split-title">Paid items</div>
                    {paidItems.map((it) => (
                        <div
                            key={it.orderItemId}
                            className="split-line"
                            onClick={() =>
                                moveItem(it, paidItems, setUnpaidItems, setPaidItems)
                            }
                        >
                            <div className="qty-box green">{it.quantity}</div>
                            <div className="split-main">
                                <div className="split-name">{it.productName}</div>
                                <div className="split-desc">{it.description}</div>
                            </div>
                            <div className="split-center">Line Item</div>
                            <div className="split-right">
                                <div className="split-price">
                                    {safe(it.totalPrice).toFixed(2)} €
                                </div>
                                <div className="split-meta">
                                    Discount: {it.discountText}
                                </div>
                                <div className="split-meta">Tax: {it.taxText}</div>
                            </div>
                        </div>
                    ))}
                </div>
            </section>

            <section className="split-footer">
                <div className="payer-row">
                    <span>Items paid by:</span>
                    <input
                        type="text"
                        placeholder="Your Name"
                        value={payerName}
                        onChange={(e) => setPayerName(e.target.value)}
                    />
                </div>

                <div className="tip-row">
                    <span>Add tip:</span>
                    <input
                        type="number"
                        min="0"
                        step="0.01"
                        value={tip}
                        onChange={(e) => setTip(e.target.value)}
                    />
                </div>

                <div className="method-row">
                    <span>Payment method:</span>
                    <select
                        value={method}
                        onChange={(e) => setMethod(e.target.value)}
                    >
                        <option value="CASH">Cash</option>
                        <option value="CARD">Card</option>
                        <option value="GIFT_CARD">Gift card</option>
                    </select>
                </div>

                {error && <div className="pay-error">{error}</div>}

                <button
                    className="btn-dark full-width"
                    disabled={saving || paidItems.length === 0}
                    onClick={handlePay}
                >
                    {saving
                        ? 'Processing...'
                        : `Pay ${safe(totalThisPayment).toFixed(2)} €`}
                </button>
            </section>
        </div>
    );
};

export default PaymentSplit;
