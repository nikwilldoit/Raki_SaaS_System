// src/Components/manage/MenuDashboard.jsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import './MenuDashboard.css';
import Header from "../Header/Header";

const MenuDashboard = ({ userData, onLogout }) => {
    const navigate = useNavigate();
    const { businessType } = userData || {};

    const goToProducts = () => navigate('/manage/menu/products');
    const goToCategories = () => navigate('/manage/menu/categories');

    const isFoodBusiness =
        businessType === 'RESTAURANT' ||
        businessType === 'CAFE' ||
        businessType === 'BAR';

    return (
        <div className="menu-dashboard">

            <Header userData={userData} onLogout={onLogout} />
            <div className="menu-dashboard-header">
                <h2>Menu Management</h2>
                <p>
                    {isFoodBusiness
                        ? 'Manage products and ingredient categories for your menu.'
                        : 'Manage services and options for your business.'}
                </p>
            </div>

            <div className="menu-dashboard-grid">
                <div className="menu-dashboard-card" onClick={goToProducts}>
                    <h3>Products</h3>
                    <p>Add, edit, and delete products.</p>
                    <button>Manage Products</button>
                </div>

                <div className="menu-dashboard-card" onClick={goToCategories}>
                    <h3>Ingredient Categories</h3>
                    <p>Create ingredient categories and ingredients.</p>
                    <button>Manage Categories</button>
                </div>
            </div>
        </div>
    );
};

export default MenuDashboard;
