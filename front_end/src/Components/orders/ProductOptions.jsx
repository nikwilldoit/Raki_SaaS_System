// src/Components/orders/ProductOptions.jsx
import React, { useState, useEffect, useMemo } from 'react';
import './ProductOptions.css';

const ProductOptions = ({ productId, onAddToCart, onCancel }) => {


    function applyDiscount(basePrice, discount) {
        if (!discount) return basePrice;

        if (discount.discountType === 'PERCENT') {
            return basePrice - (basePrice * (discount.discountValue  / 100));
        }

        if (discount.discountType  === 'AMOUNT') {
            return Math.max(0, basePrice - discount.discountValue );
        }

        return basePrice;
    }

    const [product, setProduct] = useState(null);

    // δυναμικές κατηγορίες από backend
    const [categories, setCategories] = useState([]); // [{categoryId, categoryName, singleSelect, options: [{ingredientId,name,priceDelta}]}]
    const [selected, setSelected] = useState({});     // { [categoryId]: [ingredientId,...] }

    const [specialRequest, setSpecialRequest] = useState('');
    const [quantity, setQuantity] = useState(1);

    useEffect(() => {
        const token = localStorage.getItem('authToken');
        if (!productId || !token) return;

        // 1) Βασικό product
        fetch(`http://localhost:8080/api/products/${productId}`, {
            headers: { Authorization: 'Bearer ' + token },
        })
            .then((res) => {
                if (!res.ok) throw new Error('Failed to load product ' + res.status);
                return res.json();
            })
            .then((data) => {
                //here I apply the discount
                const finalPrice = applyDiscount(Number(data.basePrice), data.discount);

                setProduct({
                    id: data.productId,
                    name: data.name,
                    description: data.description,
                    rawBasePrice: Number(data.basePrice),
                    basePrice: finalPrice,
                    discount: data.discount
                });
            })
            .catch((err) => console.error('Failed to load product', err));

        // 2) Categories + options για το συγκεκριμένο product
        fetch(`http://localhost:8080/api/products/${productId}/extras`, {
            headers: { Authorization: 'Bearer ' + token },
        })
            .then((res) => {
                if (!res.ok) throw new Error('Failed to load extras');
                return res.json();
            })
            .then((list) => {
                if (!Array.isArray(list)) return;
                setCategories(list);
                const initial = {};
                list.forEach((c) => {
                    initial[c.categoryId] = [];
                });
                setSelected(initial);
            })
            .catch((err) => {
                console.error('Failed to load extras', err);
                setCategories([]);
                setSelected({});
            });
    }, [productId]);

    const toggleOption = (category, optionId) => {
        setSelected((prev) => {
            const current = prev[category.categoryId] || [];
            let next;
            if (category.singleSelect) {
                next = current.includes(optionId) ? [] : [optionId];
            } else {
                next = current.includes(optionId)
                    ? current.filter((id) => id !== optionId)
                    : [...current, optionId];
            }
            return { ...prev, [category.categoryId]: next };
        });
    };

    const extrasTotal = useMemo(() => {
        return categories.reduce((sum, cat) => {
            const chosenIds = selected[cat.categoryId] || [];
            const catSum = chosenIds.reduce((s, id) => {
                const opt = cat.options.find((o) => o.ingredientId === id);
                return opt ? s + Number(opt.priceDelta ?? 0) : s;
            }, 0);
            return sum + catSum;
        }, 0);
    }, [categories, selected]);

    const unitPrice = useMemo(() => {
        if (!product) return 0;
        return Number(product.basePrice + extrasTotal).toFixed(2);
    }, [product, extrasTotal]);

    const totalPrice = useMemo(
        () => Number(unitPrice * quantity).toFixed(2),
        [unitPrice, quantity]
    );

    const handleAddToCart = () => {
        if (!product || !onAddToCart) return;

        const selections = categories.map((cat) => ({
            categoryId: cat.categoryId,
            categoryName: cat.categoryName,
            options: cat.options.filter((o) =>
                (selected[cat.categoryId] || []).includes(o.ingredientId)
            ),
        }));

        onAddToCart({
            productId: product.id,
            name: product.name,
            quantity,
            unitPrice: Number(unitPrice),
            totalPrice: Number(totalPrice),
            selections,
            specialRequest: specialRequest.trim() || null,
        });
    };

    const handleQuantityChange = (delta) => {
        setQuantity((prev) => {
            const next = prev + delta;
            return next < 1 ? 1 : next;
        });
    };

    if (!product) return null;

    return (
        <div className="product-options-overlay">
            <div className="product-options-modal">
                <div className="product-options-header">
                    <div>
                        <h2>{product.name}</h2>
                        <p className="product-options-description">
                            {product.description}
                        </p>
                    </div>
                    <button
                        type="button"
                        className="product-options-close"
                        onClick={onCancel}
                    >
                        ✕
                    </button>
                </div>

                <div className="product-options-body">
                    {categories.map((cat) => (
                        <section
                            key={cat.categoryId}
                            className="product-options-section"
                        >
                            <h3>{cat.categoryName}</h3>
                            <div
                                className={
                                    cat.singleSelect ? 'option-chips' : 'option-list'
                                }
                            >
                                {cat.options.map((opt) => {
                                    const active = (selected[cat.categoryId] || []).includes(
                                        opt.ingredientId
                                    );
                                    return (
                                        <button
                                            key={opt.ingredientId}
                                            type="button"
                                            className={
                                                cat.singleSelect
                                                    ? `option-chip ${
                                                        active ? 'option-chip--active' : ''
                                                    }`
                                                    : `option-row ${
                                                        active ? 'option-row--active' : ''
                                                    }`
                                            }
                                            onClick={() => toggleOption(cat, opt.ingredientId)}
                                        >
                                            <div
                                                className={
                                                    cat.singleSelect
                                                        ? undefined
                                                        : 'option-row-main'
                                                }
                                            >
                                                <span>{opt.name}</span>
                                                {Number(opt.priceDelta || 0) !== 0 && (
                                                    <span
                                                        className={
                                                            cat.singleSelect
                                                                ? 'option-chip-price'
                                                                : 'option-row-price'
                                                        }
                                                    >
                            +{Number(opt.priceDelta).toFixed(2)}€
                          </span>
                                                )}
                                            </div>
                                            {!cat.singleSelect && (
                                                <div className="option-row-check">
                                                    {active ? '✓' : ''}
                                                </div>
                                            )}
                                        </button>
                                    );
                                })}
                                {cat.options.length === 0 && (
                                    <span className="product-options-description">
                    No options available for this category.
                  </span>
                                )}
                            </div>
                        </section>
                    ))}

                    <section className="product-options-section">
                        <h3>Notes</h3>
                        <textarea
                            className="product-options-notes"
                            placeholder="E.g. less foam, lactose-free milk..."
                            value={specialRequest}
                            onChange={(e) => setSpecialRequest(e.target.value)}
                        />
                    </section>
                </div>

                <div className="product-options-footer">
                    <div className="product-options-quantity">
                        <button
                            type="button"
                            onClick={() => handleQuantityChange(-1)}
                            disabled={quantity <= 1}
                        >
                            −
                        </button>
                        <span>{quantity}</span>
                        <button type="button" onClick={() => handleQuantityChange(1)}>
                            +
                        </button>
                    </div>
                    <div className="product-options-summary">
                        <span className="summary-label">Total</span>
                        <span className="summary-price">{totalPrice}€</span>
                    </div>
                    <button
                        type="button"
                        className="product-options-add-btn"
                        onClick={handleAddToCart}
                    >
                        Add to order
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ProductOptions;
