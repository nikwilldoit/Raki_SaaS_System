// src/Components/manage/ManageIngredientCategories.jsx
import React, { useState, useEffect } from 'react';
import './ManageIngredientCategories.css';
import Header from "../Header/Header";

const emptyCategory = {
    id: null,
    name: '',
    description: '',
    ingredients: [{ name: '', price: '' }],
};

const ManageIngredientCategories = ({ userData, onLogout }) => {
    const [categories, setCategories] = useState([]);
    const [filter, setFilter] = useState('');
    const [sort, setSort] = useState('NAME_ASC');
    const [loading, setLoading] = useState(false);

    const [modalOpen, setModalOpen] = useState(false);
    const [deleteModalOpen, setDeleteModalOpen] = useState(false);
    const [editingCategory, setEditingCategory] = useState(emptyCategory);
    const [categoryToDelete, setCategoryToDelete] = useState(null);

    const businessId = userData?.businessId;

    console.log('userData in ManageIngredientCategories', userData);
    console.log('businessId', businessId);

    // ============= Load list from backend =============
    useEffect(() => {
        if (!businessId) return;
        const token = localStorage.getItem('authToken');
        if (!token) return;

        fetch(
            `http://localhost:8080/api/menu/ingredient-categories?businessId=${businessId}`,
            { headers: { Authorization: 'Bearer ' + token } }
        )
            .then((r) => r.json())
            .then((data) => {
                setCategories(
                    data.map((c) => ({
                        id: c.categoryId,
                        name: c.name,
                        description: c.description,
                        ingredientsCount: c.ingredientsCount
                    }))
                );
            })
            .catch((err) => console.error('Failed to load ingredient categories', err));
    }, [businessId]);

    const openAddModal = () => {
        setEditingCategory(emptyCategory);
        setModalOpen(true);
    };

    const openEditModal = (category) => {
        const token = localStorage.getItem('authToken');
        if (!token) return;

        // Φέρε τα ingredients της κατηγορίας
        fetch(
            `http://localhost:8080/api/menu/ingredient-categories/${category.id}/ingredients`,
            { headers: { Authorization: 'Bearer ' + token } }
        )
            .then((r) => r.json())
            .then((data) => {
                setEditingCategory({
                    id: category.id,
                    name: category.name,
                    description: category.description,
                    ingredients:
                        data.length > 0
                            ? data.map((ing) => ({
                                id: ing.ingredientId,
                                name: ing.name,
                                price: String(ing.price),
                            }))
                            : [{ name: '', price: '' }],
                });
                setModalOpen(true);
            })
            .catch(() => {
                // αν κάτι πάει στραβά, άνοιξε modal μόνο με την κατηγορία
                setEditingCategory({
                    id: category.id,
                    name: category.name,
                    description: category.description,
                    ingredients: [{ name: '', price: '' }],
                });
                setModalOpen(true);
            });
    };

    const closeModal = () => {
        setModalOpen(false);
        setEditingCategory(emptyCategory);
    };

    const handleCategoryFieldChange = (e) => {
        const { name, value } = e.target;
        setEditingCategory((prev) => ({ ...prev, [name]: value }));
    };

    const handleIngredientChange = (index, field, value) => {
        setEditingCategory((prev) => {
            const ingredients = [...prev.ingredients];

            let finalValue = value;
            if (field === 'price') {
                finalValue = Math.max(0, Number(value) || 0).toString();
            }

            ingredients[index] = {
                ...ingredients[index],
                [field]: finalValue,
            };

            return { ...prev, ingredients };
        });
    };


    const addIngredientRow = () => {
        setEditingCategory((prev) => ({
            ...prev,
            ingredients: [...prev.ingredients, { name: '', price: '' }],
        }));
    };

    const removeIngredientRow = (index) => {
        setEditingCategory((prev) => ({
            ...prev,
            ingredients: prev.ingredients.filter((_, i) => i !== index),
        }));
    };

    // ============= Save (POST / PUT) =============
    const handleSaveCategory = async () => {
        if (!businessId) {
            alert('Missing business id');
            return;
        }
        const token = localStorage.getItem('authToken');
        if (!token) {
            alert('No auth token. Please login again.');
            return;
        }

        setLoading(true);
        try {
            const payload = {
                categoryId: editingCategory.id,
                businessId,
                name: editingCategory.name,
                description: editingCategory.description,
                ingredients: editingCategory.ingredients
                    .filter((ing) => ing.name.trim() !== '')
                    .map((ing) => ({
                        ingredientId: ing.id,
                        name: ing.name,
                        price: Math.max(0, Number(ing.price) || 0),
                    })),
            };

            const url = editingCategory.id
                ? `http://localhost:8080/api/menu/ingredient-categories/${editingCategory.id}`
                : 'http://localhost:8080/api/menu/ingredient-categories';
            const method = editingCategory.id ? 'PUT' : 'POST';

            const res = await fetch(url, {
                method,
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: 'Bearer ' + token,
                },
                body: JSON.stringify(payload),
            });

            if (!res.ok) {
                console.error('Save category failed', await res.text());
                alert('Failed to save category');
                return;
            }

            // refetch without reload
            await fetch(
                `http://localhost:8080/api/menu/ingredient-categories?businessId=${businessId}`,
                { headers: { Authorization: 'Bearer ' + token } }
            )
                .then((r) => r.json())
                .then((data) => {
                    setCategories(
                        data.map((c) => ({
                            id: c.categoryId,
                            name: c.name,
                            description: c.description,
                            ingredientsCount: c.ingredientsCount,
                        }))
                    );
                });
            closeModal();
        } catch (e) {
            console.error('Save category error', e);
            alert('Network error while saving category');
        } finally {
            setLoading(false);
        }
    };

    const confirmDeleteCategory = (category) => {
        setCategoryToDelete(category);
        setDeleteModalOpen(true);
    };

    // ============= Delete =============
    const handleDeleteConfirmed = async () => {
        if (!categoryToDelete) return;
        const token = localStorage.getItem('authToken');
        if (!token) {
            alert('No auth token. Please login again.');
            return;
        }
        if (!businessId) {
            alert('Missing business id');
            return;
        }

        setLoading(true);
        try {
            const res = await fetch(
                `http://localhost:8080/api/menu/ingredient-categories/${categoryToDelete.id}?businessId=${businessId}`,
                {
                    method: 'DELETE',
                    headers: { Authorization: 'Bearer ' + token },
                }
            );

            if (!res.ok) {
                console.error('Delete category failed', await res.text());
                alert('Failed to delete category');
                return;
            }

            setCategories((prev) => prev.filter((c) => c.id !== categoryToDelete.id));
            setDeleteModalOpen(false);
        } catch (e) {
            console.error('Delete category error', e);
            alert('Network error while deleting category');
        } finally {
            setLoading(false);
        }
    };


    const filteredCategories = categories
        .filter((c) =>
            c.name.toLowerCase().includes(filter.toLowerCase().trim())
        )
        .sort((a, b) => {
            if (sort === 'NAME_ASC') return a.name.localeCompare(b.name);
            if (sort === 'NAME_DESC') return b.name.localeCompare(a.name);
            return 0;
        });

    return (
        <div className="manage-category-page">
            <Header userData={userData} onLogout={onLogout} />
            <div className="page-header">
                <h2>Manage Category</h2>
                <button className="primary-btn" onClick={openAddModal}>
                    + Add Category
                </button>
            </div>

            <div className="filter-bar">
                <input
                    type="text"
                    placeholder="Search categories..."
                    value={filter}
                    onChange={(e) => setFilter(e.target.value)}
                />
                <select value={sort} onChange={(e) => setSort(e.target.value)}>
                    <option value="NAME_ASC">Name A-Z</option>
                    <option value="NAME_DESC">Name Z-A</option>
                </select>
                <button
                    className="secondary-btn"
                    onClick={() => {
                        setFilter('');
                        setSort('NAME_ASC');
                    }}
                >
                    Clear Filters
                </button>
            </div>

            <table className="category-table">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Ingredients</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                {filteredCategories.map((c) => (
                    <tr key={c.id}>
                        <td>{c.name}</td>
                        <td>{c.description}</td>
                        <td>
                            <span className="pill green">{c.ingredientsCount}</span>
                        </td>
                        <td>
                            <button
                                className="icon-btn edit"
                                onClick={() => openEditModal(c)}
                            >
                                ✏️
                            </button>
                            <button
                                className="icon-btn delete"
                                onClick={() => confirmDeleteCategory(c)}
                            >
                                🗑️
                            </button>
                        </td>
                    </tr>
                ))}
                {filteredCategories.length === 0 && (
                    <tr>
                        <td colSpan="7" className="empty-row">
                            No categories found.
                        </td>
                    </tr>
                )}
                </tbody>
            </table>

            {modalOpen && (
                <div className="modal-backdrop">
                    <div className="modal">
                        <h3>{editingCategory.id ? 'Edit Category' : 'Add Category'}</h3>

                        <label>
                            Category Name *
                            <input
                                type="text"
                                name="name"
                                value={editingCategory.name}
                                onChange={handleCategoryFieldChange}
                            />
                        </label>

                        <label>
                            Description
                            <textarea
                                name="description"
                                value={editingCategory.description}
                                onChange={handleCategoryFieldChange}
                            />
                        </label>

                        <div className="ingredients-header">
                            <span>Ingredients</span>
                            <button
                                type="button"
                                className="secondary-btn"
                                onClick={addIngredientRow}
                            >
                                + Add Ingredient
                            </button>
                        </div>

                        <div className="ingredients-list">
                            {editingCategory.ingredients.map((ing, index) => (
                                <div key={index} className="ingredient-row">
                                    <input
                                        type="text"
                                        placeholder="Ingredient name"
                                        value={ing.name}
                                        onChange={(e) =>
                                            handleIngredientChange(index, 'name', e.target.value)
                                        }
                                    />
                                    <input
                                        type="number"
                                        step="0.01"
                                        placeholder="Price"
                                        value={ing.price}
                                        onChange={(e) => {
                                            const value = Math.max(0, Number(e.target.value));
                                            handleIngredientChange(index, 'price', value.toString());
                                        }}
                                    />
                                    <button
                                        type="button"
                                        className="icon-btn delete"
                                        onClick={() => removeIngredientRow(index)}
                                    >
                                        −
                                    </button>
                                </div>
                            ))}
                        </div>

                        <div className="modal-actions">
                            <button className="secondary-btn" onClick={closeModal}>
                                Cancel
                            </button>
                            <button
                                className="primary-btn"
                                onClick={handleSaveCategory}
                                disabled={loading}
                            >
                                {editingCategory.id ? 'Update' : 'Add'}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {deleteModalOpen && categoryToDelete && (
                <div className="modal-backdrop">
                    <div className="modal small">
                        <h3>Are you sure?</h3>
                        <p>
                            Are you sure you want to delete the category "
                            {categoryToDelete.name}"? This action cannot be undone.
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
                                disabled={loading}
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

export default ManageIngredientCategories;
