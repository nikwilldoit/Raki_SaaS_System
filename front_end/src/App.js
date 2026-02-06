// App.js
import React, { useState, useEffect } from 'react';
import {
    BrowserRouter as Router,
    Routes,
    Route,
    Navigate,
    useNavigate,
} from 'react-router-dom';

import './App.css';

import LoginPage from './Components/loginPage/LoginPage';
import Dashboard from './Components/Dashboard/Dashboard';
import MenuDashboard from './Components/manage/MenuDashboard';
import ManageProducts from './Components/manage/ManageProducts';
import ManageIngredientCategories from './Components/manage/ManageIngredientCategories';
import OrderOverview from './Components/orders/OrderOverview';
import ManageDiscounts from './Components/discounts/ManageDiscounts';
import ManageTaxPolicies from './Components/taxes/ManageTaxPolicies';
import OrderList from './Components/orders/OrderList';
import PaymentSummary from './Components/payment/PaymentSummary';
import PaymentSplit from './Components/payment/PaymentSplit';
import PaymentOverview from './Components/payment/PaymentOverview';
import ReservationCreatePage from './Components/Reservation/ReservationCreatePage/ReservationCreatePage';
import ReservationListPage from './Components/Reservation/ReservationViewList/ReservationListPage';
import ManageStaff from './Components/staff/ManageStaff';
import ManageBusiness from './Components/Business/ManageBusiness';
import SuperAdminSelectBusiness from './Components/admin/SuperAdminSelectBusiness';


const AppWrapper = () => {
    const [userData, setUserData] = useState(null);
    const navigate = useNavigate();

    // Load user from localStorage when page refreshes
    useEffect(() => {
        const savedUser = localStorage.getItem('user');
        if (savedUser) setUserData(JSON.parse(savedUser));
    }, []);

    const handleLogin = (data) => {
        setUserData(data);
        localStorage.setItem('user', JSON.stringify(data));

        if (data.role === 'SuperAdmin') {
            navigate('/super-admin/select-business');
        } else {
            navigate('/dashboard');
        }
    };

    const handleLogout = () => {
        setUserData(null);

        localStorage.removeItem('user');
        localStorage.removeItem('authToken');
        navigate('/login');
    };

    const ProtectedRoute = ({ children }) => {
        if (!userData) {
            return <Navigate to="/login" replace />;
        }
        return children;
    };

    return (
        <Routes>
            <Route path="/" element={<Navigate to="/login" replace />} />

            <Route
                path="/login"
                element={
                    userData ? (
                        <Navigate
                            to={
                                userData.role === 'SuperAdmin'
                                    ? '/super-admin/select-business'
                                    : '/dashboard'
                            }
                            replace
                        />
                    ) : (
                        <LoginPage onLogin={handleLogin} />
                    )
                }
            />

            <Route
                path="/super-admin/select-business"
                element={
                    <ProtectedRoute>
                        <SuperAdminSelectBusiness
                            userData={userData}
                            setUserData={setUserData}
                            onLogout={handleLogout}
                        />
                    </ProtectedRoute>
                }
            />


            <Route
                path="/dashboard"
                element={
                    <ProtectedRoute>
                        <Dashboard userData={userData} onLogout={handleLogout} />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/manage/menu"
                element={
                    <ProtectedRoute>
                        <MenuDashboard userData={userData} onLogout={handleLogout} />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/manage/business"
                element={
                    <ProtectedRoute>
                        <ManageBusiness userData={userData} onLogout={handleLogout} />
                    </ProtectedRoute>
                }
            />

            <Route
                    path="/manage/menu/products"
                    element={
                        <ProtectedRoute>
                            <ManageProducts userData={userData} onLogout={handleLogout} />
                        </ProtectedRoute>
                    }
            />

            <Route
                path="/manage/menu/categories"
                element={
                    <ProtectedRoute>
                        <ManageIngredientCategories
                            userData={userData}
                            onLogout={handleLogout}
                        />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/orders"
                element={
                    <ProtectedRoute>
                        <OrderOverview userData={userData} onLogout={handleLogout} />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/reservations"
                element={
                    <ProtectedRoute>
                        <ReservationListPage userData={userData} onLogout={handleLogout} />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/reservations/new"
                element={
                    <ProtectedRoute>
                        <ReservationCreatePage userData={userData} onLogout={handleLogout} />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/order-list"
                element={
                    <ProtectedRoute>
                        <OrderList userData={userData} onLogout={handleLogout} />
                    </ProtectedRoute>
                }
            />


            <Route
                path="/manage/discounts"
                element={
                    <ProtectedRoute>
                        <ManageDiscounts userData={userData} onLogout={handleLogout} />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/manage/taxes"
                element={
                    <ProtectedRoute>
                        <ManageTaxPolicies userData={userData} onLogout={handleLogout} />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/orders/:orderId/payment"
                element={
                    <ProtectedRoute>
                        <PaymentSummary />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/orders/:orderId/payment/split"
                element={
                    <ProtectedRoute>
                        <PaymentSplit />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/orders/:orderId/payments"
                element={
                    <ProtectedRoute>
                        <PaymentOverview />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/manage/employees"
                element={
                    <ProtectedRoute>
                        <ManageStaff userData={userData} onLogout={handleLogout} />
                    </ProtectedRoute>
                }
            />

            <Route
                path="*"
                element={
                    <Navigate to={userData ? '/dashboard' : '/login'} replace />
                }
            />

        </Routes>
    );
};

function App() {
    return (
        <Router>
            <AppWrapper />
        </Router>
    );
}

export default App;
