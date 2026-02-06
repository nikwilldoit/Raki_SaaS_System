import React, { useEffect, useState } from 'react';
import './DiscountForm.css';

const DiscountForm = ({ userData, discount, onClose }) => {
    const isEdit = !!discount?.id;

    const [name, setName] = useState(discount?.name || '');
    const [discountType, setDiscountType] = useState(discount?.discountType || 'PERCENT');
    const [value, setValue] = useState(discount?.discountValue || '');
    const [startDate, setStartDate] = useState(discount?.startDate || '');
    const [endDate, setEndDate] = useState(discount?.endDate || '');
    const [noExpiration, setNoExpiration] = useState(!discount?.endDate);
    const [isActive, setIsActive] = useState(discount ? discount.active : true);

    const [scope, setScope] = useState(discount?.scope || 'PRODUCT');

    const [products, setProducts] = useState([]);
    const [selectedProducts, setSelectedProducts] = useState([]);

    const [services, setServices] = useState([]);
    const [selectedServices, setSelectedServices] = useState([]);

    const [loading, setLoading] = useState(false);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');

    // Φόρτωση products + services
    useEffect(() => {
        const fetchData = async () => {
            try {
                setLoading(true);
                const token = localStorage.getItem('authToken');

                const requests = [
                    fetch(
                        `http://localhost:8080/api/products?businessId=${userData.businessId}`,
                        { headers: { Authorization: 'Bearer ' + token } },
                    ),
                ];

                // services μόνο αν έχει νόημα
                if (userData?.businessType === 'HAIRDRESSER') {
                    requests.push(
                        fetch(
                            `http://localhost:8080/api/services?businessId=${userData.businessId}`,
                            { headers: { Authorization: 'Bearer ' + token } },
                        ),
                    );
                }

                const responses = await Promise.all(requests);
                const prodData = await responses[0].json();
                setProducts(prodData);

                if (responses[1]) {
                    const servData = await responses[1].json();
                    setServices(servData);
                }
            } catch (err) {
                console.error('Fetch products/services error:', err);
                setError('Σφάλμα κατά τη φόρτωση προϊόντων/υπηρεσιών.');
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [userData.businessId, userData?.businessType]);

    const toggleProduct = (id) => {
        setSelectedProducts((prev) =>
            prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id],
        );
    };

    const toggleService = (id) => {
        setSelectedServices((prev) =>
            prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id],
        );
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSaving(true);
        setError('');

        try {
            const token = localStorage.getItem('authToken');

            const payload = {
                businessId: userData.businessId,
                name,
                scope, // PRODUCT / SERVICE / BOTH
                discountType,
                discountValue: Math.max(0, Number(value) || 0),
                startDate: startDate || null,
                endDate: noExpiration ? null : endDate || null,
                isActive,
                productIds: selectedProducts,
                serviceIds: selectedServices,
            };

            const url = isEdit
                ? `http://localhost:8080/api/discounts/${discount.id}`
                : 'http://localhost:8080/api/discounts';

            const method = isEdit ? 'PUT' : 'POST';

            const res = await fetch(url, {
                method,
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
            console.error('Save discount error:', err);
            setError('Δεν ήταν δυνατή η αποθήκευση της έκπτωσης.');
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className="discount-form-backdrop">
            <div className="discount-form-container">
                <div className="form-header">
                    <h2>{isEdit ? 'Edit Discount' : 'Create New Discount'}</h2>
                    <button className="icon-btn" type="button" onClick={() => onClose(false)}>
                        ✕
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="discount-form">
                    <div className="form-row">
                        <label>Discount Name</label>
                        <input
                            type="text"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required
                            placeholder="Enter discount name"
                        />
                    </div>

                    <div className="form-grid">
                        {/* Αριστερή στήλη */}
                        <div className="left-column">
                            <div className="form-row">
                                <label>Applies to</label>
                                <select value={scope} onChange={(e) => setScope(e.target.value)}>
                                    <option value="PRODUCT">Products</option>
                                    {userData?.businessType === 'HAIRDRESSER' && (
                                        <>
                                            <option value="SERVICE">Services</option>
                                            <option value="BOTH">Products & Services</option>
                                        </>
                                    )}
                                </select>
                            </div>

                            <div className="form-row">
                                <label>Value</label>
                                <div className="value-toggle">
                                    <button
                                        type="button"
                                        className={discountType === 'AMOUNT' ? 'active' : ''}
                                        onClick={() => setDiscountType('AMOUNT')}
                                    >
                                        Amount
                                    </button>
                                    <button
                                        type="button"
                                        className={discountType === 'PERCENT' ? 'active' : ''}
                                        onClick={() => setDiscountType('PERCENT')}
                                    >
                                        Percent
                                    </button>
                                </div>
                                <input
                                    type="number"
                                    min="0"
                                    step="0.01"
                                    value={value}
                                    onChange={(e) => {
                                        const v = Math.max(0, Number(e.target.value));
                                        setValue(v.toString());
                                    }}
                                    required
                                    placeholder={
                                        discountType === 'PERCENT' ? 'e.g. 10 for 10%' : 'e.g. 5.00'
                                    }
                                />
                            </div>
                        </div>

                        {/* Δεξιά στήλη */}
                        <div className="right-column">
                            {(scope === 'PRODUCT' || scope === 'BOTH') && (
                                <div className="form-row">
                                    <label>Products</label>
                                    <div className="products-box">
                                        <div className="products-header">
                                            <span>Select products to apply discount</span>
                                            {loading && <span style={{ fontSize: 12 }}>Loading...</span>}
                                        </div>
                                        <div className="products-list">
                                            {products.map((p) => (
                                                <label key={p.productId || p.id} className="product-item">
                                                    <input
                                                        type="checkbox"
                                                        checked={selectedProducts.includes(p.productId || p.id)}
                                                        onChange={() => toggleProduct(p.productId || p.id)}
                                                    />
                                                    <span>{p.name}</span>
                                                </label>
                                            ))}
                                            {!loading && products.length === 0 && (
                                                <div style={{ fontSize: 12, color: '#9ca3af' }}>
                                                    Δεν βρέθηκαν προϊόντα.
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            )}

                            {userData?.businessType === 'HAIRDRESSER' &&
                                (scope === 'SERVICE' || scope === 'BOTH') && (
                                    <div className="form-row">
                                        <label>Services</label>
                                        <div className="products-box">
                                            <div className="products-header">
                                                <span>Select services to apply discount</span>
                                                {loading && <span style={{ fontSize: 12 }}>Loading...</span>}
                                            </div>
                                            <div className="products-list">
                                                {services.map((s) => (
                                                    <label key={s.id} className="product-item">
                                                        <input
                                                            type="checkbox"
                                                            checked={selectedServices.includes(s.id)}
                                                            onChange={() => toggleService(s.id)}
                                                        />
                                                        <span>{s.name}</span>
                                                    </label>
                                                ))}
                                                {!loading && services.length === 0 && (
                                                    <div style={{ fontSize: 12, color: '#9ca3af' }}>
                                                        Δεν βρέθηκαν υπηρεσίες.
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    </div>
                                )}
                        </div>
                    </div>

                    {/* Time period + status */}
                    <div className="form-grid">
                        <div className="left-column">
                            <div className="inline-row">
                                <div className="form-row">
                                    <label>Start Date</label>
                                    <input
                                        type="date"
                                        value={startDate}
                                        onChange={(e) => setStartDate(e.target.value)}
                                    />
                                </div>
                                <div className="form-row">
                                    <label>End Date</label>
                                    <input
                                        type="date"
                                        value={noExpiration ? '' : endDate}
                                        onChange={(e) => setEndDate(e.target.value)}
                                        disabled={noExpiration}
                                    />
                                </div>
                            </div>
                            <label className="checkbox-label">
                                <input
                                    type="checkbox"
                                    checked={noExpiration}
                                    onChange={(e) => setNoExpiration(e.target.checked)}
                                />
                                No expiration date
                            </label>
                        </div>

                        <div className="right-column">
                            <div className="form-row">
                                <label>Status</label>
                                <select
                                    value={isActive ? 'ACTIVE' : 'INACTIVE'}
                                    onChange={(e) => setIsActive(e.target.value === 'ACTIVE')}
                                >
                                    <option value="ACTIVE">Active</option>
                                    <option value="INACTIVE">Inactive</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    {error && <div className="form-error">{error}</div>}

                    <div className="form-footer">
                        <button
                            type="button"
                            className="btn-secondary"
                            onClick={() => onClose(false)}
                            disabled={saving}
                        >
                            Cancel
                        </button>
                        <button type="submit" className="btn-primary" disabled={saving}>
                            {saving ? 'Saving...' : 'Save Discount'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default DiscountForm;
