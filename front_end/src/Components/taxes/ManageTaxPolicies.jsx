import React, { useState } from "react";
import "./ManageTaxPolicies.css";
import Header from "../Header/Header";

const TAX_TYPE_OPTIONS = [
    { value: "STANDARD", label: "STANDARD" },
    { value: "REDUCED", label: "REDUCED" },
    { value: "ZERO", label: "ZERO" },
];

export default function ManageTaxPolicies({ userData, onLogout }) {
    const [name, setName] = useState("");
    const [rate, setRate] = useState("");
    const [taxType, setTaxType] = useState(TAX_TYPE_OPTIONS[0].value);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState(null);
    const [error, setError] = useState(null);

    const reset = () => {
        setName("");
        setRate("");
        setTaxType(TAX_TYPE_OPTIONS[0].value);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage(null);
        setError(null);

        if (!name.trim()) {
            setError("Name is required.");
            return;
        }

        const parsedRate = parseFloat(rate);
        if (Number.isNaN(parsedRate) || parsedRate < 0 || parsedRate > 100) {
            setError("Rate must be between 0 and 100.");
            return;
        }

        setLoading(true);
        try {
            const token = localStorage.getItem("authToken");

            const res = await fetch("http://localhost:8080/api/taxes", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    ...(token ? { Authorization: "Bearer " + token } : {}),
                },
                body: JSON.stringify({
                    name: name.trim(),
                    rate: parsedRate,
                    taxType,
                }),
            });

            if (!res.ok) {
                const txt = await res.text().catch(() => "");
                throw new Error(txt || "Failed");
            }

            setMessage("Tax policy saved!");
            reset();
        } catch (err) {
            console.error(err);
            setError("Failed to save tax: " + err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="tax-page">

            {/* ✅ Header όπως στο MenuDashboard */}
            <Header userData={userData} onLogout={onLogout} />

            {/* Title Area */}
            <div className="menu-dashboard-header">
                <h2>Tax Policies</h2>
                <p>Create and manage the tax policies for your business.</p>
            </div>

            {/* Form Card */}
            <main className="tax-form-card">
                <form className="tax-form" onSubmit={handleSubmit}>
                    <label className="field-label">
                        Name
                        <input
                            className="field-input"
                            type="text"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            placeholder="e.g. VAT 24%"
                            required
                        />
                    </label>

                    <label className="field-label">
                        Rate (%)
                        <input
                            className="field-input"
                            type="number"
                            step="0.01"
                            min="0"
                            max="100"
                            value={rate}
                            onChange={(e) => setRate(e.target.value)}
                            placeholder="e.g. 24"
                            required
                        />
                    </label>

                    <label className="field-label">
                        Tax Type
                        <select
                            className="field-input"
                            value={taxType}
                            onChange={(e) => setTaxType(e.target.value)}
                        >
                            {TAX_TYPE_OPTIONS.map((opt) => (
                                <option key={opt.value} value={opt.value}>
                                    {opt.label}
                                </option>
                            ))}
                        </select>
                    </label>

                    <div className="tax-form-actions">
                        <button type="submit" className="btn-primary" disabled={loading}>
                            {loading ? "Saving..." : "Save"}
                        </button>

                        <button
                            type="button"
                            className="btn-ghost"
                            onClick={reset}
                            disabled={loading}
                        >
                            Reset
                        </button>
                    </div>

                    {message && <div className="form-message success">{message}</div>}
                    {error && <div className="form-message error">{error}</div>}
                </form>
            </main>
        </div>
    );
}
