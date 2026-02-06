// src/Components/manage/ManageProducts.jsx
import React, { useState, useEffect } from 'react';
import './ManageProducts.css';
import Header from '../Header/Header';

const emptyProduct = {
    id: null,
    name: '',
    description: '',
    taxCategoryId: '',
    productTypeId: '',
    ingredientCategoryId: '',
    // Κρατάμε πλήρη objects: { ingredientId, ingredientName, categoryId }
    ingredients: [],
    price: '',
    status: 'ACTIVE',
};

const ManageProducts = ({ userData, onLogout }) => {
    const [products, setProducts] = useState([]);
    const [filterText, setFilterText] = useState('');
    const [filterCategory, setFilterCategory] = useState('ALL');
    const [filterStatus, setFilterStatus] = useState('ALL');
    const [sort, setSort] = useState('NAME_ASC');

    const [modalOpen, setModalOpen] = useState(false);
    const [deleteModalOpen, setDeleteModalOpen] = useState(false);
    const [editingProduct, setEditingProduct] = useState(emptyProduct);
    const [productToDelete, setProductToDelete] = useState(null);

    // dropdown data
    const [taxCategories, setTaxCategories] = useState([]);
    const [productTypes, setProductTypes] = useState([]);
    const [ingredientCategories, setIngredientCategories] = useState([]);
    const [ingredientsForSelectedCategory, setIngredientsForSelectedCategory] =
        useState([]);

    const businessId = userData?.businessId;

    useEffect(() => {
        if (!businessId) return;
        const token = localStorage.getItem('authToken');
        if (!token) return;

        // 1) Products
        fetch(`http://localhost:8080/api/products?businessId=${businessId}`, {
            headers: { Authorization: 'Bearer ' + token },
        })
            .then((r) => r.json())
            .then((data) => {
                setProducts(
                    data.map((p) => ({
                        id: p.productId,
                        name: p.name,
                        category: p.productTypeName || 'Uncategorized',
                        description: p.description,
                        price: Number(p.basePrice),
                        status: p.status,
                        createdAt: p.createdAt,
                        updatedAt: p.updatedAt,
                        taxCategoryId: p.taxCategory,
                        productTypeId: p.productType,
                    })),
                );
            })
            .catch((err) => console.error('Failed to load products', err));

        // 2) Tax categories
        fetch('http://localhost:8080/api/taxes', {
            headers: { Authorization: 'Bearer ' + token },
        })
            .then((r) => r.json())
            .then((data) => {
                const list = Array.isArray(data) ? data : [];
                setTaxCategories(list);
            })
            .catch(() => setTaxCategories([]));

        // 3) Product types
        fetch('http://localhost:8080/api/product-types', {
            headers: { Authorization: 'Bearer ' + token },
        })
            .then((r) => r.json())
            .then((data) => {
                const list = Array.isArray(data) ? data : [];
                setProductTypes(list);
            })
            .catch(() => setProductTypes([]));

        // 4) Ingredient categories
        fetch(
            `http://localhost:8080/api/menu/ingredient-categories?businessId=${businessId}`,
            { headers: { Authorization: 'Bearer ' + token } },
        )
            .then((r) => r.json())
            .then((data) =>
                setIngredientCategories(
                    data.map((c) => ({ id: c.categoryId, name: c.name })),
                ),
            )
            .catch(() => {});
    }, [businessId]);

    const openAddModal = () => {
        setEditingProduct({
            ...emptyProduct,
            status: 'ACTIVE',
            ingredients: [],
        });
        setIngredientsForSelectedCategory([]);
        setModalOpen(true);
    };

    const openEditModal = (product) => {
        // Αν θες να φορτώνεις ήδη αποθηκευμένα ingredients, εδώ θα κάνεις fetch
        setEditingProduct({
            id: product.id,
            name: product.name,
            description: product.description || '',
            taxCategoryId: product.taxCategoryId || '',
            productTypeId: product.productTypeId || '',
            ingredientCategoryId: '',
            ingredients: [],
            price: product.price,
            status: product.status,
        });
        setIngredientsForSelectedCategory([]);
        setModalOpen(true);
    };

    const closeModal = () => {
        setModalOpen(false);
        setEditingProduct(emptyProduct);
    };

    const handleProductFieldChange = (e) => {
        const { name, value } = e.target;

        let finalValue = value;
        if (name === 'price') {
            finalValue = Math.max(0, Number(value) || 0).toString();
        }

        setEditingProduct((prev) => ({
            ...prev,
            [name]: finalValue,
        }));
    };


    const handleIngredientCategoryChange = (e) => {
        const id = e.target.value;

        setEditingProduct((prev) => ({
            ...prev,
            ingredientCategoryId: id,
        }));

        const token = localStorage.getItem('authToken');
        if (!id || !token) {
            setIngredientsForSelectedCategory([]);
            return;
        }

        fetch(
            `http://localhost:8080/api/menu/ingredient-categories/${id}/ingredients`,
            { headers: { Authorization: 'Bearer ' + token } },
        )
            .then((r) => r.json())
            .then((data) => {
                setIngredientsForSelectedCategory(
                    data.map((ing) => ({
                        id: ing.ingredientId,
                        name: ing.name,
                    })),
                );
            })
            .catch(() => setIngredientsForSelectedCategory([]));
    };

    // toggle με πλήρες object, για να ξέρουμε categoryId & name
    const toggleIngredientSelection = (ingredient) => {
        setEditingProduct((prev) => {
            const exists = prev.ingredients.some(
                (it) => it.ingredientId === ingredient.id,
            );
            if (exists) {
                return {
                    ...prev,
                    ingredients: prev.ingredients.filter(
                        (it) => it.ingredientId !== ingredient.id,
                    ),
                };
            }
            return {
                ...prev,
                ingredients: [
                    ...prev.ingredients,
                    {
                        ingredientId: ingredient.id,
                        ingredientName: ingredient.name,
                        categoryId: prev.ingredientCategoryId,
                    },
                ],
            };
        });
    };

    const handleSaveProduct = async () => {
        const token = localStorage.getItem('authToken');
        if (!token) {
            alert('No auth token. Please login again.');
            return;
        }
        if (!businessId) {
            alert('Missing business id');
            return;
        }

        const payload = {
            productId: editingProduct.id,
            name: editingProduct.name,
            description: editingProduct.description,
            taxCategory: editingProduct.taxCategoryId,
            productType: editingProduct.productTypeId,
            businessId,
            basePrice: Math.max(0, Number(editingProduct.price) || 0),
            type: 'PRODUCT',
            status: editingProduct.status,
            // μόνο τα IDs πάνε στο backend
            ingredientIds: (editingProduct.ingredients || []).map(
                (it) => it.ingredientId,
            ),
        };

        const url = editingProduct.id
            ? `http://localhost:8080/api/products/${editingProduct.id}`
            : 'http://localhost:8080/api/products';
        const method = editingProduct.id ? 'PUT' : 'POST';

        try {
            const res = await fetch(url, {
                method,
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: 'Bearer ' + token,
                },
                body: JSON.stringify(payload),
            });

            if (!res.ok) {
                console.error('Save product failed', await res.text());
                alert('Failed to save product');
                return;
            }

            const listRes = await fetch(
                `http://localhost:8080/api/products?businessId=${businessId}`,
                { headers: { Authorization: 'Bearer ' + token } },
            );

            if (listRes.ok) {
                const data = await listRes.json();
                setProducts(
                    data.map((p) => ({
                        id: p.productId,
                        name: p.name,
                        category: p.productTypeName || 'Uncategorized',
                        description: p.description,
                        price: Number(p.basePrice),
                        status: p.status,
                        createdAt: p.createdAt,
                        updatedAt: p.updatedAt,
                        taxCategoryId: p.taxCategory,
                        productTypeId: p.productType,
                    })),
                );
            }
        } catch (e) {
            console.error('Save product error', e);
            alert('Network error while saving product');
        } finally {
            closeModal();
        }
    };

    const confirmDeleteProduct = (product) => {
        setProductToDelete(product);
        setDeleteModalOpen(true);
    };

    const handleDeleteConfirmed = async () => {
        if (!productToDelete) return;
        const token = localStorage.getItem('authToken');
        if (!token) {
            alert('No auth token. Please login again.');
            return;
        }
        if (!businessId) {
            alert('Missing business id');
            return;
        }

        try {
            const res = await fetch(
                `http://localhost:8080/api/products/${productToDelete.id}?businessId=${businessId}`,
                {
                    method: 'DELETE',
                    headers: { Authorization: 'Bearer ' + token },
                },
            );

            if (!res.ok) {
                console.error('Delete product failed', await res.text());
                alert('Failed to delete product');
                return;
            }

            setProducts((prev) => prev.filter((p) => p.id !== productToDelete.id));
            setDeleteModalOpen(false);
        } catch (e) {
            console.error('Delete product error', e);
            alert('Network error while deleting product');
        }
    };

    const filteredProducts = products
        .filter((p) =>
            p.name.toLowerCase().includes(filterText.toLowerCase().trim()),
        )
        .filter((p) =>
            filterCategory === 'ALL' ? true : p.category === filterCategory,
        )
        .filter((p) =>
            filterStatus === 'ALL' ? true : p.status === filterStatus,
        )
        .sort((a, b) => {
            if (sort === 'NAME_ASC') return a.name.localeCompare(b.name);
            if (sort === 'NAME_DESC') return b.name.localeCompare(a.name);
            return 0;
        });

    const categoryOptions = ['ALL', ...new Set(products.map((p) => p.category))];

    return (
        <div className="manage-product-page">
            <Header userData={userData} onLogout={onLogout} />
            <div className="page-header">
                <h2>Manage Product</h2>
                <button className="primary-btn" onClick={openAddModal}>
                    + Add Product
                </button>
            </div>

            <div className="filter-bar">
                <input
                    type="text"
                    placeholder="Search products..."
                    value={filterText}
                    onChange={(e) => setFilterText(e.target.value)}
                />
                <select
                    value={filterCategory}
                    onChange={(e) => setFilterCategory(e.target.value)}
                >
                    {categoryOptions.map((c) => (
                        <option key={c} value={c}>
                            {c === 'ALL' ? 'All Categories' : c}
                        </option>
                    ))}
                </select>
                <select
                    value={filterStatus}
                    onChange={(e) => setFilterStatus(e.target.value)}
                >
                    <option value="ALL">All Status</option>
                    <option value="ACTIVE">Active</option>
                    <option value="INACTIVE">Inactive</option>
                </select>
                <select value={sort} onChange={(e) => setSort(e.target.value)}>
                    <option value="NAME_ASC">Name A-Z</option>
                    <option value="NAME_DESC">Name Z-A</option>
                </select>
                <button
                    className="secondary-btn"
                    onClick={() => {
                        setFilterText('');
                        setFilterCategory('ALL');
                        setFilterStatus('ALL');
                        setSort('NAME_ASC');
                    }}
                >
                    Clear Filters
                </button>
            </div>

            <table className="product-table">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Category</th>
                    <th>Description</th>
                    <th>Price</th>
                    <th>Status</th>
                    <th>Created At</th>
                    <th>Updated At</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                {filteredProducts.map((p) => (
                    <tr key={p.id}>
                        <td>{p.name}</td>
                        <td>
                            <span className="pill purple">{p.category}</span>
                        </td>
                        <td>{p.description}</td>
                        <td>{p.price}</td>
                        <td>
                <span
                    className={
                        p.status === 'ACTIVE' ? 'pill green' : 'pill grey'
                    }
                >
                  {p.status === 'ACTIVE' ? 'Active' : 'Inactive'}
                </span>
                        </td>
                        <td>{p.createdAt}</td>
                        <td>{p.updatedAt}</td>
                        <td>
                            <button
                                className="icon-btn edit"
                                onClick={() => openEditModal(p)}
                            >
                                ✏️
                            </button>
                            <button
                                className="icon-btn delete"
                                onClick={() => confirmDeleteProduct(p)}
                            >
                                🗑️
                            </button>
                        </td>
                    </tr>
                ))}
                {filteredProducts.length === 0 && (
                    <tr>
                        <td colSpan="8" className="empty-row">
                            No products found.
                        </td>
                    </tr>
                )}
                </tbody>
            </table>

            {modalOpen && (
                <div className="modal-backdrop">
                    <div className="modal">
                        <h3>{editingProduct.id ? 'Edit Product' : 'Add Product'}</h3>

                        <label>
                            Product Name *
                            <input
                                type="text"
                                name="name"
                                value={editingProduct.name}
                                onChange={handleProductFieldChange}
                            />
                        </label>

                        <label>
                            Tax Category *
                            <select
                                name="taxCategoryId"
                                value={editingProduct.taxCategoryId}
                                onChange={handleProductFieldChange}
                            >
                                <option value="">Select tax category</option>
                                {taxCategories.map((t) => (
                                    <option key={t.id} value={t.id}>
                                        {t.name}
                                    </option>
                                ))}
                            </select>
                        </label>

                        <label>
                            Subcategory *
                            <select
                                name="productTypeId"
                                value={editingProduct.productTypeId}
                                onChange={handleProductFieldChange}
                            >
                                <option value="">Select subcategory</option>
                                {productTypes.map((t) => (
                                    <option key={t.id} value={t.id}>
                                        {t.name}
                                    </option>
                                ))}
                            </select>
                        </label>

                        <label>
                            Ingredient Category
                            <select
                                name="ingredientCategoryId"
                                value={editingProduct.ingredientCategoryId}
                                onChange={handleIngredientCategoryChange}
                            >
                                <option value="">Select ingredient category</option>
                                {ingredientCategories.map((c) => (
                                    <option key={c.id} value={c.id}>
                                        {c.name}
                                    </option>
                                ))}
                            </select>
                        </label>

                        {/* Εμφάνιση ΟΛΩΝ των επιλεγμένων ανά κατηγορία */}
                        {editingProduct.ingredients.length > 0 && (
                            <div className="selected-ingredients-groups">
                                {ingredientCategories.map((cat) => {
                                    const selectedInThisCategory =
                                        editingProduct.ingredients.filter(
                                            (it) =>
                                                String(it.categoryId) === String(cat.id),
                                        );
                                    if (selectedInThisCategory.length === 0) return null;
                                    return (
                                        <div
                                            key={cat.id}
                                            className="selected-ingredients-group"
                                        >
                                            <div className="selected-ingredients-category-name">
                                                {cat.name}
                                            </div>
                                            <div className="selected-ingredients-chips">
                                                {selectedInThisCategory.map((it) => (
                                                    <span
                                                        key={it.ingredientId}
                                                        className="chip selected chip-small"
                                                    >
                            {it.ingredientName}
                          </span>
                                                ))}
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>
                        )}

                        {editingProduct.ingredientCategoryId && (
                            <div className="ingredient-picker">
                                {ingredientsForSelectedCategory.map((ing) => (
                                    <button
                                        key={ing.id}
                                        type="button"
                                        className={
                                            editingProduct.ingredients.some(
                                                (it) => it.ingredientId === ing.id,
                                            )
                                                ? 'chip selected'
                                                : 'chip'
                                        }
                                        onClick={() => toggleIngredientSelection(ing)}
                                    >
                                        {ing.name}
                                    </button>
                                ))}
                            </div>
                        )}

                        <label>
                            Price *
                            <input
                                type="number"
                                step="0.01"
                                name="price"
                                value={editingProduct.price}
                                onChange={(e) => {
                                    const value = Math.max(0, Number(e.target.value));
                                    setEditingProduct((prev) => ({
                                        ...prev,
                                        price: value.toString(),
                                    }));
                                }}
                            />
                        </label>

                        <label>
                            Description
                            <textarea
                                name="description"
                                value={editingProduct.description}
                                onChange={handleProductFieldChange}
                            />
                        </label>

                        <div className="modal-actions">
                            <button className="secondary-btn" onClick={closeModal}>
                                Cancel
                            </button>
                            <button className="primary-btn" onClick={handleSaveProduct}>
                                {editingProduct.id ? 'Update' : 'Add'}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {deleteModalOpen && productToDelete && (
                <div className="modal-backdrop">
                    <div className="modal small">
                        <h3>Are you sure?</h3>
                        <p>
                            Are you sure you want to delete the product "
                            {productToDelete.name}"? This action cannot be undone.
                        </p>
                        <div className="modal-actions">
                            <button
                                className="secondary-btn"
                                onClick={() => setDeleteModalOpen(false)}
                            >
                                Cancel
                            </button>
                            <button
                                className="danger-btn"
                                onClick={handleDeleteConfirmed}
                            >
                                Delete
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ManageProducts;
