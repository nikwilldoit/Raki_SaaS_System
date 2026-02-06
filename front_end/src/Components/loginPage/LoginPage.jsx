// LoginPage.jsx
import React, { useState } from "react";
import "./LoginPage.css";
import email_photo from "../Assets/email.png";
import password_photo from "../Assets/password.png";

const LoginPage = ({ onLogin }) => {
    const [formData, setFormData] = useState({ email: "", password: "" });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            // 1) LOGIN
            const response = await fetch('http://localhost:8080/api/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData),

            });

            const data = await response.json();

            if (!response.ok || !data.token) {
                setError('Invalid credentials');
                setLoading(false);
                return;
            }

            // Save token
            localStorage.setItem('authToken', data.token);

            // 2) FETCH DASHBOARD INFO
            const dashResp = await fetch('http://localhost:8080/api/dashboard', {
                method: 'GET',
                headers: { Authorization: 'Bearer ' + data.token },
            });

            const dashData = await dashResp.json();

            if (!dashResp.ok) {
                setError('Unauthorized dashboard access');
                setLoading(false);
                return;
            }

            // 3) Construct final user object
            const userData = {
                userId: dashData.userId,
                userName: data.username,          // από login
                role: dashData.employeeType,      // από dashboard
                businessType: dashData.businessType,
                superAdmin: dashData.superAdmin,
                businessId: dashData.businessId,  // ΠΡΟΣΟΧΗ: από dashboard
            };

            // 4) Save to storage & lift state
            localStorage.setItem('user', JSON.stringify(userData));
            if (onLogin) onLogin(userData);
        } catch (err) {
            console.error('Login error:', err);
            setError('Network error. Try again.');
        } finally {

            setLoading(false);
        }
    };

    return (
        <div className="login-container">
            <div className="login-header">
                <div className="text"> Login </div>
                <div className="login-underline"></div>
            </div>

            <form className="login-form" onSubmit={handleSubmit}>
                <div className="input-group">
                    <img src={email_photo} alt="Email Icon" />
                    <input
                        type="email"
                        name="email"
                        placeholder="Email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="input-group">
                    <img src={password_photo} alt="Password Icon" />
                    <input
                        type="password"
                        name="password"
                        placeholder="Password"
                        value={formData.password}
                        onChange={handleChange}
                        required
                    />
                </div>

                {error && <div className="error-message">{error}</div>}

                <div className="login-button-container">
                    <button type="submit" className="login-button" disabled={loading}>
                        {loading ? "Logging in..." : "Login"}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default LoginPage;
