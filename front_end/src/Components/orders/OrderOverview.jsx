// src/Components/orders/OrderOverview.jsx
import React, { useState, useEffect } from 'react';
import './OrderOverview.css';
import ProductOptions from './ProductOptions';
import Header from '../Header/Header';

import { useNavigate } from 'react-router-dom';

const OrderOverview = ({ userData, onLogout }) => {
    const navigate = useNavigate();
    const [categories, setCategories] = useState([]);
    const [productsByCategory, setProductsByCategory] = useState({});
    const [activeCategory, setActiveCategory] = useState(null);

    const [orderItems, setOrderItems] = useState([]);
    const [specialRequests, setSpecialRequests] = useState('');
    //const [discount, setDiscount] = useState(0);
    const [discountPercent, setDiscountPercent] = useState(0);



    const [optionsProduct, setOptionsProduct] = useState(null); // {id, name, price}

    const businessId = userData?.businessId;

    // Φέρε products από backend
    useEffect(() => {
        if (!businessId) return;

        const token = localStorage.getItem('authToken');
        if (!token) return;

        fetch(`http://localhost:8080/api/products?businessId=${businessId}`, {
            headers: {
                Authorization: 'Bearer ' + token,
            },
        })
            .then((res) => {
                if (!res.ok) {
                    throw new Error('Failed to load products: ' + res.status);
                }
                return res.json();
            })
            .then((data) => {
                if (!Array.isArray(data)) {
                    console.error('Products response is not array', data);
                    return;
                }
                const grouped = { All: [] };
                data.forEach((p) => {
                    grouped.All.push({
                        id: p.productId,
                        name: p.name,
                        price: Number(p.basePrice ?? 0),
                    });
                });
                setProductsByCategory(grouped);
                setCategories(Object.keys(grouped));
                setActiveCategory('All');
            })
            .catch((err) => {
                console.error('Failed to load products', err);
            });
    }, [businessId]);

    const handleOpenOptions = (product) => {
        setOptionsProduct(product);
    };

    const handleCloseOptions = () => {
        setOptionsProduct(null);
    };

    const handleAddFromOptions = (item) => {
        setOrderItems((prev) => [
            ...prev,
            {
                id: Date.now(), // local row id
                productId: item.productId,
                name: item.name,
                size: item.size,
                extras: item.extras,
                specialRequest: item.specialRequest,
                quantity: item.quantity,
                unitPrice: item.unitPrice,
            },
        ]);
        setOptionsProduct(null);
    };

    const updateQuantity = (rowId, delta) => {
        setOrderItems((prev) =>
            prev
                .map((item) =>
                    item.id === rowId
                        ? { ...item, quantity: item.quantity + delta }
                        : item
                )
                .filter((item) => item.quantity > 0)
        );
    };

    const subtotal = orderItems.reduce(
        (sum, item) => sum + item.unitPrice * item.quantity,
        0
    );

    const discountAmount = Number(((subtotal * discountPercent) / 100).toFixed(2));
    const total = subtotal - discountAmount;

    const handleDiscountPercentChange = (e) => {
        const value = e.target.value;
        if (value === '') {
            setDiscountPercent(0);
            return;
        }
        const num = Number(value.replace(',', '.'));
        if (Number.isNaN(num) || num < 0) {
            setDiscountPercent(0);
            return;
        }
        if (num > 100) {
            setDiscountPercent(100);
            return;
        }
        setDiscountPercent(num);
    };

    const createOrder = async () => {
        console.log('businessId = ', businessId, 'userId = ', userData?.userId);

        if (orderItems.length === 0) {
            alert('Order is empty');
            return null;
        }

        const token = localStorage.getItem('authToken');
        if (!token) {
            alert('No auth token. Please login again.');
            return null;
        }

        if (!businessId || !userData?.userId) {
            alert('Missing business or user id');
            return null;
        }

        const payload = {
            businessId,
            userId: userData.userId,
            orderNumber: null,
            specialRequests,
            items: orderItems.map((item) => {
                const quantity = Number(item.quantity) || 1;
                const unitPrice = Number(item.unitPrice) || 0;

                return {
                    productId: item.productId,
                    quantity,
                    unitPrice,
                    totalPrice: unitPrice * quantity,
                };
            }),
        };

        try {
            const res = await fetch('http://localhost:8080/api/orders', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: 'Bearer ' + token,
                },
                body: JSON.stringify(payload),
            });

            console.log('create order status', res.status);

            if (!res.ok) {
                const txt = await res.text();
                console.error('Order create failed', res.status, txt);
                alert('Failed to create order: ' + res.status);
                return null;
            }

            const body = await res.json(); // { order, items }
            const orderId = body.order?.id;
            const backendItems = body.items || [];

            if (!orderId) {
                console.error('Missing order id in response', body);
                alert('Missing order id from backend');
                return null;
            }

            return { orderId, backendItems };
        } catch (e) {
            console.error('Order create error', e);
            alert('Network error while creating order');
            return null;
        }
    };



    const resetLocalState = () => {
        setOrderItems([]);
        setSpecialRequests('');
        setDiscountPercent(0);
    };


    const handleProceedToPayment = async () => {
        const result = await createOrder();
        if (!result) return;

        const { orderId, backendItems } = result;

        const currentSubtotal = subtotal;
        const currentDiscountPercent = discountPercent;
        const currentDiscountAmount = discountAmount;
        const currentTotal = total;

        resetLocalState();

        navigate(`/orders/${orderId}/payment`, {
            state: {
                orderId,
                businessId,
                subtotal: currentSubtotal,
                discountPercent: currentDiscountPercent,
                discountAmount: currentDiscountAmount,
                total: currentTotal,
                // backendItems: έχουν πραγματικά orderItemId από DB
                items: backendItems,
            },
        });
    };


    const handleSubmitOrder = async () => {
        const result = await createOrder();
        if (!result) return;

        const { orderId } = result;
        alert('Order created successfully: #' + orderId);
        resetLocalState();
    };

    const handleCancelOrder = () => {
        setOrderItems([]);
        setSpecialRequests('');
        setDiscountPercent(0);
    };

    const productsInCategory = activeCategory
        ? productsByCategory[activeCategory] || []
        : [];

    return (
        <div className="orders-page">
            <Header userData={userData} onLogout={onLogout} />

            <div className="order-overview">
                {/* Αριστερά: NEW ORDER */}
                <div className="order-overview-left">
                    <div className="order-overview-header">
                        <h2>New order</h2>
                        <span>Select items to add</span>
                    </div>

                    <div className="order-categories">
                        {categories.map((cat) => (
                            <button
                                key={cat}
                                type="button"
                                className={
                                    'order-category-tab' +
                                    (activeCategory === cat ? ' order-category-tab--active' : '')
                                }
                                onClick={() => setActiveCategory(cat)}
                            >
                                {cat}
                            </button>
                        ))}
                    </div>

                    <div className="order-products">
                        {productsInCategory.map((p) => (
                            <button
                                key={p.id}
                                type="button"
                                className="order-product-card"
                                onClick={() => handleOpenOptions(p)}
                            >
                                <span className="order-product-name">{p.name}</span>
                                <span className="order-product-price">
                  {p.price.toFixed(2)}€
                </span>
                            </button>
                        ))}
                        {productsInCategory.length === 0 && (
                            <div className="order-empty-state">
                                No products in this category yet.
                            </div>
                        )}
                    </div>
                </div>

                {/* Δεξιά: ORDER SUMMARY */}
                <div className="order-overview-right">
                    <div className="order-summary-header">
                        <h3>Order summary</h3>
                        <span>{orderItems.length} items</span>
                    </div>

                    <div className="order-items">
                        {orderItems.length === 0 && (
                            <div className="order-empty-state">
                                The order is empty. Select a product on the left to start.
                            </div>
                        )}

                        {orderItems.map((item) => (
                            <div key={item.id} className="order-item">
                                <div className="order-item-main">
                                    <span className="order-item-title">{item.name}</span>
                                    <span className="order-item-meta">
                    {item.size && `${item.size} · `}
                                        {item.unitPrice.toFixed(2)}€ per item
                  </span>
                                    {item.extras && item.extras.length > 0 && (
                                        <span className="order-item-meta">
                      Extras: {item.extras.map((e) => e.name).join(', ')}
                    </span>
                                    )}
                                    {item.specialRequest && (
                                        <span className="order-item-meta">
                      Note: {item.specialRequest}
                    </span>
                                    )}
                                </div>
                                <div className="order-item-total">
                                    {(item.unitPrice * item.quantity).toFixed(2)}€
                                </div>
                                <div className="order-item-qty-controls">
                                    <button onClick={() => updateQuantity(item.id, -1)}>-</button>
                                    <span>{item.quantity}</span>
                                    <button onClick={() => updateQuantity(item.id, 1)}>+</button>
                                </div>
                            </div>
                        ))}
                    </div>

                    <div className="order-extra-controls">
            <textarea
                className="order-special-requests"
                placeholder="Overall notes for this order (e.g. table number, allergies)..."
                value={specialRequests}
                onChange={(e) => setSpecialRequests(e.target.value)}
            />
                        <div className="order-discount-row">
<span>
  Discount:{' '}
    {discountPercent > 0
        ? `- ${discountAmount.toFixed(2)}€ (${discountPercent}%)`
        : ''}
</span>

                            <div className="order-discount-input">
                                <input
                                    type="number"
                                    min="0"
                                    max="100"
                                    step="0.1"
                                    value={discountPercent}
                                    onChange={handleDiscountPercentChange}
                                    placeholder="Discount %"
                                />
                                <span>%</span>
                            </div>
                        </div>

                    </div>

                    <div className="order-totals">
                        <div className="order-totals-row">
                            <span className="label">Subtotal</span>
                            <span className="value">{subtotal.toFixed(2)}€</span>
                        </div>
                        <div className="order-totals-row">
                            <span className="label">Discount</span>
                            <span className="value">- {discountAmount.toFixed(2)}€</span>
                        </div>
                        <div className="order-totals-row">
                            <span className="label">Total</span>
                            <span className="value">{total.toFixed(2)}€</span>
                        </div>
                    </div>

                    <div className="order-actions">
                        <button
                            type="button"
                            className="order-cancel"
                            onClick={handleCancelOrder}
                        >
                            Cancel
                        </button>
                        <button
                            type="button"
                            className="order-proceed"
                            onClick={handleProceedToPayment}
                        >
                            Proceed to payment
                        </button>
                        <button
                            type="button"
                            className="order-submit"
                            onClick={handleSubmitOrder}
                        >
                            Submit Order
                        </button>
                    </div>
                </div>
            </div>

            {optionsProduct && (
                <ProductOptions
                    productId={optionsProduct.id}
                    onAddToCart={handleAddFromOptions}
                    onCancel={handleCloseOptions}
                    userData={userData}
                />
            )}
        </div>
    );
};

export default OrderOverview;
