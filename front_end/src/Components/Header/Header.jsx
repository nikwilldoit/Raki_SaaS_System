// Header.jsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import './Header.css'; // Ensure the CSS for the header is available here

const Header = ({ userData, onLogout }) => {
    const {
        userName,
        role,
        businessType
    } = userData || {}; // Safety check: defaults to empty object if userData is null

    return (
        <header className="header">
            <div className="header-content">
                <h1>POS Dashboard</h1>
                <div className="user-info">
                    {/* We use conditional rendering just in case data is missing */}
                    <span>Welcome, <strong>{userName || 'User'}</strong></span>
                    <span>Role: {role || 'N/A'}</span>
                    <span>Business Type: {businessType || 'N/A'}</span>

                    <button onClick={onLogout} className="logout-btn">
                        Logout
                    </button>
                </div>
            </div>
        </header>
    );
};

export default Header;