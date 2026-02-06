// Dashboard.jsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import './Dashboard.css';
import Header from '../Header/Header';

const Dashboard = ({ userData, onLogout }) => {
    const navigate = useNavigate();

    if (!userData) {
        navigate('/login');
        return null;
    }

    //if he has no business send him to choose
    if (userData.role === 'SuperAdmin' && !userData.businessId) {
        navigate('/super-admin/select-business');
        return null;
    }

    const {
        userName,
        role,            // from employeeType in backend
        businessType,    // from backend
        superAdmin,
        businessId
    } = userData;

    const handleNavigation = (path) => {
        navigate(path);
    };

    // Role flags
    const isAdmin = superAdmin;
    const isManager = role === 'Owner';
    const isEmployee = role === 'Employee';

    // Business type flags
    const isFoodBusiness =
        businessType === 'RESTAURANT' ||
        businessType === 'CAFE' ||
        businessType === 'BAR';

    const isSalonBusiness =
        businessType === 'HAIRDRESSER' ||
        businessType === 'BARBERSHOP' ||
        businessType === 'SPA';

    // Permissions
    const canSeeOrders = isFoodBusiness && (isEmployee || isAdmin || isManager);
    const canSeeReservations = isSalonBusiness && (isEmployee || isAdmin || isManager);
    const canCreateReservations = canSeeReservations;
    const canManageMenu = (isAdmin || isManager) && isFoodBusiness;
    const canManageStaff = isManager || isAdmin;
    const canManageTaxes = isAdmin;
    const canSeeOrderList = isFoodBusiness && (isAdmin || isManager|| isEmployee);
    const canManageDiscounts  = isAdmin || isManager;
    const canManageBusiness = isManager || isAdmin;

    return (
        <div className="dashboard">
            
            <Header userData={userData} onLogout={onLogout} />
            <div className="dashboard-content">
                <div className="action-grid">

                    {canSeeOrders && (
                        <div className="action-card" onClick={() => handleNavigation('/orders')}>
                            <div className="action-icon">🛒</div>
                            <h3>Order Management</h3>
                        </div>
                    )}

                    {canSeeReservations && (
                        <div className="action-card" onClick={() => handleNavigation('/reservations')}>
                            <div className="action-icon">📅</div>
                            <h3>Reservations</h3>
                        </div>
                    )}
                    {canCreateReservations && (
                        <div className="action-card" onClick={() => handleNavigation('/reservations/new')}>
                            <div className="action-icon">➕</div>
                            <h3>Create Reservation</h3>
                        </div>
                    )}

                    {canManageMenu && (
                        <div className="action-card" onClick={() => handleNavigation('/manage/menu')}>
                            <div className="action-icon">📋</div>
                            <h3>Menu Management</h3>
                            <p>
                                'Add Products, Categories'
                            </p>
                        </div>
                    )}

                    {canManageStaff && (
                        <div className="action-card" onClick={() => handleNavigation('/manage/employees')}>
                            <div className="action-icon">👥</div>
                            <h3>Staff Management</h3>
                        </div>
                    )}

                    {canManageTaxes && (
                        <div className="action-card" onClick={() => handleNavigation('/manage/taxes')}>
                            <div className="action-icon">💰</div>
                            <h3>Tax Management</h3>
                        </div>
                    )}

                    {canSeeOrderList && (
                        <div className="action-card" onClick={() => handleNavigation('/order-list')}>
                            <div className="action-icon">📊</div>
                            <h3>Orders List</h3>
                        </div>
                    )}

                    {canManageDiscounts && (
                        <div className="action-card" onClick={() => handleNavigation('/manage/discounts')}>
                            <div className="action-icon">📊</div>
                            <h3>Discount Management</h3>
                        </div>
                    )}

                    {canManageBusiness && (
                        <div className="action-card" onClick={() => handleNavigation('/manage/business')}>
                            <div className="action-icon">📊</div>
                            <h3>Business Management</h3>
                        </div>
                    )}

                </div>
            </div>
        </div>
    );
};

export default Dashboard;
