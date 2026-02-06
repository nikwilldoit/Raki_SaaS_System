// src/Components/payment/PaymentOverview.jsx
import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import './Payment.css';

const PaymentOverview = () => {
    const { orderId } = useParams();
    const [order, setOrder] = useState(null);
    const [payments, setPayments] = useState([]);

    useEffect(() => {
        const loadData = async () => {
            try {
                const token = localStorage.getItem('authToken');

                // Φέρε order + items
                const orderRes = await fetch(
                    `http://localhost:8080/api/orders/${orderId}`,
                    { headers: { Authorization: 'Bearer ' + token } }
                );
                if (orderRes.ok) {
                    const dto = await orderRes.json(); // {order, items}
                    setOrder(dto);
                }

                // Φέρε payments
                const payRes = await fetch(
                    `http://localhost:8080/api/orders/${orderId}/payments`,
                    { headers: { Authorization: 'Bearer ' + token } }
                );
                if (payRes.ok) {
                    const data = await payRes.json();
                    setPayments(data);
                }
            } catch (e) {
                console.error(e);
            }
        };

        loadData();
    }, [orderId]);

    const totalAmount = order
        ? order.items.reduce((s, it) => s + (it.totalPrice || 0), 0)
        : 0;

    const alreadyPaid = payments.reduce(
        (s, p) => s + p.totalAmount,
        0
    );

    const leftToPay = totalAmount - alreadyPaid;

    return (
        <div className="pay-page">
            <header className="pay-header">
                <h1>Payments Overview</h1>
                <div className="pay-subtitle">
                    Order Number: {order ? order.order.orderNumber : orderId}
                </div>
            </header>

            <section className="pay-bill">
                <div className="pay-row">
                    <span>Your bill</span>
                    <span>{totalAmount.toFixed(2)} €</span>
                </div>
            </section>

            <section className="pay-history">
                {payments.map((p, idx) => (
                    <div key={p.paymentId} className="pay-history-line">
                        <div>Payment #{idx + 1}</div>
                        <div>By: {p.payerSummary}</div>
                        <div>Products: {p.productSummary}</div>
                        <div>
                            {p.totalAmount.toFixed(2)} € + Tip {p.totalTip.toFixed(2)} €
                        </div>
                    </div>
                ))}
            </section>

            <section className="pay-bill">
                <div className="pay-row">
                    <span>Left to pay</span>
                    <span>{leftToPay.toFixed(2)} €</span>
                </div>
            </section>
        </div>
    );
};

export default PaymentOverview;
